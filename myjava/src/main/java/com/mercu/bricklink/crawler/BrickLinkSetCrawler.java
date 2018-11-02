package com.mercu.bricklink.crawler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.bricklink.service.BrickLinkAjaxService;
import com.mercu.bricklink.service.BrickLinkColorService;
import com.mercu.html.WebDomService;
import com.mercu.http.HttpService;
import com.mercu.log.LogService;

@Service
public class BrickLinkSetCrawler {
    Logger logger = LoggerFactory.getLogger(BrickLinkSetCrawler.class);

    @Autowired
    private HttpService httpService;
    @Autowired
    private WebDomService webDomService;

    @Autowired
    private BrickLinkColorService brickLinkColorService;
    @Autowired
    private BrickLinkAjaxService brickLinkAjaxService;

    @Autowired
    private LogService logService;

    /**
     * Set Inventory
     * https://www.bricklink.com/v2/catalog/catalogitem_invtab.page?idItem=150407&st=1&show_invid=0&show_matchcolor=1&show_pglink=0&show_pcc=0&show_missingpcc=0&itemNoSeq=10706-1
     * @param setNo
     */
    public List<SetItem> crawlSetInventoryBySetNo(String setNo) {
        logService.log("crawlSetInventoryBySetNo", "crawlSetInventoryBySetNo : " + setNo);
        String setId = brickLinkAjaxService.ajaxFindSetId(setNo);

        String setInventoryUrl = "https://www.bricklink.com/v2/catalog/catalogitem_invtab.page?idItem=" + setId + "&st=1&show_invid=0&show_matchcolor=1&show_pglink=0&show_pcc=0&show_missingpcc=0&itemNoSeq=" + setNo + "-1";
        logService.log("crawlSetInventoryBySetNo", "setInventoryUrl : " + setInventoryUrl);
        String inventoryPage = httpService.getAsString(setInventoryUrl);

        List<SetItem> setItemList = new ArrayList<>();

        CategoryType categoryType = null;

        Elements itemRows = webDomService.elements(inventoryPage, ".pciinvItemRow, .pciinvExtraHeader, .pciinvItemTypeHeader");
        for (Element itemRow : itemRows) {
            if (itemRow.hasClass("pciinvItemTypeHeader")) {
                String itemType = itemRow.select("td").first().html();
                if ("Parts:".equals(itemType)) categoryType = CategoryType.P;
                else if ("Minifigs:".equals(itemType)) categoryType = CategoryType.M;
                else if ("Sets:".equals(itemType)) categoryType = CategoryType.S;
            } else if (itemRow.hasClass("pciinvItemRow")) {
                if (Objects.isNull(categoryType)) {
                    logService.log("crawlSetInventoryBySetNo", "categoryType is null! - itemRow : " + itemRow, "exception");
                    continue;
                }

                SetItem setItem = itemToString(itemRow, categoryType);
                setItem.setSetId(setId);
                setItem.setSetNo(setNo);

                logger.info("setItem : {}", setItem);
                setItemList.add(setItem);
            } else if (itemRow.hasClass("pciinvExtraHeader") &&
                    ("Extra Items:".equals(itemRow.select("td").first().html())
                    || "Alternate Items:".equals(itemRow.select("td").first().html()))) {
                    break;
            }
        }

        logService.log("crawlSetInventoryBySetNo", "setNo : " + setNo + ", setItemList.size : " + setItemList.size());
        return setItemList;
    }

    private SetItem itemToString(Element itemRow, CategoryType categoryType) {
        SetItem setItem = new SetItem();
        setItem.setItemNo(itemRow.select("td:nth-of-type(4) a").first().html());
        setItem.setCategoryType(categoryType.getCode());
        setItem.setImage(itemRow.select("img").first().attr("src"));
        setItem.setQty(NumberUtils.toInt(itemRow.select("td:nth-of-type(3)").html(), 0));
        setItem.setDescription(itemRow.select("td:nth-of-type(5) b").first().html());
        if (categoryType == CategoryType.P) {
            ColorInfo colorInfo = brickLinkColorService.findColor(setItem.getDescription());
            if (Objects.isNull(colorInfo)) logger.warn("not found color! - desc : {}", setItem.getDescription());
            else {
                setItem.setColorId(colorInfo.getId());
            }
        }
        return setItem;
    }

}
