package com.mercu.lego.repository.my;

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import lombok.extern.slf4j.Slf4j;

import com.mercu.config.AppConfig;
import com.mercu.lego.model.my.MyItem;

/**
 * @author 고종봉 (jongbong.ko@navercorp.com)
 */
@Slf4j
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {AppConfig.class})
public class MyItemRepositoryTest {
    @Autowired
    private MyItemRepository myItemRepository;

    @Test
    public void bean() {
        assertNotNull(myItemRepository);
    }

    @Test
    public void groupByWheres() {
        Pageable pageable = new PageRequest(0, 10);
        List<MyItem> results = myItemRepository.groupByWheres(pageable);
        log.info("*** results : {}", results);
    }


}
