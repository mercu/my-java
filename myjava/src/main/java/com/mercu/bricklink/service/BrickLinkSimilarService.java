package com.mercu.bricklink.service;

import com.mercu.bricklink.repository.similar.SimilarPartRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrickLinkSimilarService {
    Logger logger = LoggerFactory.getLogger(BrickLinkSimilarService.class);

    @Autowired
    private SimilarPartRepository similarPartRepository;

    /**
     * partNo 로 유사 partNo 목록을 조회
     * (입력한 partNo는 제외)
     * @param partNo
     * @return
     */
    public List<String> findPartNos(String partNo) {
        return similarPartRepository.findPartNos(partNo);
    }

    public String findRepresentPartNo(String partNo) {
        return similarPartRepository.findRepresentPartNo(partNo);
    }
}
