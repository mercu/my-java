package com.mercu.bricklink;

import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.bricklink.service.BrickLinkService;
import com.mercu.bricklink.service.BrickLinkSetService;
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
    private BrickLinkService brickLinkService;
    @Autowired
    private BrickLinkSetService brickLinkSetService;
    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;

    @Autowired
    private LogService logService;

    @Test
    public void crawlSetInventory() {
        brickLinkSetService.saveSetItemList(
                brickLinkService.crawlSetInventoryBySetNo("71020"));
    }

    @Test
    public void existsSetItem() {
        System.out.println(brickLinkSetService.existsSetItem("149900"));
    }

    @Test
    public void crawlSetInventories() {
        for (int year = 2017; year >= 1953; year--) {
            logService.log("crawlSetInventories", "- year : " + year);
            List<SetInfo> setInfoList = brickLinkCatalogService.findSetInfoListByYear(String.valueOf(year));
            int index = 0;
            for (SetInfo setInfo : setInfoList) {
                index++;
                if (brickLinkSetService.existsSetItem(setInfo.getId())) {
                    logService.log("crawlSetInventories", "- year : " + year + ", " + index + "/" + setInfoList.size() + " - exists - skipped!");
                    continue;
                }
                logService.log("crawlSetInventories", "- year : " + year + ", " + index + "/" + setInfoList.size() + " - setInfo : " + setInfo + " - start");
                brickLinkSetService.saveSetItemList(
                        brickLinkService.crawlSetInventoryBySetNo(setInfo.getSetNo()));
                logService.log("crawlSetInventories", "- year : " + year + ", " + index + "/" + setInfoList.size() + " - setInfo : " + setInfo + " - finish");
            }
        }
    }

}
