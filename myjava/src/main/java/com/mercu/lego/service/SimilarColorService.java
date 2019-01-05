package com.mercu.lego.service;

import com.mercu.bricklink.model.similar.SimilarColor;
import com.mercu.bricklink.repository.similar.SimilarColorRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class SimilarColorService {
    @Autowired
    private SimilarColorRepository similarColorRepository;

    // 캐시 맵 (colorId, List<colorId>)
    private Map<String, List<String>> similarColorIdsCacheMap;

    private Map<String, List<String>> getSimilarColorIdsCacheMap() {
        if (similarColorIdsCacheMap == null) {
            initSimilarColorsCacheMap();
        }
        return similarColorIdsCacheMap;
    }

    private void initSimilarColorsCacheMap() {
        List<SimilarColor> similarColorsAll = (List<SimilarColor>)similarColorRepository.findAll();

        similarColorIdsCacheMap = new HashMap<>();
        Map<Integer, List<SimilarColor>> similarColorsGroup = similarColorsAll.stream()
                .collect(Collectors.groupingBy(
                        SimilarColor::getSimilarId, toList()
                ));
        similarColorsGroup.entrySet().stream()
                .forEach(similarColorGroupEntry -> {
                    List<SimilarColor> similarColors = similarColorGroupEntry.getValue();
                    similarColors.stream()
                            .forEach(similarColor -> {
                                String colorId = similarColor.getColorId();
                                similarColorIdsCacheMap.put(colorId, toSimilarColorIdList(similarColors));
                            });
                });
    }

    private List<String> toSimilarColorIdList(List<SimilarColor> similarColors) {
        return similarColors.stream()
                .map(similarColor -> similarColor.getColorId())
                .collect(toList());
    }

    /**
     * colorId 로 유사 colorId 목록을 조회
     * (입력한 colorId는 제외)
     * @param colorId
     * @return
     */
    public List<String> findColorIdsCached(String colorId) {
        List<String> colorIds = getSimilarColorIdsCacheMap().get(colorId);
        if (CollectionUtils.isEmpty(colorIds)) {
            return Arrays.asList(new String[]{colorId});
        } else {
            return colorIds;
        }
//        return similarColorRepository.findColorIdsCached(colorId);
    }

    public String findRepresentColorId(String colorId) {
        return similarColorRepository.findRepresentColorId(colorId);
    }

    /**
     * @param colorId1
     * @param colorId2
     * @return
     */
    public boolean compareWithSimilarColorIds(String colorId1, String colorId2) {
        List<String> similarColorIds = findColorIdsCached(colorId1);
        if (similarColorIds == null) {
            return StringUtils.equals(colorId1, colorId2);
        } else {
            return similarColorIds.contains(colorId2);
        }
    }
}
