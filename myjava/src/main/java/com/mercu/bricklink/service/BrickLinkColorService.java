package com.mercu.bricklink.service;

import com.mercu.bricklink.model.info.ColorInfo;
import com.mercu.bricklink.repository.info.ColorInfoRepository;
import com.mercu.utils.UrlUtils;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
public class BrickLinkColorService {
    @Autowired
    private BrickLinkCatalogService brickLinkCatalogService;

    @Autowired
    private ColorInfoRepository colorInfoRepository;

    private List<ColorInfo> allColorsCache;

    public ColorInfo findColorCached(String desc) {
        for (ColorInfo colorInfo : allColorsCache()) {
            if (desc.startsWith(colorInfo.getName())) {
                return colorInfo;
            }
        }
        return null;
    }

    public ColorInfo findColorByIdCached(String colorId) {
        return allColorsCache().stream()
                .filter(colorInfo -> StringUtils.equals(colorId, colorInfo.getId()))
                .findFirst()
                .orElse(null);
    }

    public List<ColorInfo> allColorsCache() {
        if (Objects.isNull(allColorsCache)) {
            allColorsCache = (List<ColorInfo>)colorInfoRepository.findAll();
        }
        return allColorsCache;
    }

    public List<ColorPartImageUrl> findAllColorPartImgUrlsByPartNo(String partNo) {
        List<String> colorIds = colorInfoRepository.findByPartNo(partNo);

        String partImgUrl = brickLinkCatalogService.findPartByPartNo(partNo).getImg();

        return colorIds.stream()
                .map(colorId -> new ColorPartImageUrl(colorId,
                        UrlUtils.replaceLastPath(partImgUrl, colorId),
                        findColorByIdCached(colorId).getName()))
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
