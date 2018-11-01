package com.mercu.bricklink.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.bricklink.model.match.MatchMyItemSetItem;
import com.mercu.bricklink.model.match.MatchMyItemSetItemRatio;
import com.mercu.bricklink.model.my.MyItem;
import com.mercu.bricklink.repository.MatchMyItemSetItemRatioRepository;
import com.mercu.bricklink.repository.MatchMyItemSetItemRepository;
import com.mercu.bricklink.repository.MyItemRepository;
import com.mercu.bricklink.repository.SetItemRepository;
import com.mercu.http.HttpService;
import com.mercu.log.LogService;
import com.mercu.utils.HtmlUtils;
import com.mercu.utils.SubstringUtils;

@Service
public class BrickLinkMyService {

    @Autowired
    private MyItemRepository myItemRepository;
    @Autowired
    private SetItemRepository setItemRepository;
    @Autowired
    private MatchMyItemSetItemRepository matchMyItemSetItemRepository;
    @Autowired
    private MatchMyItemSetItemRatioRepository matchMyItemSetItemRatioRepository;

    @Autowired
    private HttpService httpService;
    @Autowired
    private LogService logService;

    /**
     * My WantedList
     */
    public void crawlWantedList() {
        String jsonContainedLine = HtmlUtils.findLineOfStringContains(
                httpService.getAsString("https://www.bricklink.com/v2/wanted/list.page"),
                "wantedLists");

        JsonObject jsonObj = new JsonParser().parse(
                SubstringUtils.substringBetweenWith(jsonContainedLine, "{", "}"))
                .getAsJsonObject();

        JsonArray wantedLists = jsonObj.get("wantedLists").getAsJsonArray();
        for (JsonElement wantedEl : wantedLists) {
            System.out.println("wantedEl : " + wantedEl);
        }

    }

    /**
     * @param setNo
     */
    public void addMyListBySetNo(String setNo) {
        // setItemList 가져오기
        List<SetItem> setItemList = setItemRepository.findBySetNo(setNo);
        System.out.println(setItemList);

        // myItem에 추가하기
        for (SetItem setItem : setItemList) {
            MyItem myItem = new MyItem();
            myItem.setItemType(CategoryType.P.getCode());
            myItem.setItemNo(setItem.getItemNo());
            myItem.setColorId(setItem.getColorId());
            myItem.setQty(setItem.getQty());
            myItem.setWhereType("virtual");

            myItemRepository.save(myItem);
        }
    }

    /**
     * @param matchId
     */
    public void mapMyItemToSet(String matchId) {
        logService.log("mapMyItemToSet", "map start - matchId : " + matchId);
        // myItemList 조회하기
        List<MyItem> myItemList = (List<MyItem>)myItemRepository.findAll();
        logService.log("mapMyItemToSet", "myItemList.size : " + myItemList.size());

        int index = 0;
        for (MyItem myItem : myItemList) {
            index++;
            logService.log("mapMyItemToSet", index + "/" + myItemList.size() + ", myItem : " + myItem);
            boolean existsMapItem = matchMyItemSetItemRepository.existsMapItem(matchId, myItem.getItemNo());
            if (existsMapItem) {
                logService.log("mapMyItemToSet", "alread exists! - skipped");
                continue;
            }

            // find set
            // TODO 유사 item 반영하기
            // TODO color 범위 확대하기
            List<SetItem> setItemList = setItemRepository.findByItemAndColor(myItem.getItemNo(), myItem.getColorId());
            logService.log("mapMyItemToSet", "setItemList.size : " + setItemList.size());

            for (SetItem setItem : setItemList) {
                MatchMyItemSetItem findItemSet = new MatchMyItemSetItem();
                findItemSet.setItemNo(myItem.getItemNo());
                findItemSet.setColorId(myItem.getColorId());
                findItemSet.setSetId(setItem.getSetId());
                findItemSet.setSetNo(setItem.getSetNo());
                findItemSet.setQty(setItem.getQty());
                findItemSet.setMatchId(matchId);
                findItemSet.setItemType(CategoryType.P.getCode());

                matchMyItemSetItemRepository.save(findItemSet);
            }
        }
        logService.log("mapMyItemToSet", "map finish - matchId : " + matchId);
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
            // 매칭 부품 수
            int matched = matchMyItemSetItemRepository.countBySetId(match.getSetId(), matchId);

            // 전체 부품 수
            int total = setItemRepository.countItemsBySetId(match.getSetId(), CategoryType.P.getCode());
            System.out.println("total : " + total);

            MatchMyItemSetItemRatio itemRatio = new MatchMyItemSetItemRatio();
            itemRatio.setMatchId(matchId);
            itemRatio.setSetId(match.getSetId());
            itemRatio.setSetNo(match.getSetNo());
            itemRatio.setMatched(matched);
            itemRatio.setTotal(total);
            itemRatio.setRatio((float)matched / total);

            logService.log("mapMyItemToSemtRatio", index + "/" + matchList.size() + " - itemRatio : " + itemRatio);
            matchMyItemSetItemRatioRepository.save(itemRatio);

        }

        logService.log("mapMyItemToSemtRatio", "map finish - matchId : " + matchId);
    }
}
