package com.mercu.bricklink.service;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.category.AbstractCategory;
import com.mercu.bricklink.model.category.MinifigCategory;
import com.mercu.bricklink.model.category.PartCategory;
import com.mercu.bricklink.model.category.SetCategory;
import com.mercu.bricklink.repository.MinifigCategoryRepository;
import com.mercu.bricklink.repository.PartCategoryRepository;
import com.mercu.bricklink.repository.SetCategoryRepository;
import com.mercu.html.WebDomService;
import com.mercu.http.HttpService;
import com.mercu.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.toList;

@Service
public class BrickLinkCategoryService {
    Logger logger = LoggerFactory.getLogger(BrickLinkCategoryService.class);

    @Autowired
    private HttpService httpService;
    @Autowired
    private WebDomService webDomService;

    @Autowired
    private PartCategoryRepository partCategoryRepository;
    @Autowired
    private SetCategoryRepository setCategoryRepository;
    @Autowired
    private MinifigCategoryRepository minifigCategoryRepository;

    /**
     * https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=M
     * - table.catalog-tree__category-list--internal
     */
    public List<MinifigCategory> crawlMinifigCategoryList() {
        return crawlCategoryList(CategoryType.M).stream()
                .map(category -> (MinifigCategory)category)
                .collect(toList());
    }

    /**
     * https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=S
     * - table.catalog-tree__category-list--internal
     */
    public List<SetCategory> crawlSetCategoryList() {
        return crawlCategoryList(CategoryType.S).stream()
                .map(category -> (SetCategory)category)
                .collect(toList());
    }

    /**
     * https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=P
     * - table.catalog-list__category-list--internal
     */
    public List<PartCategory> crawlPartCategoryList() {
        return crawlCategoryList(CategoryType.P).stream()
                .map(category -> (PartCategory)category)
                .collect(toList());
    }

    public List<AbstractCategory> crawlCategoryList(CategoryType categoryType) {
        String categoryUrl = "https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=" + categoryType.getCode();

        Element baseEl = webDomService.element(
                httpService.getAsString(categoryUrl),
                "table.catalog-tree__category-list--internal");

        List<AbstractCategory> categoryList = new ArrayList<>();
        baseEl.select("tr td").stream()
                .filter(element -> element.toString().contains("catalogList.asp"))
                .forEach(element -> {
                    String[] splits = element.html().split("<a");

                    for (String split : splits) {
                        if (StringUtils.isBlank(split)) continue;

                        categoryList.add(extractCategory(split, categoryType));
                    }

                });

        return categoryList;
    }

    private AbstractCategory extractCategory(String splitHtml, CategoryType categoryType) {
        String categoryHtml = "<a" + splitHtml;

        Element aEl = webDomService.element(categoryHtml, "a");
        Map<String, String> aQueriesMap = UrlUtils.urlParametersMap("http:" + aEl.attr("href"));
        Elements spanEl = webDomService.elements(categoryHtml, "span");

        return elToCategory(aEl, aQueriesMap, spanEl, categoryType);
    }

    private AbstractCategory elToCategory(Element aEl, Map<String, String> aQueriesMap, Elements spanEl, CategoryType categoryType) {
        switch (categoryType) {
            case S:
                return elToSetCategory(aEl, aQueriesMap, spanEl);
            case P:
                return elToPartCategory(aEl, aQueriesMap, spanEl);
            case M:
                return elToMinifigCategory(aEl, aQueriesMap, spanEl);
            default:
                return null;
        }
    }

    private SetCategory elToSetCategory(Element aEl, Map<String, String> aQueriesMap, Elements spanEl) {
        SetCategory setCategory = new SetCategory();
        setCategory.setId(aQueriesMap.get("catString"));
        setCategory.setDepth(setCategory.getId().split("[.]").length);
        setCategory.setName(setCategory.getDepth() == 1 ? aEl.selectFirst("b").html() : aEl.html());
        setCategory.setParts(spanEl.html());
        setCategory.setType(aQueriesMap.get("catType"));
        return setCategory;
    }

    private PartCategory elToPartCategory(Element aEl, Map<String, String> aQueriesMap, Elements spanEl) {
        PartCategory partCategory = new PartCategory();
        partCategory.setName(aEl.selectFirst("b").html());
        partCategory.setParts(spanEl.html());
        partCategory.setType(aQueriesMap.get("catType"));
        partCategory.setId(aQueriesMap.get("catString"));
        return partCategory;
    }

    private MinifigCategory elToMinifigCategory(Element aEl, Map<String, String> aQueriesMap, Elements spanEl) {
        MinifigCategory minifigCategory = new MinifigCategory();
        minifigCategory.setId(aQueriesMap.get("catString"));
        minifigCategory.setDepth(minifigCategory.getId().split("[.]").length);
        minifigCategory.setName(minifigCategory.getDepth() == 1 ? aEl.selectFirst("b").html() : aEl.html());
        minifigCategory.setParts(spanEl.html());
        minifigCategory.setType(aQueriesMap.get("catType"));
        return minifigCategory;
    }

    /**
     * @param partCategoryList
     */
    public void savePartCategoryList(List<PartCategory> partCategoryList) {
        for (PartCategory partCategory : partCategoryList) {
            partCategoryRepository.save(partCategory);
        }
    }

    /**
     * @param setCategoryList
     */
    public void saveSetCategoryList(List<SetCategory> setCategoryList) {
        for (SetCategory setCategory : setCategoryList) {
            setCategoryRepository.save(setCategory);
        }
    }

    /**
     * @param minifigCategoryList
     */
    public void saveMinifigCategoryList(List<MinifigCategory> minifigCategoryList) {
        for (MinifigCategory minifigCategory : minifigCategoryList) {
            minifigCategoryRepository.save(minifigCategory);
        }
    }

    /**
     * @return
     */
    public List<SetCategory> findSetCategoriesAll() {
        return (List<SetCategory>)setCategoryRepository.findAll();
    }

    /**
     * @return
     */
    public List<PartCategory> findPartCategoriesAll() {
        return (List<PartCategory>)partCategoryRepository.findAll();
    }

    /**
     * @return
     */
    public List<MinifigCategory> findMinifigCategoriesRoot() {
        List<MinifigCategory> minifigCategoriesAll = (List<MinifigCategory>)minifigCategoryRepository.findAll();
        return minifigCategoriesAll.stream()
                .filter(minifigCategory -> minifigCategory.getDepth() == 1)
                .collect(toList());
    }

}
