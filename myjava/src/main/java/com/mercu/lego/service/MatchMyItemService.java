package com.mercu.lego.service;

import com.mercu.bricklink.BrickLinkUrlUtils;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.bricklink.service.BrickLinkColorService;
import com.mercu.bricklink.service.BrickLinkSetService;
import com.mercu.bricklink.service.BrickLinkSimilarService;
import com.mercu.lego.model.match.MatchMyItemSetItem;
import com.mercu.lego.model.match.MatchMyItemSetItemRatio;
import com.mercu.lego.model.my.MyItem;
import com.mercu.lego.repository.MatchMyItemSetItemRatioRepository;
import com.mercu.lego.repository.MatchMyItemSetItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

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

    public List<String> findMatchIds() {
        return matchMyItemSetItemRatioRepository.findMatchIds();
    }

    public List<MatchMyItemSetItemRatio> findMatchSetList(String matchId) {
        if (StringUtils.isBlank(matchId)) {
            matchId = findMatchIds().stream()
                    .findFirst().get();
        }
        return matchMyItemSetItemRatioRepository.findMatchSetList(matchId, new PageRequest(0, 500));
    }

    public List<MatchMyItemSetItem> findMatchSetParts(String matchId, String setId) {
//        SetInfo setInfo = brickLinkCatalogService.findSetInfo(setId);
//        System.out.println("*** setInfo : " + setInfo);

        List<SetItem> partList = brickLinkSetService.findBySetId(setId);
        System.out.println("*** partList : " + partList);

        List<MatchMyItemSetItem> matchItems = matchMyItemSetItemRepository.findMatchSetParts(matchId, setId);
        System.out.println("*** matchItems : " + matchItems);

        partList.stream()
                .forEach(part -> {
                    if (!existsMatched(part, matchItems)) {
                        matchItems.add(newMatchMyItemSetItem(part, matchId));
                    }
                });

        matchItems.stream()
                .forEach(matchItem -> {
                    try {
                        if (Objects.nonNull(matchItem.getColorId()))
                            matchItem.setColorInfo(brickLinkColorService.findColorById(matchItem.getColorId()));
                        if (Objects.nonNull(matchItem.getItemNo())) {
                            matchItem.setPartInfo(brickLinkCatalogService.findPartByPartNo(matchItem.getItemNo()));
                            matchItem.setMyItems(findMyItemsWithSimilar(matchItem));
                        }
                        matchItem.setImgUrl(BrickLinkUrlUtils.partImageUrl(matchItem.getItemNo(), matchItem.getColorId()));

                    } catch (Exception e) {
                        System.out.println("exception! - matchItem : " + matchItem);
                    }
                });

        return matchItems;
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

    private boolean existsMatched(SetItem part, List<MatchMyItemSetItem> matchItems) {
        return matchItems.stream()
                .anyMatch(matchItem -> brickLinkSimilarService.compareWithSimilarPartNos(matchItem.getItemNo(), part.getItemNo())
                        && matchItem.getColorId().equals(part.getColorId())
                );
    }
}
