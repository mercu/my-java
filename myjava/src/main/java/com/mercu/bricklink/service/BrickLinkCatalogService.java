package com.mercu.bricklink.service;

import com.mercu.bricklink.model.PartCategory;
import com.mercu.html.WebDomService;
import com.mercu.http.HttpService;
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
     * https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=P
     * - table.catalog-list__category-list--internal
     */
    public List<PartCategory> partCategoryList() {
        Element baseEl = webDomService.element(httpService.getAsString("https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=P"), "table.catalog-tree__category-list--internal");
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
