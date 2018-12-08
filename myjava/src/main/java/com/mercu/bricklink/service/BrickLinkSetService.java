package com.mercu.bricklink.service;

import com.mercu.bricklink.model.map.SetItem;
import com.mercu.bricklink.repository.map.SetItemRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BrickLinkSetService {
    Logger logger = LoggerFactory.getLogger(BrickLinkSetService.class);

    @Autowired
    private SetItemRepository setItemRepository;

    public List<SetItem> findBySetId(String setId) {
        return setItemRepository.findBySetId(setId);
    }

    public void saveSetItemList(List<SetItem> setItemList) {
        setItemRepository.saveAll(setItemList);
    }

    public boolean existsSetItem(String setId) {
        return setItemRepository.existsBySetId(setId);
    }
}
