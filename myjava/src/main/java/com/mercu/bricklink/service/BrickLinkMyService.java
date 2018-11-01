package com.mercu.bricklink.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.map.FindItemSet;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.bricklink.model.my.MyItem;
import com.mercu.bricklink.repository.FindItemSetRepository;
import com.mercu.bricklink.repository.MyItemRepository;
import com.mercu.bricklink.repository.SetItemRepository;
import com.mercu.http.HttpService;
import com.mercu.log.LogService;
import com.mercu.utils.HtmlUtils;
import com.mercu.utils.SubstringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrickLinkMyService {

    @Autowired
    private MyItemRepository myItemRepository;
    @Autowired
    private SetItemRepository setItemRepository;
    @Autowired
    private FindItemSetRepository findItemSetRepository;

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
     * @param mapId
     */
    public void mapMyItemToSet(String mapId) {
        logService.log("mapMyItemToSet", "map start - mapId : " + mapId);
        // myItemList 조회하기
        List<MyItem> myItemList = (List<MyItem>)myItemRepository.findAll();
        logService.log("mapMyItemToSet", "myItemList.size : " + myItemList.size());

        int index = 0;
        for (MyItem myItem : myItemList) {
            index++;
            logService.log("mapMyItemToSet", index + "/" + myItemList.size() + ", myItem : " + myItem);
            boolean existsMapItem = findItemSetRepository.existsMapItem(mapId, myItem.getItemNo());
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
                FindItemSet findItemSet = new FindItemSet();
                findItemSet.setItemNo(myItem.getItemNo());
                findItemSet.setColorId(myItem.getColorId());
                findItemSet.setSetId(setItem.getSetId());
                findItemSet.setSetNo(setItem.getSetNo());
                findItemSet.setQty(setItem.getQty());
                findItemSet.setMapId(mapId);
                findItemSet.setItemType(CategoryType.P.getCode());

                findItemSetRepository.save(findItemSet);
            }
        }
        logService.log("mapMyItemToSet", "map finish - mapId : " + mapId);
    }
}
