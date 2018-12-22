package com.mercu.bricklink.service;

import com.mercu.bricklink.BrickLinkUrlUtils;
import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.lego.model.my.MyItem;
import com.mercu.lego.model.my.MyItemGroup;
import com.mercu.bricklink.repository.map.SetItemRepository;
import com.mercu.lego.repository.my.MyItemRepository;
import com.mercu.lego.model.match.MatchMyItemSetItem;
import com.mercu.lego.model.match.MatchMyItemSetItemRatio;
import com.mercu.lego.repository.MatchMyItemSetItemRatioRepository;
import com.mercu.lego.repository.MatchMyItemSetItemRepository;
import com.mercu.log.LogService;
import com.mercu.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.mercu.lego.model.my.MyItem.*;
import static java.util.stream.Collectors.*;

@Service
public class BrickLinkMyService {

    @Autowired
    private BrickLinkSimilarService brickLinkSimilarService;
    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;
    @Autowired
    private BrickLinkColorService brickLinkColorService;

    @Autowired
    private MyItemRepository myItemRepository;
    @Autowired
    private SetItemRepository setItemRepository;
    @Autowired
    private MatchMyItemSetItemRepository matchMyItemSetItemRepository;
    @Autowired
    private MatchMyItemSetItemRatioRepository matchMyItemSetItemRatioRepository;

    @Autowired
    private LogService logService;

    /**
     * @return
     */
    public List<MyItem> findMyItems() {
        Pageable pageable = new PageRequest(0, 500);
        return myItemRepository.findList(pageable);
    }

    /**
     *
     * @param itemType
     * @param itemNo
     * @return
     */
    public List<MyItem> findMyItems(String itemType, String itemNo) {
        return myItemRepository.findList(itemType, itemNo);
    }

    /**
     * @return
     */
    public List<MyItemGroup> findMyItemsGroup() {
        return findMyItemsGroup(findMyItems());
    }
    public List<MyItemGroup> findMyItemsGroup(List<MyItem> myItemList) {
        return myItemList.stream()
                .collect(
                        groupingBy(myItem -> new MyItem(myItem.getItemType(), myItem.getItemNo(), myItem.getColorId()),
                                toList())
                ).entrySet().stream()
                .map(entry -> myItemGroup(entry.getValue()))
                .collect(toList());
    }

    public MyItemGroup myItemGroup(List<MyItem> myItemList) {
        MyItem firstItem = myItemList.get(0);

        MyItemGroup myItemGroup = new MyItemGroup();
        myItemGroup.setItemType(firstItem.getItemType());
        myItemGroup.setItemNo(firstItem.getItemNo());
        myItemGroup.setColorId(firstItem.getColorId());
        myItemGroup.setQty(myItemList.stream()
                .mapToInt(MyItem::getQty)
                .sum());
        myItemGroup.setMyItems(myItemList);
        myItemGroup.setColorCode(brickLinkColorService.findColorById(firstItem.getColorId()).getColorCode());
        myItemGroup.setRepImgOriginal(brickLinkCatalogService.findPartByPartNo(firstItem.getItemNo()).getImg());
        myItemGroup.setRepImg(
                UrlUtils.replaceLastPath(
                        myItemGroup.getRepImgOriginal(),
                        firstItem.getColorId()));
        return myItemGroup;
    }

    public List<MyItem> findMyItemWheres(String itemType, String itemNo, String colorId) {
        return findMyItemWheres(itemType, itemNo, colorId, null);
    }

