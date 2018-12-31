package com.mercu.lego.service;

import static java.util.stream.Collectors.*;

import java.util.*;

import com.mercu.bricklink.model.CategoryType;
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
    private BrickLinkSetService brickLinkSetService;
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

    public Map<String, Object> findMatchSetParts(String matchId, String setId, String whereValue) {
        Map<String, Object> resultMap = new HashMap<>();

        SetInfo setInfo = brickLinkCatalogService.findSetInfo(setId);
        resultMap.put("setInfo", setInfo);
        String setNo = setInfo.getSetNo();

        List<SetItem> partList = brickLinkSetService.findBySetId(setId);

        List<MatchMyItemSetItem> matchItems = matchMyItemSetItemRepository.findMatchSetParts(matchId, setId);

        // 해당 WANTED에 모자른 부품들에 대한 Set Where 목록
        Map<String, Integer> matchWheres = new HashMap<>();

        partList.stream()
                .forEach(part -> {
                    // 해당 WANTED에 있으면 match
                    MatchMyItemSetItem matchItem = findMatched(part, matchItems);
                    if (Objects.isNull(matchItem)) {
                        matchItem = newMatchMyItemSetItem(part, matchId);
                        matchItems.add(matchItem);
                    }
                    matchItem.setPartQty(part.getQty());
                    matchItem.setQty(Optional.ofNullable(myItemService.findByIdWhere(part.getCategoryType(), part.getItemNo(), part.getColorId(), setNo)).map(MyItem::getQty).orElse(0));
                    // 필터링 여부 (숨김)
                    final boolean[] filtered = {true};
                    if (StringUtils.isNotBlank(whereValue)) {
                        filtered[0] = false;
                    }
                    // 해당 WANTED match qty가 모자르면 나머지 STORAGE, WANTED에서 찾기
                    if (matchItem.getQty() < matchItem.getPartQty()) {
                        brickLinkMyService.findMyItemWheresSimilar(part.getCategoryType(), part.getItemNo(), part.getColorId(), null).stream()
                                .filter(myItem -> myItem.getQty() > 0)
                                .collect(toList()).stream()
                                .map(myItem -> myItem.getWhereCode() + "-" + myItem.getWhereMore())
                                .collect(toSet()).stream()
                                .forEach(matchWhere -> {
                                    MapUtils.increase(matchWheres, matchWhere);
                                    // 필터링 여부 (노출)
                                    if (StringUtils.equals(matchWhere, whereValue)) {
                                        filtered[0] = true;
                                    }
                                });
                    }
                    // 필터링 여부 (숨김)
                    matchItem.setFiltered(filtered[0]);
                });

        matchItems.stream()
                .forEach(matchItem -> {
                    try {
                        if (Objects.nonNull(matchItem.getColorId()))
                            matchItem.setColorInfo(brickLinkColorService.findColorById(matchItem.getColorId()));
                        if (Objects.nonNull(matchItem.getItemNo())) {
                            PartInfo partInfo = brickLinkCatalogService.findPartByPartNo(matchItem.getItemNo());
                            matchItem.setPartInfo(partInfo);
                            matchItem.setMyItems(findMyItemsWithSimilar(matchItem));
                            matchItem.setMatched(hasMatchedWhere(matchItem.getMyItems(), setNo)
                                    && matchItem.getQty() >= matchItem.getPartQty());
                            // 정렬 순서 (카테고리)
                            matchItem.setSortOrder(myCategoryService.findRootCategoryByBlCategoryId(partInfo.getCategoryId()).getSortOrder());
                        }
                        matchItem.setImgUrl(BrickLinkUrlUtils.partImageUrl(matchItem.getItemNo(), matchItem.getColorId()));

                    } catch (Exception e) {
                        System.out.println("exception! - matchItem : " + matchItem);
                    }
                });

        // 부품 카테고리 기준으로 정렬하기
        resultMap.put("matchItems", matchItems.stream()
                .filter(MatchMyItemSetItem::getFiltered)
                .sorted(Comparator.comparing(MatchMyItemSetItem::getSortOrder).reversed()
                        .thenComparing(matchItem -> Optional.ofNullable(matchItem.getPartInfo()).map(PartInfo::getPartName).orElse("")))
                .collect(toList()));

        // 해당 WANTED에 모자른 부품들에 대한 Set Where 목록
        resultMap.put("matchWheres", matchWheres.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(toList()));

        return resultMap;
    }

    private boolean hasMatchedWhere(List<MyItem> myItems, String setNo) {
        return myItems.stream()
                .filter(myItem -> StringUtils.equals(myItem.getWhereMore(), setNo))
                .findAny()
                .isPresent();
    }

    private List<MyItem> findMyItemsWithSimilar(MatchMyItemSetItem matchItem) {
        List<MyItem> myItems = new ArrayList<>();
        brickLinkSimilarService.findPartNos(matchItem.getItemNo()).stream()
                .forEach(partNo -> myItems.addAll(myItemService.findList(matchItem.getItemType(), partNo, matchItem.getColorId())));
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

    private MatchMyItemSetItem findMatched(SetItem part, List<MatchMyItemSetItem> matchItems) {
        return matchItems.stream()
                .filter(matchItem -> brickLinkSimilarService.compareWithSimilarPartNos(matchItem.getItemNo(), part.getItemNo())
                        && matchItem.getColorId().equals(part.getColorId()))
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
        int totalQty = brickLinkSetService.findBySetId(setId).stream()
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
        SetItem setItem = brickLinkSetService.findSetPart(setId, partNo, colorId);

        // 보유 부품 목록(유사부품 포함)
        List<MyItem> myItems = brickLinkSimilarService.findPartNos(setItem.getItemNo()).stream()
                .map(itemNo -> myItemService.findList(setItem.getCategoryType(), itemNo, setItem.getColorId()))
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
        List<SetItem> setItemList = brickLinkSetService.findBySetId(setId);

        // 보유 부품 목록(유사부품 포함)
        List<MatchMyItemSetItem> matchMyItemSetItemList = new ArrayList<>();
        setItemList.stream()
                .forEach(setItem -> {
                    List<MyItem> myItems = brickLinkSimilarService.findPartNos(setItem.getItemNo()).stream()
                            .map(partNo -> myItemService.findList(setItem.getCategoryType(), partNo, setItem.getColorId()))
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
