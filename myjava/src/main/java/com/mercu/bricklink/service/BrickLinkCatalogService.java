package com.mercu.bricklink.service;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.info.*;
import com.mercu.bricklink.model.map.SetItem;
import com.mercu.lego.model.my.MyItemGroup;
import com.mercu.bricklink.repository.info.ColorInfoRepository;
import com.mercu.bricklink.repository.info.MinifigInfoRepository;
import com.mercu.bricklink.repository.info.PartInfoRepository;
import com.mercu.bricklink.repository.info.SetInfoRepository;
import com.mercu.bricklink.repository.map.SetItemRepository;
import com.mercu.lego.repository.my.MyItemRepository;
import com.mercu.log.LogService;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Service
public class BrickLinkCatalogService {
    Logger logger = LoggerFactory.getLogger(BrickLinkCatalogService.class);

    @Autowired
    private BrickLinkMyService brickLinkMyService;

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
    private MyItemRepository myItemRepository;

    @Autowired
    private LogService logService;

    // setId와 setNo 매핑용 맵
    private Map<String, String> setIdNoCacheMap = new HashMap<>();

    public List<PartInfo> findPartInfoListByCategoryId(Integer categoryId) {
        return partInfoRepository.findAllByCategoryId(categoryId);
    }

    /**
     * @param blCategoryId
     * @param limit
     * @return
     */
    public List<PartInfo> findPartInfoListByCategoryId(Integer blCategoryId, Integer limit) {
        if (Objects.isNull(limit)) limit = Integer.MAX_VALUE;
        Pageable pageable = new PageRequest(0, limit);
        List<PartInfo> partInfoList = partInfoRepository.findAllByCategoryId(blCategoryId, pageable);

        return partInfoList;
    }

    /**
     * @param blCategoryId
     * @param limit
     * @return
     */
    public List<PartInfo> findPartInfoListByCategoryIdWithMyItems(Integer blCategoryId, Integer limit) {
        List<PartInfo> partInfoList = findPartInfoListByCategoryId(blCategoryId, limit);

        // with MyItems (itemType, itemNo)
        partInfoList.stream()
                .forEach(partInfo -> {
                    partInfo.setMyItemGroups(
                            brickLinkMyService.findMyItemsGroup(
                                    brickLinkMyService.findMyItems(partInfo.getItemType(), partInfo.getPartNo())
                            ));
                    partInfo.setMyItemsQty(
                            partInfo.getMyItemGroups().stream()
                                    .mapToInt(MyItemGroup::getQty)
                                    .sum()
                    );
                });

        return partInfoList;
    }

    public List<SetInfo> findSetInfoListByYear(String year) {
        return setInfoRepository.findAllByYear(year);
    }

    /**
     * @param setNo
     * @return
     */
    public SetInfo findSetInfoBySetNo(String setNo) {
        return setInfoRepository.findByBlSetNo(setNo + "-1");
    }

    /**
     * @param setId
     * @return
     */
    public SetInfo findSetInfo(String setId) {
        return setInfoRepository.findById(setId).orElse(null);
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
        int index = 0;
        for (AbstractInfo info : infoList) {
            index++;
            logService.log("saveInfoList", index + "/" + infoList.size() + " - info : " + info);
            switch (categoryType) {
                case S:
                    updateSetInfo((SetInfo)info);
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

    private void updateSetInfo(SetInfo setInfo) {
        SetInfo savedSetInfo = setInfoRepository.findById(setInfo.getId()).orElse(null);
        if (Objects.isNull(savedSetInfo)) {
            logService.log("updateSetInfo", "not found setInfo! - setInfo : " + setInfo, "not found!");
        } else {
            if(StringUtils.equals(setInfo.getSetNo(), savedSetInfo.getSetNo()) == false) {
                logService.log("updateSetInfo", "setNo update! - saved : " + savedSetInfo.getSetNo() + ", new : " + setInfo.getSetNo(), "setNo update!");
            }
        }
        setInfoRepository.save(setInfo);
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

    /**
     * @param partNo
     * @return
     */
    public PartInfo findPartByPartNo(String partNo) {
        return partInfoRepository.findByPartNo(partNo).orElse(null);
    }

    /**
     * @param itemNo
     * @return
     */
    public MinifigInfo findMinifigByPartNo(String itemNo) {
        return minifigInfoRepository.findByMinifigNo(itemNo).orElse(null);
    }

    /**
     * setId로 setNo를 구함 (캐싱됨)
     * @param setId
     * @return
     */
    public String setNoBySetIdCached(String setId) {
        return getSetIdNoCacheMap().get(setId);
    }

    public String setIdBySetNoCached(String setNo) {
        return getSetIdNoCacheMap().entrySet().stream()
                .filter(entry -> StringUtils.equals(entry.getValue(), setNo))
                .findFirst()
                .get().getKey();
    }

    public Map<String, String> getSetIdNoCacheMap() {
        if (CollectionUtils.isEmpty(setIdNoCacheMap)) {
            setInfoRepository.findAll()
                    .forEach(setInfo -> setIdNoCacheMap.put(setInfo.getId(), setInfo.getSetNo()));
        }
        return setIdNoCacheMap;
    }

}