    /**
     * @param itemType
     * @param itemNo
     * @param colorId
     * @param setNo nullable
     * @return
     */
    public List<MyItem> findMyItemWheres(String itemType, String itemNo, String colorId, String setNo) {
        List<MyItem> myItemList = myItemRepository.findList(itemType, itemNo, colorId);

        // wanted 보관소(whereMore-setNo) 값 추가하기
        if (Objects.nonNull(setNo) && containsWhere(myItemList, WHERE_CODE_WANTED, setNo) == false) {
            myItemList = addMyItemWhereForward(itemType, itemNo, colorId, WHERE_CODE_WANTED, setNo, myItemList);
        }
        // 기본 보관소(storage) 값 추가하기
        if (containsWhere(myItemList, WHERE_CODE_STORAGE) == false) {
            myItemList = addMyItemWhereForward(itemType, itemNo, colorId, WHERE_CODE_STORAGE, WHERE_CODE_STORAGE, myItemList);
            myItemList = addMyItemWhereForward(itemType, itemNo, colorId, WHERE_CODE_STORAGE, WHERE_CODE_TEMPORARY, myItemList);
        }

        myItemList.stream()
                .forEach(myItem -> {
                    // 부품 정보
                    myItem.setPartInfo(brickLinkCatalogService.findPartByPartNo(myItem.getItemNo()));
                    // 색상 정보
                    myItem.setColorInfo(brickLinkColorService.findColorById(myItem.getColorId()));
                    // 이미지URL
                    myItem.setImgUrl(BrickLinkUrlUtils.partImageUrl(myItem.getItemNo(), myItem.getColorId()));
                    // 매칭율
                    if (MyItem.WHERE_CODE_WANTED.equals(myItem.getWhereCode())) {
                        myItem.setMatchMyItemSetItemRatio(matchMyItemSetItemRatioRepository.findRecentlyOneBySetNo(myItem.getWhereMore()));
                    }
                });

        return myItemList;
    }

    /**
     * 부품 단건에 대해 보유 목록 리스팅하고(유사포함)
     * @param itemType
     * @param itemNo
     * @param colorId
     * @param setNo nullable
     * @return
     */
    public List<MyItem> findMyItemWheresSimilar(String itemType, String itemNo, String colorId, String setNo) {
        List<MyItem> myItemList = new ArrayList<>();

        // 유사 아이템 목록
        brickLinkSimilarService.findPartNos(itemNo).stream()
                .forEach(partNo -> {
                    myItemList.addAll(findMyItemWheres(itemType, partNo, colorId, setNo));
                });

        return myItemList;
    }

    private List<MyItem> addMyItemWhereForward(String itemType, String itemNo, String colorId, String whereCode, String whereMore, List<MyItem> myItemList) {
        List<MyItem> newMyItemList = new ArrayList<>();

        MyItem newMyItem = new MyItem();
        newMyItem.setItemType(itemType);
        newMyItem.setItemNo(itemNo);
        newMyItem.setColorId(colorId);
        newMyItem.setWhereCode(whereCode);
        newMyItem.setWhereMore(whereMore);
        newMyItem.setQty(0);

        newMyItemList.add(newMyItem);
        newMyItemList.addAll(myItemList);

        return newMyItemList;
    }

    private boolean containsWhere(List<MyItem> myItemList, String whereCode) {
        for (MyItem myItem : myItemList) {
            if (StringUtils.equals(myItem.getWhereCode(), whereCode)) return true;
        }
        return false;
    }

    private boolean containsWhere(List<MyItem> myItemList, String whereCode, String whereMore) {
        for (MyItem myItem : myItemList) {
            if (StringUtils.equals(myItem.getWhereCode(), whereCode)
                    && StringUtils.equals(myItem.getWhereMore(), whereMore)) return true;
        }
        return false;
    }

    /**
     * @param itemType
     * @param itemNo
     * @param colorId
     * @param whereCode
     * @param whereMore
     * @param qty
     * @return
     */
    public MyItem addMyItem(String itemType, String itemNo, String colorId, String whereCode, String whereMore, Integer qty) {
        return myItemRepository.save(new MyItem(itemType, itemNo, colorId, whereCode, whereMore, qty));
    }

