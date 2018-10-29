package com.mercu.bricklink.service;

import com.mercu.bricklink.model.PartCategory;
import com.mercu.bricklink.model.PartInfo;
import com.mercu.bricklink.model.SetCategory;
import com.mercu.bricklink.model.SetInfo;
import com.mercu.bricklink.repository.PartCategoryRepository;
import com.mercu.bricklink.repository.PartInfoRepository;
import com.mercu.bricklink.repository.SetCategoryRepository;
import com.mercu.bricklink.repository.SetInfoRepository;
import com.mercu.html.WebDomService;
import com.mercu.http.HttpService;
import com.mercu.utils.SubstringUtils;
import com.mercu.utils.UrlUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class BrickLinkCatalogService {
    Logger logger = LoggerFactory.getLogger(BrickLinkCatalogService.class);

    @Autowired
    private HttpService httpService;
    @Autowired
    private WebDomService webDomService;

    @Autowired
    private PartCategoryRepository partCategoryRepository;
    @Autowired
    private SetCategoryRepository setCategoryRepository;
    @Autowired
    private SetInfoRepository setInfoRepository;
    @Autowired
    private PartInfoRepository partInfoRepository;

    /**
     * https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=S
     * - table.catalog-tree__category-list--internal
     */
    public List<SetCategory> crawlSetCategoryList() {
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
     * https://www.bricklink.com/catalogList.asp?catType=S&itemYear=2018
     */
    public List<SetInfo> crawlSetInfoListOfYear(String year) {
        String setListUrl = "https://www.bricklink.com/catalogList.asp?catType=S&itemYear=" + year;
        return crawlSetInfoListByUrl(setListUrl);
    }

    /**
     * https://www.bricklink.com/catalogList.asp?catType=P&itemBrand=1000&catString=93
     * - table.catalog-list__body-main
     */
    public List<PartInfo> crawlPartInfoListOfCategory(String partCategoryId) {
        String partListUrl = "https://www.bricklink.com/catalogList.asp?catType=P&itemBrand=1000&catString=" + partCategoryId;
        return crawlPartInfoListByUrl(partListUrl);
    }

    private List<PartInfo> crawlPartInfoListByUrl(String partListUrl) {
        logger.info("* partListUrl : {}", partListUrl);
        Integer pages = setListPages(partListUrl);
        if (Objects.isNull(pages)) return Collections.emptyList();

        List<PartInfo> partInfoList = new ArrayList<>();

        for (int page = 1; page <= pages; page++) {
            Elements setEls =
                    webDomService.elements(
                            httpService.getAsString(partListUrl + "&pg=" + page),
                            "table.catalog-list__body-main tr");
            for (Element setEl : setEls) {
                if (!setEl.toString().contains("Part No:")) continue;
                PartInfo partInfo = new PartInfo();
                partInfo.setId(setEl.selectFirst("span").attr("data-itemid"));
                partInfo.setCategoryId(
                        UrlUtils.urlParametersMap("http:" + setEl.selectFirst("td:nth-of-type(3) a:last-of-type").attr("href")
                                .replaceAll("&=", "&catString="))
                                .get("catString"));
                partInfo.setImg(setEl.selectFirst("img").attr("src"));
                partInfo.setPartNo(setEl.selectFirst("a").html());
                partInfo.setPartName(setEl.selectFirst("td strong").html());
                System.out.println(partInfo);
                partInfoList.add(partInfo);
            }
        }

        return partInfoList;
    }

    /**
     * https://www.bricklink.com/catalogList.asp?pg=1&catString=102&itemBrand=1000&catType=S
     * - table.catalog-list__body-main
     * - catSring :
     * - pg : 1 ~ n
     *   - div.catalog-list__pagination--top
     */
    public List<SetInfo> crawlSetInfoListOfCategory(String setCategoryId) {
        String setListUrl = "https://www.bricklink.com/catalogList.asp?catString=" + setCategoryId + "&itemBrand=1000&catType=S";
        return crawlSetInfoListByUrl(setListUrl);
    }

    private List<SetInfo> crawlSetInfoListByUrl(String setListUrl) {
        logger.info("* setListUrl : {}", setListUrl);
        Integer pages = setListPages(setListUrl);
        if (Objects.isNull(pages)) return Collections.emptyList();

        List<SetInfo> setInfoList = new ArrayList<>();

        for (int page = 1; page <= pages; page++) {
            Elements setEls =
                    webDomService.elements(
                            httpService.getAsString(setListUrl + "&pg=" + page),
                            "table.catalog-list__body-main tr");
            for (Element setEl : setEls) {
                if (!setEl.toString().contains("Set No:")) continue;
                SetInfo setInfo = new SetInfo();
                setInfo.setId(setEl.selectFirst("span").attr("data-itemid"));
                setInfo.setCategoryId(
                        UrlUtils.urlParametersMap("http:" + setEl.selectFirst("td:nth-of-type(3) a:last-of-type").attr("href")
                                .replaceAll("&=", "&catString="))
                                .get("catString"));
                setInfo.setImg(setEl.selectFirst("img").attr("src"));
                setInfo.setSetNo(setEl.selectFirst("a").html().replace("-1", ""));
                setInfo.setSetName(setEl.selectFirst("td strong").html());
                setInfo.setSetBrief(SubstringUtils.substringBetweenWithout(setEl.select("td:nth-of-type(3) font").html(), "<br>", "<br>"));
                System.out.println(setInfo);
                setInfoList.add(setInfo);
            }
        }

        return setInfoList;
    }

    private Integer setListPages(String listUrl) {
        return NumberUtils.createInteger(
                webDomService.element(
                        httpService.getAsString(listUrl),
                        "div.catalog-list__pagination--top div:nth-of-type(2) b:nth-of-type(3)").html()
        );
    }

    /**
     * https://www.bricklink.com/catalogTree.asp?itemBrand=1000&itemType=P
     * - table.catalog-list__category-list--internal
     */
    public List<PartCategory> crawlPartCategoryList() {
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

    public void savePartCategoryList(List<PartCategory> partCategoryList) {
        for (PartCategory partCategory : partCategoryList) {
            partCategoryRepository.save(partCategory);
        }
    }

    public void saveSetCategoryList(List<SetCategory> setCategoryList) {
        for (SetCategory setCategory : setCategoryList) {
            setCategoryRepository.save(setCategory);
        }
    }

    public List<SetCategory> findSetCategoriesAll() {
        return (List<SetCategory>)setCategoryRepository.findAll();
    }

    public void saveSetInfoList(List<SetInfo> setInfoList) {
        for (SetInfo setInfo : setInfoList) {
            setInfoRepository.save(setInfo);
        }
    }

    public List<PartCategory> findPartCategoriesAll() {
        return (List<PartCategory>)partCategoryRepository.findAll();
    }

    public void savePartInfoList(List<PartInfo> partInfoList) {
        for (PartInfo partInfo : partInfoList) {
            partInfoRepository.save(partInfo);
        }
    }
}
