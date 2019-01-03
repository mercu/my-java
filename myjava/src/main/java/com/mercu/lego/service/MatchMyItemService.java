package com.mercu.lego.service;

import static com.mercu.lego.model.my.MyItem.WHERE_CODE_WANTED;
import static java.util.stream.Collectors.*;

import java.util.*;
import java.util.stream.Collectors;

import com.mercu.bricklink.model.similar.SimilarPart;
import com.mercu.bricklink.service.*;
import com.mercu.utils.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.mercu.bricklink.BrickLinkUrlUtils;
import com.mercu.bricklink.model.info.PartInfo;
import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.lego.model.match.MatchMyItemSetItem;
import com.mercu.lego.model.match.MatchMyItemSetItemRatio;
import com.mercu.lego.model.my.MyItem;
import com.mercu.lego.repository.MatchMyItemSetItemRatioRepository;
import com.mercu.lego.repository.MatchMyItemSetItemRepository;
import org.springframework.util.CollectionUtils;

@Slf4j
@Service
public class MatchMyItemService {
    @Autowired
    private MatchMyItemSetItemRatioRepository matchMyItemSetItemRatioRepository;
    @Autowired
    private MatchMyItemSetItemRepository matchMyItemSetItemRepository;

    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;
    @Autowired
    private BrickLinkSetItemService brickLinkSetItemService;
    @Autowired
    private BrickLinkColorService brickLinkColorService;
    @Autowired
    private BrickLinkSimilarService brickLinkSimilarService;
    @Autowired
    private MyItemService myItemService;
    @Autowired
    private MyCategoryService myCategoryService;
    @Autowired
    private BrickLinkMyService brickLinkMyService;

    public List<String> findMatchIds() {
        return matchMyItemSetItemRatioRepository.findMatchIds();
    }

    public List<MatchMyItemSetItemRatio> findMatchSetList(String matchId) {
        if (StringUtils.isBlank(matchId)) {
            matchId = findMatchIds().stream()
                    .findFirst().get();
        }
        return matchMyItemSetItemRatioRepository.findMatchSetList(matchId, new PageRequest(0, 1000));
    }

    /**
     * @param matchId
     * @param setId
     * @param whereValue (nullable)
     * @return
     */
    public Map<String, Object> findMatchSetParts(String matchId, String setId, String whereValue) {
        Map<String, Object> resultMap = new HashMap<>();

        // SetInfo
        SetInfo setInfo = brickLinkCatalogService.findSetInfo(setId);
        resultMap.put("setInfo", setInfo);

        // 매칭된 보유 부품목록
        List<MatchMyItemSetItem> matchItems = matchMyItemSetItemRepository.findMatchSetParts(matchId, setId);

        // 해당 WANTED에 모자른 부품들에 대한 Set Where 목록
//        Map<String, Integer> matchWheres = new HashMap<>();

        // 전체 부품 목록기준으로 보유 부품 대입하고 없으면 생성 (세트 부품 목록 완성하기)
        fillSetItems(matchId, setId, matchItems);

        // matchItem 내용 채우기
        fillMatchItemInfos(setId, matchItems);

        // 부품 카테고리 기준으로 정렬하기
        resultMap.put("matchItems", sortMatchItems(matchItems));

        // 해당 WANTED에 모자른 부품들에 대한 Set Where 목록
//        resultMap.put("matchWheres", matchWheres.entrySet().stream()
//                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
//                .collect(toList()));

        return resultMap;
    }

    public Map<String, Object> recommendPartsWhere(String matchId, String setId) {
        Map<String, Object> resultMap = new HashMap<>();

        // 매칭된 보유 부품목록
        List<MatchMyItemSetItem> matchItems = matchMyItemSetItemRepository.findMatchSetParts(matchId, setId);

        // 전체 부품 목록기준으로 보유 부품 대입하고 없으면 생성 (세트 부품 목록 완성하기)
        fillSetItems(matchId, setId, matchItems);

        // matchItem 내용 채우기
        fillMatchItemInfos(setId, matchItems);

        // 추천 부품 위치 채우기
        recommendItemWheres(setId, matchItems);

        // 부품 카테고리 기준으로 정렬하기
        resultMap.put("matchItems", sortMatchItems(matchItems));

        // 추천 위치 목록
        resultMap.put("matchWheres", matchWheres(matchItems));

        return resultMap;
    }

