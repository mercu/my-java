package com.mercu.lego.service;

import static com.mercu.lego.model.my.MyItem.*;

import java.util.List;
import java.util.Objects;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.mercu.bricklink.model.map.SetItemId;
import com.mercu.bricklink.service.BrickLinkSetInfoService;
import com.mercu.lego.model.my.MyItem;
import com.mercu.lego.repository.my.MyItemRepository;

@Service
public class MyItemService {
    @Autowired
    private BrickLinkSetInfoService brickLinkSetInfoService;

    @Autowired
    private MyItemRepository myItemRepository;

    public List<MyItem> findList(String itemNo, String colorId) {
        if (Objects.isNull(colorId)) return findList(itemNo);

        return myItemRepository.findList(itemNo, colorId);
    }

    public List<MyItem> findList(String itemNo) {
        return myItemRepository.findList(itemNo);
    }

    /**
     * 보관중인 아이템 목록-위치 전체
     * @return
     */
    public List<MyItem> groupByWheres() {
        Pageable pageable = new PageRequest(0, 1000);
        List<MyItem> myWheres = myItemRepository.groupByWheres(pageable);

        // 세트 이미지
        myWheres.stream()
            .forEach(myWhere -> {
                if (StringUtils.equals(WHERE_CODE_WANTED, myWhere.getWhereCode())) {
                    myWhere.setImgUrl("https://img.bricklink.com/ItemImage/ST/0/" + myWhere.getWhereMore() + "-1.t2.png");
                    myWhere.setLinkUrl("https://www.bricklink.com/v2/catalog/catalogitem.page?id=" + brickLinkSetInfoService.findIdBySetNo(myWhere.getWhereMore()) + "#T=I");
                }
            });

        return myWheres;
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