    /**
     * 부품-단건 보유 수량(양수/음수) 변경 후 갱신된 목록 리스트 반환
     * @param itemType
     * @param itemNo
     * @param colorId
     * @param whereCode
     * @param whereMore
     * @param val
     * @return
     */
    public List<MyItem> increaseMyPartWhere(String itemType, String itemNo, String colorId, String whereCode, String whereMore, Integer val, String setNo) {
        // 부품-단건 보유 수량(양수/음수) 변경
        MyItem myItem = myItemRepository.findByIdWhere(itemType, itemNo, colorId, whereCode, whereMore);
        // 값이 없을 시 초기화
        if (Objects.isNull(myItem)) {
            myItem = new MyItem(itemType, itemNo, colorId, whereCode, whereMore, 0);
        }
        // 수량 변경 - 음수인 경우엔 통과
        if (myItem.getQty() + val >= 0) {
            myItem.setQty(myItem.getQty() + val);
            myItemRepository.save(myItem);
        }

        // 목록 리스트 반환
        return findMyItemWheresSimilar(itemType, itemNo, colorId, setNo);
    }

    /**
     * @param setNo
     */
    public void addMyListBySetNo(String setNo, String whereCode) {
        logService.log("addMyListBySetNo", "add start - setNo : " + setNo);
        // setItemList 가져오기
        List<SetItem> setItemList = setItemRepository.findBySetNo(setNo);
        System.out.println(setItemList);

        // myItem에 추가하기
        int index = 0;
        for (SetItem setItem : setItemList) {
            index++;
            logService.log("addMyListBySetNo", "add item : " + setItem, index + "/" + setItemList.size());

            MyItem myItem = myItemRepository.findById(CategoryType.P.getCode(), setItem.getItemNo(), setItem.getColorId(), whereCode);
            if (Objects.nonNull(myItem)) {
                myItem.setQty(myItem.getQty() + setItem.getQty());
            } else {
                myItem = new MyItem();
                myItem.setItemType(CategoryType.P.getCode());
                myItem.setItemNo(setItem.getItemNo());
                myItem.setColorId(setItem.getColorId());
                myItem.setQty(setItem.getQty());
                myItem.setWhereCode(whereCode);
            }

            myItemRepository.save(myItem);
        }
        logService.log("addMyListBySetNo", "add finish - setNo : " + setNo);
    }

    /**
     * @param matchId
     */
    public void mapMyItemToSet(String matchId) {
        logService.log("mapMyItemToSet", "map start - matchId : " + matchId);
        // myItemList 조회하기
        List<MyItem> myItemList = ((List<MyItem>)myItemRepository.findAll()).stream()
                .collect(
                        groupingBy(myItem -> new MyItem(myItem.getItemType(), myItem.getItemNo(), myItem.getColorId()),
                                summingInt(MyItem::getQty))
                ).entrySet().stream()
                .map(entry -> {
                    MyItem myItem = new MyItem(entry.getKey().getItemType(), entry.getKey().getItemNo(), entry.getKey().getColorId());
                    myItem.setQty(entry.getValue());
                    return myItem;
                }).collect(toList());
        logService.log("mapMyItemToSet", "myItemList.size : " + myItemList.size());

        int index = 0;
        for (MyItem myItem : myItemList) {
            index++;
            logService.log("mapMyItemToSet", index + "/" + myItemList.size() + ", myItem : " + myItem);
            boolean existsMapItem = matchMyItemSetItemRepository.existsMapItem(matchId, myItem.getItemNo(), myItem.getColorId());
            if (existsMapItem) {
                logService.log("mapMyItemToSet", "alread exists! - skipped");
                continue;
            }

            // find set
            // TODO color 범위 확대하기
            addMatchSetItemWithSimilarAll(matchId, myItem);
        }
        logService.log("mapMyItemToSet", "map finish - matchId : " + matchId);
    }