    // 추천 위치 목록
    private List<Map.Entry<String, Integer>> matchWheres(List<MatchMyItemSetItem> matchItems) {
        Map<String, Integer> matchWheres = new HashMap<>();
        matchItems.stream()
                .forEach(matchItem -> {
                    matchItem.getMyItems().stream()
                            .forEach(myItem -> {
                                MapUtils.increase(matchWheres, myItem.getWhereCode() + "-" + myItem.getWhereMore());
                            });
                });

        return matchWheres.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(toList());
    }

    // 추천 부품 위치 채우기
    private void recommendItemWheres(String setId, List<MatchMyItemSetItem> matchItems) {
        List<MyItem> myItemList = new ArrayList<>();
        // 보유 부품 위치 목록 전체
        matchItems.stream()
                .forEach(matchItem -> {
                    // 보유 아이템 목록 전체 - 유사 아이템 반영
                    brickLinkSimilarService.findPartNosCached(matchItem.getItemNo()).stream()
                            .forEach(partNo -> {
                                myItemList.addAll(brickLinkMyService.findMyItems(matchItem.getItemNo(), matchItem.getColorId()));
                            });
                });

        // 수량 정렬
        Map<String, List<MyItem>> myItemsMap = myItemList.stream()
                .filter(myItem -> myItem.getQty() > 0)
                .collect(Collectors.groupingBy(myItem -> myItem.getWhereCode() + "-" + myItem.getWhereMore()
                        , toList())).entrySet().stream()
                .sorted(Comparator.comparing(entry -> ((List<MyItem>)((Map.Entry)entry).getValue()).stream()
                        .mapToInt(MyItem::getQty)
                        .sum()).reversed())
                .collect(toMap(Map.Entry::getKey, Map.Entry::getValue));
        log.info("myItemsMap : {}", myItemsMap);

        // 추천 부품 채우기
        matchItems.stream()
                .forEach(matchItem -> {
                    // 부품 채우기 (1순위. 해당 SET 부품 -> 2순위. 추천 부품)
                    matchItem.setMyItems(fillPriority(matchItem, myItemsMap, setId));
                    matchItem.setQty(matchItem.getMyItems().stream()
                            .mapToInt(MyItem::getQty).sum());
                });

        log.info("matchItems : {}", matchItems);
//                    brickLinkMyService.findMyItemWheresSimilar(matchItem.getItemType(), matchItem.getItemNo(), matchItem.getColorId(), brickLinkCatalogService.setNoBySetIdCached(setId)).stream()
//                                .filter(myItem -> myItem.getQty() > 0)
//                                .collect(toList()).stream()
//                                .map(myItem -> myItem.getWhereCode() + "-" + myItem.getWhereMore())
//                                .collect(toSet()).stream()
//                                .forEach(matchWhere -> {
//                                    MapUtils.increase(matchWheres, matchWhere);
//                                    // 필터링 여부 (노출)
//                                    if (StringUtils.equals(matchWhere, whereValue)) {
//                                        filtered[0] = true;
//                                    }
//                                });
//                    }
//                });
    }

    // 부품 채우기 (1순위. 해당 SET 부품 -> 2순위. 추천 부품)
    private List<MyItem> fillPriority(MatchMyItemSetItem matchItem, Map<String, List<MyItem>> myItemsMap, String setId) {
        List<MyItem> fillItems = new ArrayList<>();

        // 1순위. 해당 SET 부품
        String wantedSet = WHERE_CODE_WANTED + "-" + brickLinkCatalogService.setNoBySetIdCached(setId);
        List<MyItem> wantedItems = myItemsMap.get(wantedSet);
        if (CollectionUtils.isEmpty(wantedItems) == false) {
            MyItem wantedItem = wantedItems.stream()
                    .filter(myItem -> StringUtils.equals(myItem.getItemNo(), matchItem.getItemNo())
                            && StringUtils.equals(myItem.getColorId(), matchItem.getColorId()))
                    .findFirst()
                    .orElse(null);
            if (Objects.nonNull(wantedItem) && wantedItem.getQty() > 0) {
                fillItems.add(wantedItem);

                if (wantedItem.getQty() >= matchItem.getPartQty())
                    return fillItems;
            }
        }

        // 2순위. 추천 부품 (수량 다 채워질때 까지)
        myItemsMap.entrySet().stream()
                .forEach(entry -> {
                    // 해당 SET이거나 수량이 다 차면 skip
                    if (StringUtils.equals(entry.getKey(), wantedSet)
                            || fillItems.stream().mapToInt(MyItem::getQty).sum() >= matchItem.getPartQty()) {
                        // skip
                    } else {
                        // 부품 일치하면 순서대로 채우기
                        MyItem recommendItem = entry.getValue().stream()
                                .filter(myItem -> StringUtils.equals(myItem.getItemNo(), matchItem.getItemNo())
                                        && StringUtils.equals(myItem.getColorId(), matchItem.getColorId()))
                                .findFirst()
                                .orElse(null);
                        if (Objects.nonNull(recommendItem)) {
                            fillItems.add(recommendItem);
                        }
                    }

                });


        return fillItems;
    }

