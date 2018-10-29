package com.mercu.bricklink.service;

import com.mercu.bricklink.model.PartCategory;
import com.mercu.bricklink.model.SetCategory;
import com.mercu.html.WebDomService;
import com.mercu.http.HttpService;
import com.mercu.utils.SubstringUtils;
import com.mercu.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class BrickLinkCatalogService {
    @Autowired
    private HttpService httpService;
    @Autowired
    private WebDomService webDomService;

    /**
     * https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=S
     * - table.catalog-tree__category-list--internal
     */
    public List<SetCategory> setCategoryList() {
        Element baseEl = webDomService.element(
                httpService.getAsString("https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=S"),
                "table.catalog-tree__category-list--internal");

        List<SetCategory> setCategoryList = new ArrayList<>();
        baseEl.select("tr td").stream()
                .filter(element -> element.toString().contains("catalogList.asp"))
                .forEach(element -> {
                    String[] setSplits = element.html().split("<a");

                    for (String setSplit : setSplits) {
                        if (StringUtils.isBlank(setSplit)) continue;
                        if (StringUtils.contains(setSplit, "{")) continue;

                        setCategoryList.add(extractSet(setSplit));
                    }
                });

        return setCategoryList;
    }

    private SetCategory extractSet(String setSplit) {
        String setHtml = "<a" + setSplit;

        Element aEl = webDomService.element(setHtml, "a");
        Map<String, String> aQueriesMap = UrlUtils.urlParametersMap("http:" + aEl.attr("href"));
        Elements spanEl = webDomService.elements(setHtml, "span");

        SetCategory setCategory = new SetCategory();
        setCategory.setId(aQueriesMap.get("catString"));
        setCategory.setDepth(setCategory.getId().split("[.]").length);
        setCategory.setName(setCategory.getDepth() == 1 ? aEl.selectFirst("b").html() : aEl.html());
        setCategory.setParts(spanEl.html());
        setCategory.setType(aQueriesMap.get("catType"));
        return setCategory;
    }

    /**
     * https://www.bricklink.com/catalogList.asp?pg=1&catString=102&itemBrand=1000&catType=S
     * - table.catalog-list__body-main
     * - catSring :
     * - pg : 1 ~ n
     */
    public void setList() {
        Elements setEls =
                webDomService.elements(
                        httpService.getAsString("https://www.bricklink.com/catalogList.asp?pg=1&catString=102&itemBrand=1000&catType=S"),
                        "table.catalog-list__body-main tr");
        for (Element setEl : setEls) {
            if (!setEl.toString().contains("Set No:")) continue;
            System.out.println("* setEl : " + setEl);
            System.out.println("- title : " + setEl.selectFirst("span").attr("title"));
            System.out.println("- itemid : " + setEl.selectFirst("span").attr("data-itemid"));
            System.out.println("- img : " + setEl.selectFirst("img").attr("src"));
            System.out.println("- setNo : " + setEl.selectFirst("a").html().replace("-1", ""));
            System.out.println("- setName : " + setEl.selectFirst("td strong").html());
            System.out.println("- setBrief : " + SubstringUtils.substringBetweenWithout(setEl.select("td:nth-of-type(3) font").html(), "<br>", "<br>"));
        }
    }

    /**
     * https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=P
     * - table.catalog-list__category-list--internal
     */
    public List<PartCategory> partCategoryList() {
        Element baseEl = webDomService.element(
                httpService.getAsString("https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=P"),
                "table.catalog-tree__category-list--internal");

        List<PartCategory> partCategoryList = new ArrayList<>();
        baseEl.select("tr td").stream()
                .filter(element -> element.toString().contains("catalogList.asp"))
                .forEach(element -> {
                    String[] partSplits = element.html().split("<a");

                    for (String partSplit : partSplits) {
                        if (StringUtils.isBlank(partSplit)) continue;

                        partCategoryList.add(extractPart(partSplit));
                    }

                });

        return partCategoryList;
    }

    private PartCategory extractPart(String partSplit) {
        String partHtml = "<a" + partSplit;

        Element aEl = webDomService.element(partHtml, "a");
        Map<String, String> aQueriesMap = UrlUtils.urlParametersMap("http:" + aEl.attr("href"));
        Elements spanEl = webDomService.elements(partHtml, "span");

        PartCategory partCategory = new PartCategory();
        partCategory.setName(aEl.selectFirst("b").html());
        partCategory.setParts(spanEl.html());
        partCategory.setType(aQueriesMap.get("catType"));
        partCategory.setId(aQueriesMap.get("catString"));
        return partCategory;
    }

}
