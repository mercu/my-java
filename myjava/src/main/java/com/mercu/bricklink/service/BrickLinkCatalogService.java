package com.mercu.bricklink.service;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.info.*;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.bricklink.repository.info.ColorInfoRepository;
import com.mercu.bricklink.repository.info.MinifigInfoRepository;
import com.mercu.bricklink.repository.info.PartInfoRepository;
import com.mercu.bricklink.repository.info.SetInfoRepository;
import com.mercu.bricklink.repository.map.SetItemRepository;
import com.mercu.log.LogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BrickLinkCatalogService {
    Logger logger = LoggerFactory.getLogger(BrickLinkCatalogService.class);

    @Autowired
    private SetInfoRepository setInfoRepository;
    @Autowired
    private PartInfoRepository partInfoRepository;
    @Autowired
    private MinifigInfoRepository minifigInfoRepository;
    @Autowired
    private ColorInfoRepository colorInfoRepository;
    @Autowired
    private SetItemRepository setItemRepository;

    @Autowired
    private LogService logService;

    public List<PartInfo> findPartInfoListByCategoryId(Integer categoryId) {
        return partInfoRepository.findAllByCategoryId(categoryId);
    }

    public List<PartInfo> findPartInfoListByCategoryIdLimit(Integer categoryId, Integer limit) {
        if (Objects.isNull(limit)) limit = Integer.MAX_VALUE;
        Pageable pageable = new PageRequest(0, limit);
        return partInfoRepository.findAllByCategoryId(categoryId, pageable);
    }

    public List<SetInfo> findSetInfoListByYear(String year) {
        return setInfoRepository.findAllByYear(year);
    }

    /**
     * @param setInfoList
     */
    public void saveSetInfoList(List<SetInfo> setInfoList) {
        saveInfoList(setInfoList, CategoryType.S);
    }

    /**
     * @param partInfoList
     */
    public void savePartInfoList(List<PartInfo> partInfoList) {
        saveInfoList(partInfoList, CategoryType.P);
    }

    /**
     * @param minifigInfoList
     */
    public void saveMinifigInfoList(List<MinifigInfo> minifigInfoList) {
        saveInfoList(minifigInfoList, CategoryType.M);
    }

    public void saveInfoList(List<? extends AbstractInfo> infoList, CategoryType categoryType) {
        for (AbstractInfo info : infoList) {
            switch (categoryType) {
                case S:
                    setInfoRepository.save((SetInfo)info);
                    break;
                case P:
                    partInfoRepository.save((PartInfo)info);
                    break;
                case M:
                    minifigInfoRepository.save((MinifigInfo)info);
                    break;
            }
        }
    }

    /**
     * @param colorInfoList
     */
    public void saveColorInfoList(List<ColorInfo> colorInfoList) {
        colorInfoRepository.saveAll(colorInfoList);
    }

    /**
     * 부품별 세트내 총 수량 개수를 업데이트 한다.
     */
    public void updatePartInfoSetQty() {
        logService.log("updatePartInfoSetQty", "=== start !");

        List<PartInfo> partInfoAll = (List<PartInfo>)partInfoRepository.findAll();
        logService.log("updatePartInfoSetQty", "partInfoAll : " + partInfoAll.size());

        int index = 0;
        for (PartInfo partInfo : partInfoAll) {
            index++;
            List<SetItem> setItems = setItemRepository.findByItemNo(partInfo.getPartNo());
            partInfo.setSetQty(setItems.stream()
                    .mapToInt(SetItem::getQty)
                    .sum());
            logService.log(index + "/" + partInfoAll.size() + " - updatePartInfoSetQty", "partInfo : " + partInfo);
            partInfoRepository.save(partInfo);
        }

        logService.log("updatePartInfoSetQty", "=== finish !");
    }
}