    private List<MatchMyItemSetItem> sortMatchItems(List<MatchMyItemSetItem> matchItems) {
        return matchItems.stream()
                .filter(MatchMyItemSetItem::getFiltered)
                .sorted(Comparator.comparing(MatchMyItemSetItem::getSortOrder).reversed()
                        .thenComparing(matchItem -> Optional.ofNullable(matchItem.getPartInfo()).map(PartInfo::getPartName).orElse("")))
                .collect(toList());
    }

    // matchItem 내용 채우기
    private void fillMatchItemInfos(String setId, List<MatchMyItemSetItem> matchItems) {
        matchItems.stream()
                .forEach(matchItem -> {
                    try {
                        // ColorInfo 추가
                        if (Objects.nonNull(matchItem.getColorId()))
                            matchItem.setColorInfo(brickLinkColorService.findColorByIdCached(matchItem.getColorId()));
                        // PartInfo 추가
                        if (Objects.nonNull(matchItem.getItemNo())) {
                            PartInfo partInfo = brickLinkCatalogService.findPartByPartNo(matchItem.getItemNo());
                            matchItem.setPartInfo(partInfo);
                            // 유사 아이템 추가
                            matchItem.setMyItems(findMyItemsWithSimilar(matchItem));
                            // 해당 WANTED SET 수량 충족여부
                            matchItem.setMatched(matchItem.getQty() >= matchItem.getPartQty()
                                    && hasMatchedWhere(matchItem.getMyItems(), brickLinkCatalogService.setNoBySetIdCached(setId)));
                            // 정렬 순서 (카테고리)
                            matchItem.setSortOrder(myCategoryService.findRootCategoryByBlCategoryIdCached(partInfo.getCategoryId()).getSortOrder());
                        }
                        matchItem.setImgUrl(BrickLinkUrlUtils.partImageUrl(matchItem.getItemNo(), matchItem.getColorId()));

                    } catch (Exception e) {
                        System.out.println("exception! - matchItem : " + matchItem);
                    }
                });
    }

    // 전체 부품 목록기준으로 보유 부품 대입하고 없으면 생성 (세트 부품 목록 완성하기)
    private void fillSetItems(String matchId, String setId, List<MatchMyItemSetItem> matchItems) {
        brickLinkSetItemService.findItemsAllBySetId(setId).stream()
                .forEach(part -> {
                    // 매칭(유사) 보유 아이템 찾기 (없으면 null)
                    MatchMyItemSetItem matchItem = findMatchedWithSimilar(part, matchItems);
                    // 없으면 새로 생성해서 목록에 추가하기
                    if (Objects.isNull(matchItem)) {
                        matchItem = newMatchMyItemSetItem(part, matchId);
                        matchItems.add(matchItem);
                    }

                    // 보유 수량
                    matchItem.setQty(Optional.ofNullable(myItemService.findByIdWhereWanted(part.id(), brickLinkCatalogService.setNoBySetIdCached(setId)))
                            .map(MyItem::getQty)
                            .orElse(0));
                    matchItem.setPartQty(part.getQty());

//                    // 필터링 여부 (숨김)
//                    final boolean[] filtered = {true};
//                    if (StringUtils.isNotBlank(whereValue)) {
//                        filtered[0] = false;
//                    }
//                    // 해당 WANTED match qty가 모자르면 나머지 STORAGE, WANTED에서 찾기
//                    if (matchItem.getQty() < matchItem.getPartQty()) {
//                        brickLinkMyService.findMyItemWheresSimilar(part.getCategoryType(), part.getItemNo(), part.getColorId(), null).stream()
//                                .filter(myItem -> myItem.getQty() > 0)
//                                .collect(toList()).stream()
//                                .map(myItem -> myItem.getWhereCode() + "-" + myItem.getWhereMore())
//                                .collect(toSet()).stream()
//                                .forEach(matchWhere -> {
//                                    MapUtils.increase(matchWheres, matchWhere);
//                                    // 필터링 여부 (노출)
//                                    if (StringUtils.equals(matchWhere, whereValue)) {
//                                        filtered[0] = true;
//                                    }
//                                });
//                    }
//                    // 필터링 여부 (숨김)
//                    matchItem.setFiltered(filtered[0]);
                });
    }

