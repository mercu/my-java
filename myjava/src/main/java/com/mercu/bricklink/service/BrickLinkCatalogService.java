package com.mercu.bricklink.service;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercu.bricklink.model.CategoryType;
import com.mercu.bricklink.model.info.AbstractInfo;
import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.model.info.MinifigInfo;
import com.mercu.bricklink.model.info.PartInfo;
import com.mercu.bricklink.model.info.SetInfo;
import com.mercu.bricklink.repository.info.ColorInfoRepository;
import com.mercu.bricklink.repository.info.MinifigInfoRepository;
import com.mercu.bricklink.repository.info.PartInfoRepository;
import com.mercu.bricklink.repository.info.SetInfoRepository;

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

}
