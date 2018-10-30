package com.mercu.bricklink;

import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.bricklink.service.BrickLinkCatalogService;
import com.mercu.bricklink.service.BrickLinkService;
import com.mercu.bricklink.service.BrickLinkSetService;
import com.mercu.config.AppConfig;
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

    @Test
    public void crawlSetInventory() {
        brickLinkSetService.saveSetItemList(
                brickLinkService.crawlSetInventoryBySetNo("71020"));
    }

    @Test
    public void crawlSetInventories() {
        List<SetInfo> setInfoList = brickLinkCatalogService.findSetInfoListByYear("2018");
        int index = 0;
        for (SetInfo setInfo : setInfoList) {
            index++;
            logger.info("* {}/{} - setInfo : {} - start", index, setInfoList.size(), setInfo);
            brickLinkSetService.saveSetItemList(
                    brickLinkService.crawlSetInventoryBySetNo(setInfo.getSetNo()));
            logger.info("* {}/{} - setInfo : {} - finish", index, setInfoList.size(), setInfo);
        }
    }

}
