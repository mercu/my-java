package com.mercu.lego.service;

import static com.mercu.lego.model.my.MyItem.*;

import java.util.List;
import java.util.Objects;

import com.mercu.bricklink.model.map.SetItemId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercu.lego.model.my.MyItem;
import com.mercu.lego.repository.my.MyItemRepository;

@Service
public class MyItemService {
    @Autowired
    private MyItemRepository myItemRepository;

    public List<MyItem> findList(String itemNo, String colorId) {
        if (Objects.isNull(colorId)) return findList(itemNo);

        return myItemRepository.findList(itemNo, colorId);
    }

    public List<MyItem> findList(String itemNo) {
        return myItemRepository.findList(itemNo);
    }

    public MyItem findByIdWhereWanted(SetItemId setItemId, String setNo) {
        return myItemRepository.findByIdWhere(setItemId.getItemNo(), setItemId.getColorId(), WHERE_CODE_WANTED, setNo);
    }

    public String whereCodeFromWhereValue(String whereValue) {
        if (Objects.isNull(whereValue)) return null;
        else return whereValue.split("-")[0];
    }

    public String whereMoreFromWhereValue(String whereValue) {
        if (Objects.isNull(whereValue)) return null;
        else return whereValue.substring(whereValue.indexOf("-") + 1);
    }
}
