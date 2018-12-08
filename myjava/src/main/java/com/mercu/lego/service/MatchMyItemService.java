package com.mercu.lego.service;

import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.bricklink.repository.match.MatchMyItemSetItemRatioRepository;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.bricklink.service.BrickLinkSetService;
import com.mercu.lego.model.MatchMyItemSetItemRatio;
import com.mercu.lego.repository.MatchMyItemRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchMyItemService {
    @Autowired
    private MatchMyItemRepository matchMyItemRepository;

    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;
    @Autowired
    private BrickLinkSetService brickLinkSetService;

    public List<String> findMatchIds() {
        return matchMyItemRepository.findMatchIds();
    }

    public List<MatchMyItemSetItemRatio> findMatchSetList(String matchId) {
        if (StringUtils.isBlank(matchId)) {
            matchId = findMatchIds().stream()
                    .findFirst().get();
        }
        return matchMyItemRepository.findMatchSetList(matchId, new PageRequest(0, 500));
    }

    public Object findMatchSetParts(String setId) {
        SetInfo setInfo = brickLinkCatalogService.findSetInfo(setId);
        System.out.println("*** setInfo : " + setInfo);

        List<SetItem> partList = brickLinkSetService.findBySetId(setId);
        System.out.println("*** partList : " + partList);

        return null;
    }
}
