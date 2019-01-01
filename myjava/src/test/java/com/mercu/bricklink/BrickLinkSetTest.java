package com.mercu.bricklink;

import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.bricklink.crawler.BrickLinkSetCrawler;
import com.mercu.bricklink.service.BrickLinkSetItemService;
import com.mercu.config.AppConfig;
import com.mercu.log.LogService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class BrickLinkSetTest {
    private static final Logger logger = LoggerFactory.getLogger(BrickLinkSetTest.class);

    @Autowired
    private BrickLinkSetCrawler brickLinkService;
    @Autowired
    private BrickLinkSetItemService brickLinkSetItemService;
    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;

    @Autowired
    private LogService logService;

    @Test
    public void crawlSetInventory() {
        brickLinkSetItemService.saveSetItemList(
                brickLinkService.crawlSetInventoryBySetNo("6243"));
    }

    @Test
    public void existsSetItem() {
        System.out.println(brickLinkSetItemService.existsSetItem("149900"));
    }

    @Test
    public void crawlSetInventories() {
        for (int year = 2018; year >= 1953; year--) {
            logService.log("crawlSetInventories", "- year : " + year);
            List<SetInfo> setInfoList = brickLinkCatalogService.findSetInfoListByYear(String.valueOf(year));
            int index = 0;
            for (SetInfo setInfo : setInfoList) {
                index++;
                try {
                    // 전체 갱신하기
//                    if (brickLinkSetService.existsSetItem(setInfo.getId())) {
//                        logService.log("crawlSetInventories", "- year : " + year + ", " + index + "/" + setInfoList.size() + " - exists - skipped!");
//                        continue;
//                    } else
                    if (setInfo.getSetNo().contains("-")) {
                        logService.log("crawlSetInventories", "- invalid setNo : " + setInfo.getSetNo() + " - skipped!", "invalid");
                        continue;
                    }

                    logService.log("crawlSetInventories", "- year : " + year + ", " + index + "/" + setInfoList.size() + " - setInfo : " + setInfo + " - start");
                    brickLinkSetItemService.saveSetItemList(
                        brickLinkService.crawlSetInventoryBySetNo(setInfo.getSetNo()));
                    logService.log("crawlSetInventories", "- year : " + year + ", " + index + "/" + setInfoList.size() + " - setInfo : " + setInfo + " - finish");
                } catch (Exception e) {
                    logService.log("crawlSetInventories", "setNo : " + setInfo.getSetNo() + ", e : " + e.getMessage(), "exception");
                }
            }
        }
    }

    // TODO minifigs set 처리 (-2 ~ -n)


}
