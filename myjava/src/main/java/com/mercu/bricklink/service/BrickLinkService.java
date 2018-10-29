package com.mercu.bricklink.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercu.html.WebDomService;
import com.mercu.http.HttpService;
import com.mercu.utils.SubstringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrickLinkService {
    @Autowired
    private HttpService httpService;
    @Autowired
    private WebDomService webDomService;

    /**
     * https://www.bricklink.com/ajax/clone/search/autocomplete.ajax?callback=jQuery111208572062803442151_1540631420720&suggest_str=70403&_=1540631420723
     * // jQuery111208572062803442151_1540631420720({"keywords":[{"option":"70903","type":1},{"option":"70003","type":1},{"option":"7043","type":1},{"option":"70603","type":1}],"products":[{"name":"Dragon Mountain","type":2,"id":116326,"itemNo":"70403","seq":1,"imgString":"S/70403-1.jpg"},{"name":"Dragon Torso (Castle) with Black Dorsal Scales Pattern (70403)","type":2,"id":121637,"itemNo":"59224c01pb04","seq":0,"imgString":"P/5/59224c01pb04.jpg"},{"name":"Legends of Chima Super Pack 3 in 1 (70000, 70001, 70003)","type":2,"id":115257,"itemNo":"66450","seq":1,"imgString":"S/66450-1.jpg"}],"categories":[],"termincat":[],"returnCode":-1,"returnMessage":"Not Processed","errorTicket":0,"procssingTime":4});
     * @param setNo
     */
    public String findSetId(String setNo) {
        String jsonLine = httpService.getAsString("https://www.bricklink.com/ajax/clone/search/autocomplete.ajax?callback=jQuery111208572062803442151_1540631420720&suggest_str=" + setNo + "&_=1540631420723");

        JsonObject jsonObj = new JsonParser().parse(
                SubstringUtils.substringBetweenWithout(jsonLine, "(", ")"))
                .getAsJsonObject();

        JsonArray products = jsonObj.get("products").getAsJsonArray();
        for (JsonElement productEl : products) {
            JsonObject productObj = productEl.getAsJsonObject();

            if (org.apache.commons.lang3.StringUtils.equals(productObj.get("itemNo").getAsString(), setNo)) {
                return productObj.get("id").getAsString();
            }
        }

        return null;
    }

    /**
     * Set Inventory
     * https://www.bricklink.com/v2/catalog/catalogitem_invtab.page?idItem=150407&st=1&show_invid=0&show_matchcolor=1&show_pglink=0&show_pcc=0&show_missingpcc=0&itemNoSeq=10706-1
     * @param setNo
     */
    public void setInventory(String setNo) {
        String setId = findSetId(setNo);

        String inventoryPage = httpService.getAsString("https://www.bricklink.com/v2/catalog/catalogitem_invtab.page?idItem=" + setId + "&st=1&show_invid=0&show_matchcolor=1&show_pglink=0&show_pcc=0&show_missingpcc=0&itemNoSeq=" + setNo + "-1");

        Elements itemRows = webDomService.elements(inventoryPage, ".pciinvItemRow, .pciinvExtraHeader, .pciinvItemTypeHeader");
        for (Element itemRow : itemRows) {
            if (itemRow.hasClass("pciinvItemRow")) {
                itemToString(itemRow);
            } else if (itemRow.hasClass("pciinvExtraHeader") &&
                    "Extra Items:".equals(itemRow.select("td").first().html())) {
                    break;
            } else if (itemRow.hasClass("pciinvItemTypeHeader")) {
                System.out.println(itemRow.select("td").first().html());
            }
        }

    }

    private void itemToString(Element itemRow) {
        String image = itemRow.select("img").first().attr("src");
        String qty = itemRow.select("td:nth-of-type(3)").html();
        String itemNo = itemRow.select("td:nth-of-type(4) a").first().html();
        String desc = itemRow.select("td:nth-of-type(5) b").first().html();
        System.out.println("* image : " + image + "\t, * qty : " + qty + "\t, * itemNo : " + itemNo + "\t, * desc : " + desc);
    }

}
