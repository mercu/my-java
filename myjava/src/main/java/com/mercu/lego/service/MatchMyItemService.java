package com.mercu.lego.service;

import com.mercu.bricklink.repository.match.MatchMyItemSetItemRatioRepository;
import com.mercu.lego.repository.MatchMyItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MatchMyItemService {
    @Autowired
    private MatchMyItemRepository matchMyItemRepository;

    public List<String> findMatchIds() {
        return matchMyItemRepository.findMatchIds();
    }
}
