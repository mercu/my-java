package com.mercu.bricklink.service;

import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.repository.ColorInfoRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class BrickLinkColorService {
    Logger logger = LoggerFactory.getLogger(BrickLinkColorService.class);

    @Autowired
    private ColorInfoRepository colorInfoRepository;

    private List<ColorInfo> allColors;

    public ColorInfo findColor(String desc) {
        for (ColorInfo colorInfo : allColors()) {
            if (desc.startsWith(colorInfo.getName())) {
                return colorInfo;
            }
        }
        return null;
    }

    public List<ColorInfo> allColors() {
        if (Objects.isNull(allColors)) {
            allColors = (List<ColorInfo>)colorInfoRepository.findAll();
        }
        return allColors;
    }
}
