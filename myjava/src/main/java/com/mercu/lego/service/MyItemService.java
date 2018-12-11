package com.mercu.lego.service;

import com.mercu.lego.model.my.MyItem;
import com.mercu.lego.repository.my.MyItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class MyItemService {
    @Autowired
    private MyItemRepository myItemRepository;

    public List<MyItem> findList(String itemType, String itemNo, String colorId) {
        if (Objects.isNull(colorId)) return findList(itemType, itemNo);

        return myItemRepository.findList(itemType, itemNo, colorId);
    }

    public List<MyItem> findList(String itemType, String itemNo) {
        return myItemRepository.findList(itemType, itemNo);
    }
}
