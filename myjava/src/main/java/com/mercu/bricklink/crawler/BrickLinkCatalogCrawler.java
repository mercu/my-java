package com.mercu.bricklink.crawler;

import static java.util.stream.Collectors.*;

import java.util.ArrayList;
import java.util.Collections;
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
import com.mercu.bricklink.model.info.AbstractInfo;
import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.model.info.MinifigInfo;
import com.mercu.bricklink.model.info.PartInfo;
import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.html.WebDomService;
import com.mercu.http.HttpService;
import com.mercu.utils.SubstringUtils;
import com.mercu.utils.UrlUtils;

@Service
public class BrickLinkCatalogCrawler {
    Logger logger = LoggerFactory.getLogger(BrickLinkCatalogCrawler.class);

    @Autowired
    private HttpService httpService;
    @Autowired
    private WebDomService webDomService;

    /**
     * @param setCategoryId
     * @return
     */
    public List<SetInfo> crawlSetInfoListOfCategory(String setCategoryId) {
        return crawlInfoListOfCategory(setCategoryId, CategoryType.S).stream()
                .map(info -> (SetInfo)info)
                .collect(toList());
    }

    /**
     * @param partCategoryId
     * @return
     */
    public List<PartInfo> crawlPartInfoListOfCategory(String partCategoryId) {
        return crawlInfoListOfCategory(partCategoryId, CategoryType.P).stream()
                .map(info -> (PartInfo)info)
                .collect(toList());
    }

    /**
     * @param minifigCategoryId
     * @return
     */
    public List<MinifigInfo> crawlMinifigInfoListOfCategory(String minifigCategoryId) {
        return crawlInfoListOfCategory(minifigCategoryId, CategoryType.M).stream()
                .map(info -> (MinifigInfo)info)
                .collect(toList());
    }

    public List<AbstractInfo> crawlInfoListOfCategory(String categoryId, CategoryType categoryType) {
        String listUrl = listUrl(categoryId, categoryType);
        return crawlInfoListByUrl(listUrl, categoryType);
    }

    private String listUrl(String categoryId, CategoryType categoryType) {
        return "https://www.bricklink.com/catalogList.asp?catType=" + categoryType.getCode() + "&itemBrand=1000&catString=" + categoryId;
    }

    /**
     * https://www.bricklink.com/catalogList.asp?catType=S&itemYear=2018
     */
    public List<SetInfo> crawlSetInfoListOfYear(String year) {
        String setListUrl = "https://www.bricklink.com/catalogList.asp?catType=S&itemYear=" + year;
        return crawlInfoListByUrl(setListUrl, CategoryType.S).stream()
                .map(info -> (SetInfo)info)
                .collect(toList());
    }

    private List<AbstractInfo> crawlInfoListByUrl(String listUrl, CategoryType categoryType) {
        logger.info("* listUrl : {}", listUrl);
        Integer pages = setListPages(listUrl);
        if (Objects.isNull(pages)) return Collections.emptyList();

        List<AbstractInfo> infoList = new ArrayList<>();

        for (int page = 1; page <= pages; page++) {
            Elements elements =
                    webDomService.elements(
                            httpService.getAsString(listUrl + "&pg=" + page),
                            "table.catalog-list__body-main tr");
            for (Element element : elements) {
                if (element.hasClass("catalog-list__body-header")) continue;

                infoList.add(elToInfo(element, categoryType));
            }
        }

        return infoList;
    }

    private Integer setListPages(String listUrl) {
        return NumberUtils.createInteger(
                webDomService.element(
                        httpService.getAsString(listUrl),
                        "div.catalog-list__pagination--top div:nth-of-type(2) b:nth-of-type(3)").html()
        );
    }

    private AbstractInfo elToInfo(Element element, CategoryType categoryType) {
        switch (categoryType) {
            case S:
                return elToSetInfo(element);
            case P:
                return elToPartInfo(element);
            case M:
                return elToMinifigInfo(element);
            default:
                return null;
        }
    }

    private MinifigInfo elToMinifigInfo(Element minifigEl) {
        MinifigInfo minifigInfo = new MinifigInfo();
        minifigInfo.setId(minifigEl.selectFirst("span").attr("data-itemid"));
        minifigInfo.setCategoryId(
                UrlUtils.urlParametersMap("http:" + minifigEl.selectFirst("td:nth-of-type(3) a:last-of-type").attr("href")
                        .replaceAll("&=", "&catString="))
                        .get("catString"));
        minifigInfo.setImg(minifigEl.selectFirst("img").attr("src"));
        minifigInfo.setMinifigNo(minifigEl.selectFirst("a").html());
        minifigInfo.setMinifigName(minifigEl.selectFirst("td strong").html());
        System.out.println(minifigInfo);
        return minifigInfo;
    }

    private PartInfo elToPartInfo(Element partEl) {
        PartInfo partInfo = new PartInfo();
        partInfo.setId(partEl.selectFirst("span").attr("data-itemid"));
        partInfo.setCategoryId(
                UrlUtils.urlParametersMap("http:" + partEl.selectFirst("td:nth-of-type(3) a:last-of-type").attr("href")
                        .replaceAll("&=", "&catString="))
                        .get("catString"));
        partInfo.setImg(partEl.selectFirst("img").attr("src"));
        partInfo.setPartNo(partEl.selectFirst("a").html());
        partInfo.setPartName(partEl.selectFirst("td strong").html());
        System.out.println(partInfo);
        return partInfo;
    }

    private SetInfo elToSetInfo(Element setEl) {
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
        return setInfo;
    }

    /**
     * https://www.bricklink.com/catalogColors.asp?sortBy=N
     * - table:nth-of-type(3) table tr
     */
    public List<ColorInfo> crawlColorInfoList() {
        String colorUrl = "https://www.bricklink.com/catalogColors.asp?sortBy=N";
        Elements colorEls = webDomService.elements(
                httpService.getAsString(colorUrl),
                "table:nth-of-type(3) table tr");
        List<ColorInfo> colorInfoList = new ArrayList<>();
        for (Element colorEl : colorEls) {
            if (!colorEl.toString().contains("colorID=")) continue;

            colorInfoList.add(elToColorInfo(colorEl));
        }

        return colorInfoList;
    }

    private ColorInfo elToColorInfo(Element colorEl) {
        ColorInfo colorInfo = new ColorInfo();
        colorInfo.setId(UrlUtils.urlParametersMap("http:" + colorEl.selectFirst("a").attr("href"))
                .get("colorID"));
        colorInfo.setName(colorEl.select("td:nth-of-type(4) font").html().replaceAll("&nbsp;", ""));
        colorInfo.setColorCode(colorEl.select("td:nth-of-type(2)").attr("bgcolor"));

        return colorInfo;
    }

}