    private boolean hasMatchedWhere(List<MyItem> myItems, String setNo) {
        return myItems.stream()
                .filter(myItem -> StringUtils.equals(myItem.getWhereMore(), setNo))
                .findAny()
                .isPresent();
    }

    // 유사 아이템 추가
    private List<MyItem> findMyItemsWithSimilar(MatchMyItemSetItem matchItem) {
        List<MyItem> myItems = new ArrayList<>();
        brickLinkSimilarService.findPartNosCached(matchItem.getItemNo()).stream()
                .forEach(partNo -> myItems.addAll(myItemService.findList(partNo, matchItem.getColorId())));
        return myItems;
    }

    private MatchMyItemSetItem newMatchMyItemSetItem(SetItem part, String matchId) {
        MatchMyItemSetItem matchMyItemSetItem = new MatchMyItemSetItem();
        matchMyItemSetItem.setItemNo(part.getItemNo());
        matchMyItemSetItem.setSetId(part.getSetId());
        matchMyItemSetItem.setMatchId(matchId);
        matchMyItemSetItem.setItemType(part.getCategoryType());
        matchMyItemSetItem.setColorId(part.getColorId());
        matchMyItemSetItem.setSetId(part.getSetId());
        matchMyItemSetItem.setQty(0);

        return matchMyItemSetItem;
    }

    // 매칭(유사) 보유 아이템 찾기 (없으면 null)
    private MatchMyItemSetItem findMatchedWithSimilar(SetItem part, List<MatchMyItemSetItem> matchItems) {
        return matchItems.stream()
                .filter(matchItem -> StringUtils.equals(matchItem.getColorId(), part.getColorId())
                        && brickLinkSimilarService.compareWithSimilarPartNos(matchItem.getItemNo(), part.getItemNo()))
                .findFirst()
                .orElse(null);
    }

    /**
     * 매칭 정보 갱신 (매칭 부품, 매칭율)
     * @param partNo
     * @param colorId
     * @param setNo
     * @param matchId
     */
    public void updateMatchSetPart(String partNo, String colorId, String setNo, String matchId) {
        // 매칭 부품 갱신
        updateMatchSetParts(partNo, colorId, setNo, matchId);

        // 매칭율 갱신
        updateMatchSetRatio(setNo, matchId);
    }

    /**
     * 매칭 정보 갱신 (세트전체 - 매칭 부품, 매칭율)
     * @param setNo
     * @param matchId
     */
    public void updateMatchSet(String setNo, String matchId) {
        // 매칭 부품 갱신
        updateMatchSetParts(setNo, matchId);

        // 매칭율 갱신
        updateMatchSetRatio(setNo, matchId);
    }

    /**
     * 매칭율 갱신
     * @param setNo
     * @param matchId
     */
    public void updateMatchSetRatio(String setNo, String matchId) {
        // 세트ID
        String setId = brickLinkCatalogService.findSetInfoBySetNo(setNo).getId();

        // 매칭 부품 수량
        int matchedQty = matchMyItemSetItemRepository.findMatchSetParts(matchId, setId).stream()
            .mapToInt(MatchMyItemSetItem::getQty).sum();

        // 전체 부품 수량
        int totalQty = brickLinkSetItemService.findItemsAllBySetId(setId).stream()
            .mapToInt(SetItem::getQty).sum();

        MatchMyItemSetItemRatio itemRatio = new MatchMyItemSetItemRatio();
        itemRatio.setMatchId(matchId);
        itemRatio.setSetId(setId);
        itemRatio.setSetNo(setNo);
        itemRatio.setMatched(matchedQty);
        itemRatio.setTotal(totalQty);
        itemRatio.setRatio((float)matchedQty / totalQty);

        matchMyItemSetItemRatioRepository.save(itemRatio);
    }

