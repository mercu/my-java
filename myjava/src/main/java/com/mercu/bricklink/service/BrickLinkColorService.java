package com.mercu.bricklink.service;

import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.repository.info.ColorInfoRepository;
import com.mercu.utils.UrlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
public class BrickLinkColorService {
    Logger logger = LoggerFactory.getLogger(BrickLinkColorService.class);

    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;

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

    public ColorInfo findColorById(String colorId) {
        return colorInfoRepository.findById(colorId).orElse(null);
    }

    public List<ColorInfo> allColors() {
        if (Objects.isNull(allColors)) {
            allColors = (List<ColorInfo>)colorInfoRepository.findAll();
        }
        return allColors;
    }

    public List<ColorPartImageUrl> findAllColorPartImgUrlsByPartNo(String partNo) {
        List<String> colorIds = colorInfoRepository.findByPartNo(partNo);

        String partImgUrl = brickLinkCatalogService.findPartByPartNo(partNo).getImg();

        return colorIds.stream()
                .map(colorId -> new ColorPartImageUrl(colorId,
                        UrlUtils.replaceLastPath(partImgUrl, colorId),
                        colorInfoRepository.findById(colorId).get().getName()))
                .collect(toList());
    }

    @Getter
    @Setter
    @ToString
    @AllArgsConstructor
    public class ColorPartImageUrl {
        private String colorId;
        private String imgUrl;
        private String colorName;
    }
}
