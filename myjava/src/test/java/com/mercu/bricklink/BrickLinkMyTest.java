package com.mercu.bricklink;

import com.mercu.lego.model.my.MyItem;
import com.mercu.lego.repository.my.MyItemRepository;
import com.mercu.bricklink.service.BrickLinkMyService;
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
public class BrickLinkMyTest {
    private static final Logger logger = LoggerFactory.getLogger(BrickLinkMyTest.class);

    @Autowired
    private BrickLinkMyService brickLinkMyService;

    @Autowired
    private MyItemRepository myItemRepository;

    @Test
    public void myList() {
        List<MyItem> myItemList = (List<MyItem>)myItemRepository.findAll();
        System.out.println("myItemList : " + myItemList);

    }

    @Test
    public void addMyListBySetNo() {
        brickLinkMyService.addMyListBySetNo("10243", "virtual");
    }

    @Test
    public void mapMyItemToSet() {
        brickLinkMyService.mapMyItemToSet("181204-1");
    }

    @Test
    public void mapMyItemToSetRatio() {
        brickLinkMyService.mapMyItemToSetRatio("181204-1");
    }

    @Test
    public void mapMyItemToSetAndRatio() {
        brickLinkMyService.mapMyItemToSet("181218-2");
        brickLinkMyService.mapMyItemToSetRatio("181218-2");

    }


}
