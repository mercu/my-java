package com.mercu.lego;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mercu.config.AppConfig;
import com.mercu.lego.service.MatchMyItemService;
import com.mercu.log.LogService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class MatchMyItemTest {
    private static final Logger logger = LoggerFactory.getLogger(MatchMyItemTest.class);

    @Autowired
    private MatchMyItemService matchMyItemService;

    @Autowired
    private LogService logService;

    @Test
    public void updateMatchSetRatio() {
        matchMyItemService.updateMatchSetRatio("76007", "181216-2");
    }

    @Test
    public void updateMatchSetParts() {
        matchMyItemService.updateMatchSetParts("58176", "108", "76007", "181216-2");
    }

    @Test
    public void updateMatchSet() {
        matchMyItemService.updateMatchSet("76007", "181216-2");
    }

}
