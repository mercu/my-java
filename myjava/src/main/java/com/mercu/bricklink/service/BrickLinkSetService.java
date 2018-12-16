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

    /**
     * 세트 부품 정보
     * @param setId
     * @param itemNo
     * @param colorId
     * @return
     */
    public SetItem findSetPart(String setId, String itemNo, String colorId) {
        return setItemRepository.findSetPart(setId, itemNo, colorId);
    }

    public void saveSetItemList(List<SetItem> setItemList) {
        setItemRepository.saveAll(setItemList);
    }

    public boolean existsSetItem(String setId) {
        return setItemRepository.existsBySetId(setId);
    }

    /**
     * 세트 전체 부품 종류 수
     * @param setId
     * @param itemType
     * @return
     */
    public int countItemsBySetId(String setId, String itemType) {
        return setItemRepository.countItemsBySetId(setId, itemType);
    }

}