    /**
     * 매칭 부품 갱신
     * @param partNo
     * @param colorId
     * @param setNo
     * @param matchId
     */
    public void updateMatchSetParts(String partNo, String colorId, String setNo, String matchId) {
        // 세트ID
        String setId = brickLinkCatalogService.findSetInfoBySetNo(setNo).getId();

        // 세트 부품 정보
        SetItem setItem = brickLinkSetItemService.findSetPart(setId, partNo, colorId);

        // 보유 부품 목록(유사부품 포함)
        List<MyItem> myItems = brickLinkSimilarService.findPartNosCached(setItem.getItemNo()).stream()
                .map(itemNo -> myItemService.findList(itemNo, setItem.getColorId()))
                .flatMap(List::stream)
                .collect(toList());

        // 부품별 매칭정보
        MatchMyItemSetItem matchMyItemSetItem = getMatchMyItemSetItem(matchId, setItem);
        // 수량 1건이라도 있으면 등록, 없으면 삭제
        if (myItems.stream().mapToInt(MyItem::getQty).sum() > 0) {
            // 매칭 업데이트 (기존 데이터 삭제 후)
            matchMyItemSetItem.setQty(Math.min(myItems.stream().mapToInt(MyItem::getQty).sum(), setItem.getQty()));
            matchMyItemSetItemRepository.save(matchMyItemSetItem);
        } else {
            matchMyItemSetItemRepository.delete(matchMyItemSetItem);
        }

    }

    private MatchMyItemSetItem getMatchMyItemSetItem(String matchId, SetItem setItem) {
        MatchMyItemSetItem matchMyItemSetItem = new MatchMyItemSetItem();
        matchMyItemSetItem.setItemNo(setItem.getItemNo());
        matchMyItemSetItem.setColorId(setItem.getColorId());
        matchMyItemSetItem.setSetId(setItem.getSetId());
        matchMyItemSetItem.setSetNo(setItem.getSetNo());
        matchMyItemSetItem.setItemType(setItem.getCategoryType());
        matchMyItemSetItem.setMatchId(matchId);
        return matchMyItemSetItem;
    }

    /**
     * 매칭 부품 갱신
     * @param setNo
     * @param matchId
     */
    public void updateMatchSetParts(String setNo, String matchId) {
        // 세트ID
        String setId = brickLinkCatalogService.findSetInfoBySetNo(setNo).getId();

        // 세트 부품 목록
        List<SetItem> setItemList = brickLinkSetItemService.findItemsAllBySetId(setId);

        // 보유 부품 목록(유사부품 포함)
        List<MatchMyItemSetItem> matchMyItemSetItemList = new ArrayList<>();
        setItemList.stream()
                .forEach(setItem -> {
                    List<MyItem> myItems = brickLinkSimilarService.findPartNosCached(setItem.getItemNo()).stream()
                            .map(partNo -> myItemService.findList(partNo, setItem.getColorId()))
                            .flatMap(List::stream)
                            .collect(toList());
                    // 수량 1건이라도 있으면 등록
                    if (myItems.stream().mapToInt(MyItem::getQty).sum() > 0) {
                        MatchMyItemSetItem matchMyItemSetItem = getMatchMyItemSetItem(matchId, setItem);
                        matchMyItemSetItem.setQty(Math.min(myItems.stream().mapToInt(MyItem::getQty).sum(), setItem.getQty()));

                        matchMyItemSetItemList.add(matchMyItemSetItem);
                    }

                });

        // 매칭 업데이트 (기존 데이터 삭제 후)
        matchMyItemSetItemRepository.deleteAllBySetIdMatchId(setId, matchId);
        matchMyItemSetItemRepository.saveAll(matchMyItemSetItemList);
    }

    /**
     * 해당 setNo의 match 목록을 모두 제거
     * @param matchId
     * @param setId
     */
    public void removeMatchSetParts(String matchId, String setId) {
        matchMyItemSetItemRepository.deleteAllBySetIdMatchId(setId, matchId);
        matchMyItemSetItemRatioRepository.deleteAllBySetIdMatchId(setId, matchId);
    }

}
