package com.mercu.bricklink.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

import com.mercu.bricklink.repository.info.SetInfoRepository;

@Slf4j
@Service
public class BrickLinkSetInfoService {
    @Autowired
    private SetInfoRepository setInfoRepository;

    public String findIdBySetNo(String setNo) {
        try {
            return setInfoRepository.findBySetNo(setNo).getId();
        } catch (Exception e) {
            log.error("findIdBySetNo failed! - setNo : {}", setNo, e);
        }
        return null;
    }

}
