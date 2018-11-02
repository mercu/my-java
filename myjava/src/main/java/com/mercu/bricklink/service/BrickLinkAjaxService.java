package com.mercu.bricklink.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercu.http.HttpService;
import com.mercu.utils.SubstringUtils;

@Service
public class BrickLinkAjaxService {
    Logger logger = LoggerFactory.getLogger(BrickLinkAjaxService.class);

    @Autowired
    private HttpService httpService;


    /**
     * https://www.bricklink.com/ajax/clone/search/autocomplete.ajax?callback=jQuery111208572062803442151_1540631420720&suggest_str=70403&_=1540631420723
     * // jQuery111208572062803442151_1540631420720({"keywords":[{"option":"70903","type":1},{"option":"70003","type":1},{"option":"7043","type":1},{"option":"70603","type":1}],"products":[{"name":"Dragon Mountain","type":2,"id":116326,"itemNo":"70403","seq":1,"imgString":"S/70403-1.jpg"},{"name":"Dragon Torso (Castle) with Black Dorsal Scales Pattern (70403)","type":2,"id":121637,"itemNo":"59224c01pb04","seq":0,"imgString":"P/5/59224c01pb04.jpg"},{"name":"Legends of Chima Super Pack 3 in 1 (70000, 70001, 70003)","type":2,"id":115257,"itemNo":"66450","seq":1,"imgString":"S/66450-1.jpg"}],"categories":[],"termincat":[],"returnCode":-1,"returnMessage":"Not Processed","errorTicket":0,"procssingTime":4});
     * @param setNo
     */
    public String ajaxFindSetId(String setNo) {
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

}