    private void addMatchSetItemWithSimilarAll(String matchId, MyItem myItem) {
        // 유사 item 반영하기
        Set<String> itemNosWithSimilarAll = itemNosWithSimilarAll(myItem.getItemNo());
        List<SetItem> setItemList = itemNosWithSimilarAll.stream()
                .map(itemNo -> setItemRepository.findByItemAndColor(itemNo, myItem.getColorId()))
                .flatMap(List::stream)
                .collect(toList());
        logService.log("addMatchSetItemWithSimilarAll", "setItemList.size : " + setItemList.size());

        // 대표 itemNo
//        String itemNo = myItem.getItemNo();
//        if (itemNosWithSimilarAll.size() > 1) {
//            itemNo = brickLinkSimilarService.findRepresentPartNo(itemNo);
//        }

        List<MatchMyItemSetItem> matchMyItemSetItemList = new ArrayList<>();
        for (SetItem setItem : setItemList) {
            // 수량 적용
            if (myItem.getQty() <= 0) continue;

            MatchMyItemSetItem matchMyItemSetItem = new MatchMyItemSetItem();
            matchMyItemSetItem.setItemNo(setItem.getItemNo());
            matchMyItemSetItem.setColorId(setItem.getColorId());
            matchMyItemSetItem.setSetId(setItem.getSetId());
            matchMyItemSetItem.setSetNo(setItem.getSetNo());
            matchMyItemSetItem.setQty(Math.min(myItem.getQty(), setItem.getQty()));
            matchMyItemSetItem.setMatchId(matchId);
            matchMyItemSetItem.setItemType(CategoryType.P.getCode());

            matchMyItemSetItemList.add(matchMyItemSetItem);
        }

        matchMyItemSetItemRepository.saveAll(matchMyItemSetItemList);
    }

    private Set<String> itemNosWithSimilarAll(String itemNo) {
        Set<String> itemNos = new HashSet<>();
        itemNos.addAll(brickLinkSimilarService.findPartNos(itemNo));

        return itemNos;
    }

    /**
     *
     * @param matchId
     */
    public void mapMyItemToSetRatio(String matchId) {
        logService.log("mapMyItemToSemtRatio", "map start - matchId : " + matchId);
        // 대상 setIdList
        List<MatchMyItemSetItem> matchList = matchMyItemSetItemRepository.distinctSetIdNoAll(matchId)
            .stream()
            .map(objectArr -> {
                MatchMyItemSetItem matchMyItemSetItem = new MatchMyItemSetItem();
                matchMyItemSetItem.setSetId((String)objectArr[0]);
                matchMyItemSetItem.setSetNo((String)objectArr[1]);
                return matchMyItemSetItem;
            }).collect(toList());
        logService.log("mapMyItemToSemtRatio", "setIdList.size : " + matchList.size());

        // 비율 구하기
        int index = 0;
        for (MatchMyItemSetItem match : matchList) {
            index++;
            // 매칭 부품 수량
            int matchedQty = matchMyItemSetItemRepository.findMatchSetParts(matchId, match.getSetId()).stream()
                .mapToInt(MatchMyItemSetItem::getQty).sum();

            // 전체 부품 수
            int totalQty = setItemRepository.findBySetId(match.getSetId()).stream()
                .mapToInt(SetItem::getQty).sum();

            MatchMyItemSetItemRatio itemRatio = new MatchMyItemSetItemRatio();
            itemRatio.setMatchId(matchId);
            itemRatio.setSetId(match.getSetId());
            itemRatio.setSetNo(match.getSetNo());
            itemRatio.setMatched(matchedQty);
            itemRatio.setTotal(totalQty);
            itemRatio.setRatio((float)matchedQty / totalQty);

            logService.log("mapMyItemToSemtRatio", index + "/" + matchList.size() + " - itemRatio : " + itemRatio);
            matchMyItemSetItemRatioRepository.save(itemRatio);

        }

        logService.log("mapMyItemToSemtRatio", "map finish - matchId : " + matchId);
    }

    /**
     * 해당 wanted-setNo의 부품 목록을 모두 제거
     * @param setNo
     */
    public void removeAllSetMyPartsWhereWanted(String setNo) {
        myItemRepository.findByWhere(WHERE_CODE_WANTED, setNo).stream()
            .forEach(myItem -> myItemRepository.delete(myItem));
    }
}
