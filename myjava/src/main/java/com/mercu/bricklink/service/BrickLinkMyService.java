package com.mercu.bricklink.service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mercu.http.HttpService;
import com.mercu.utils.HtmlUtils;
import com.mercu.utils.JsonUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BrickLinkMyService {
    @Autowired
    private HttpService httpService;

    /**
     * My WantedList
     */
    public void wantedList() {
        String jsonContainedLine = HtmlUtils.findLineOfStringContains(
                httpService.getAsString("https://www.bricklink.com/v2/wanted/list.page"),
                "wantedLists");

        JsonObject jsonObj = new JsonParser().parse(
                JsonUtils.substringBetweenWithout(jsonContainedLine, "{", "}"))
                .getAsJsonObject();

        JsonArray wantedLists = jsonObj.get("wantedLists").getAsJsonArray();
        for (JsonElement wantedEl : wantedLists) {
            System.out.println("wantedEl : " + wantedEl);
        }

    }

}
