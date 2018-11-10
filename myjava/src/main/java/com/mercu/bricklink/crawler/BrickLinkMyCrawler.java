package com.mercu.bricklink.crawler;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.bricklink.model.my.MyItem;
import com.mercu.bricklink.repository.map.SetItemRepository;
import com.mercu.bricklink.repository.my.MyItemRepository;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.html.WebDomService;
import com.mercu.http.HttpService;
import com.mercu.log.LogService;
import com.mercu.utils.HtmlUtils;
import com.mercu.utils.SubstringUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BrickLinkMyCrawler {

    @Autowired
    private HttpService httpService;
    @Autowired
    private WebDomService webDomService;

    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;
    @Autowired
    private SetItemRepository setItemRepository;
    @Autowired
    private MyItemRepository myItemRepository;

    @Autowired
    private LogService logService;

    /**
     * My WantedList
     * - https://www.bricklink.com/v2/wanted/list.page
     * - $("div.list-container table.wl-overview-list-table")
     */
    public void crawlWantedList() {
        logService.log("crawlWantedList", "=== start");
        String wantedListUrl = "https://www.bricklink.com/v2/wanted/list.page";

        String jsonContainedLine = HtmlUtils.findLineOfStringContains(
                httpService.getAsString(wantedListUrl),
                "wantedLists");

        JsonObject jsonObj = new JsonParser().parse(
                SubstringUtils.substringBetweenWith(jsonContainedLine, "{", "}"))
                .getAsJsonObject();

        JsonArray wantedLists = jsonObj.get("wantedLists").getAsJsonArray();
        int index = 0;
        for (JsonElement wantedEl : wantedLists) {
            index++;
            logService.log("crawlWantedList", index + "/" + wantedLists.size() + " - wanted : " + wantedEl);
//            System.out.println("wantedEl : " + wantedEl);
            JsonObject wantedJson = wantedEl.getAsJsonObject();
//            System.out.println("totalLeft : " + wantedJson.get("totalLeft"));
            String setNo = extractSetNo(wantedJson.get("name").toString());
            System.out.println("name : " + setNo);
//            JsonArray items = wantedJson.get("items").getAsJsonArray();
//            System.out.println("items : " + items.size());

            // 세트정보
            SetInfo setInfo = brickLinkCatalogService.findSetInfoBySetNo(setNo);
            System.out.println("setInfo : " + setInfo);
            if (Objects.isNull(setInfo)) {
                logService.log("crawlWantedList", "not found set! - setNo : " + setNo, "warn");
                continue;
            }

            // My Wanted More (Parts)
            List<MyItem> wantedMyItems = crawlWantedMyItems(wantedJson.get("id").toString());

            // 세트 부품 정보에서 MyWantedParts 를 제외한 부품들을 MyItem으로 추가한다.
            addSetItemsToMyItemsExcludeWantedParts(setNo, wantedMyItems);
        }

        logService.log("crawlWantedList", "=== finish");
    }

    private void addSetItemsToMyItemsExcludeWantedParts(String setNo, List<MyItem> wantedMyItems) {
        logService.log("addSetItemsToMyItemsExcludeWantedParts", setNo + " - start");
        // 부품(Part)만 한다.. (우선)
        List<SetItem> setItemList = setItemRepository.findBySetNo(setNo).stream()
                .filter(setItem -> StringUtils.equals(setItem.getCategoryType(), CategoryType.P.getCode()))
                .collect(Collectors.toList());

        // 이미 완료되었으면 skip
//        if (alreadyCrawlSaved(setNo, setItemList, wantedMyItems)) {
//            logService.log("addSetItemsToMyItemsExcludeWantedParts", setNo + " - already crawl and saved. - skipped.");
//            return;
//        }

        // 저장 전에 미리 비우기 (중복 방지)
        myItemRepository.deleteByWhere(MyItem.WHERE_CODE_WANTED, setNo);

        for (SetItem setItem : setItemList) {
            MyItem toSaveMyItem = extractMyItemToSave(setItem, wantedMyItems);
            if (Objects.isNull(toSaveMyItem)) {
                logService.log("addSetItemsToMyItemsExcludeWantedParts", "toSaveMyItem : " + toSaveMyItem + " - skipped.");
                continue;
            } else {
                logService.log("addSetItemsToMyItemsExcludeWantedParts", "toSaveMyItem : " + toSaveMyItem);
                myItemRepository.save(toSaveMyItem);
            }
        }

        logService.log("addSetItemsToMyItemsExcludeWantedParts", setNo + " - finish");
    }

    private boolean alreadyCrawlSaved(String setNo, List<SetItem> setItemList, List<MyItem> wantedMyItems) {
        List<MyItem> savedMyItems = myItemRepository.findByWhere(MyItem.WHERE_CODE_WANTED, setNo);
        return (savedMyItems.size() >= setItemList.size() - wantedMyItems.size());
    }

    private MyItem extractMyItemToSave(SetItem setItem, List<MyItem> wantedMyItems) {
        MyItem toSaveMyItem = toSaveMyItemFromSetItem(setItem);

        MyItem wantedMyItem = wantedMyItems.stream()
                .filter(wantedSetPart -> StringUtils.equals(wantedSetPart.getItemNo(), setItem.getItemNo()))
                .findFirst()
                .orElse(null);
        if (Objects.nonNull(wantedMyItem)) {
            toSaveMyItem.setQty(toSaveMyItem.getQty() - wantedMyItem.getQty());
            if (toSaveMyItem.getQty() == 0) {
                return null;
            } else if (toSaveMyItem.getQty() < 0) {
                logService.log("extractMyItemToSave", "toSaveMyItem.qty is negative! - setItem : " + setItem + ", wantedMyItem : " + wantedMyItem, "warn");
                return null;
            }
        }
        return toSaveMyItem;
    }

    private MyItem toSaveMyItemFromSetItem(SetItem setItem) {
        MyItem toSaveMyItem = new MyItem();
        toSaveMyItem.setItemType(setItem.getCategoryType());
        toSaveMyItem.setItemNo(setItem.getItemNo());
        toSaveMyItem.setColorId(setItem.getColorId());
        toSaveMyItem.setWhereCode(MyItem.WHERE_CODE_WANTED);
        toSaveMyItem.setWhereMore(setItem.getSetNo());
        toSaveMyItem.setQty(setItem.getQty());
        return toSaveMyItem;
    }

    /**
     * https://www.bricklink.com/v2/wanted/search.page?wantedMoreID=1943454&page=2
     * - $("div.table-wl-edit div.table-row")
     * - pages :
     * @param wantedMoreId
     * @return
     */
    private List<MyItem> crawlWantedMyItems(String wantedMoreId) {
        List<MyItem> wantedMyItems = new ArrayList<>();

        boolean morePage = true;
        int page = 1;
        while (morePage) {
            List<MyItem> wantedMyItemssOfPage = crawlWantedMyItemsOfPage(wantedMoreId, page);
            if (wantedMyItemssOfPage.size() == 0) {
                morePage = false;
            } else {
                wantedMyItems.addAll(wantedMyItemssOfPage);
            }
            page++;
        }

        return wantedMyItems;
    }

    /**
     * https://www.bricklink.com/v2/wanted/search.page?wantedMoreID=1943454&page=2
     * - $("div.table-wl-edit div.table-row")
     * @param page
     * @return
     */
    private List<MyItem> crawlWantedMyItemsOfPage(String wantedMoreId, int page) {
        String wantedMoreUrl = "https://www.bricklink.com/v2/wanted/search.page?wantedMoreID=" + wantedMoreId + "&page=" + page;

        String jsonContainedLine = HtmlUtils.findLineOfStringContains(
                httpService.getAsString(wantedMoreUrl),
                "wlJson");

        JsonObject jsonObj = new JsonParser().parse(
                SubstringUtils.substringBetweenWith(jsonContainedLine, "{", "}"))
                .getAsJsonObject();

        List<MyItem> wantedMyItems = new ArrayList<>();
        JsonArray wantedItems = jsonObj.get("wantedItems").getAsJsonArray();
        for (JsonElement wantedItemEl : wantedItems) {
            // {"wantedID":98090845,"wantedMoreID":699402,"wantedMoreName":"9-70403-323","itemNo":"51342pb07","itemID":121621,"itemSeq":0,"itemName":"Dragon Wing 19 x 11 with Marbled Red Trailing Edge Pattern","itemType":"P","itemBrand":1000,"imgURL":"//img.bricklink.com/ItemImage/PT/11/51342pb07.t2.png","wantedQty":1,"wantedQtyFilled":0,"wantedNew":"X","wantedNotify":"N","wantedRemark":"*","wantedPrice":-1,"formatWantedPrice":"KRW -1.00","colorID":11,"colorName":"Black","colorHex":"212121"}
            wantedMyItems.add(bindMyItem(wantedItemEl.getAsJsonObject()));
        }

        return wantedMyItems;
    }

    private MyItem bindMyItem(JsonObject wantedItemJson) {
        MyItem myItem = new MyItem();
        myItem.setItemType(wantedItemJson.get("itemType").toString());
        myItem.setItemNo(wantedItemJson.get("itemNo").toString());
        myItem.setColorId(wantedItemJson.get("colorID").toString());
        myItem.setQty(NumberUtils.toInt(wantedItemJson.get("wantedQty").toString()));
        myItem.setWhereCode(MyItem.WHERE_CODE_WANTED);
        myItem.setWhereMore(wantedItemJson.get("wantedMoreName").toString());
        return myItem;
    }

    private String extractSetNo(String setName) {
        setName = setName.replaceAll("_", "-");
        if (setName.split("-").length == 3) {
            return setName.split("-")[1];
        } else if (setName.split("-").length == 4) {
            return setName.split("-")[2];
        }
//        log.warn("cant extract - setName : " + setName);
        return setName;
    }

}
