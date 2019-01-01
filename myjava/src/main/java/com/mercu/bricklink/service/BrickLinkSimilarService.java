package com.mercu.bricklink.service;

import com.mercu.bricklink.model.similar.SimilarPart;
import com.mercu.bricklink.repository.similar.SimilarPartRepository;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
public class BrickLinkSimilarService {
    Logger logger = LoggerFactory.getLogger(BrickLinkSimilarService.class);

    @Autowired
    private SimilarPartRepository similarPartRepository;

    // 캐시 맵 (partNo, List<PartNo>)
    private Map<String, List<String>> similarPartNosCacheMap;

    private Map<String, List<String>> getSimilarPartNosCacheMap() {
        if (similarPartNosCacheMap == null) {
            initSimilarPartsCacheMap();
        }
        return similarPartNosCacheMap;
    }

    private void initSimilarPartsCacheMap() {
        List<SimilarPart> similarPartsAll = (List<SimilarPart>)similarPartRepository.findAll();

        similarPartNosCacheMap = new HashMap<>();
        Map<Integer, List<SimilarPart>> similarPartsGroup = similarPartsAll.stream()
                .collect(Collectors.groupingBy(
                        SimilarPart::getSimilarId, toList()
                ));
        similarPartsGroup.entrySet().stream()
                .forEach(similarPartGroupEntry -> {
                    List<SimilarPart> similarParts = similarPartGroupEntry.getValue();
                    similarParts.stream()
                            .forEach(similarPart -> {
                                String partNo = similarPart.getPartNo();
                                similarPartNosCacheMap.put(partNo, toSimilarPartNoList(similarParts));
                            });
                });
    }

    private List<String> toSimilarPartNoList(List<SimilarPart> similarParts) {
        return similarParts.stream()
                .map(similarPart -> similarPart.getPartNo())
                .collect(toList());
    }

    /**
     * partNo 로 유사 partNo 목록을 조회
     * (입력한 partNo는 제외)
     * @param partNo
     * @return
     */
    public List<String> findPartNosCached(String partNo) {
        List<String> partNos = getSimilarPartNosCacheMap().get(partNo);
        if (CollectionUtils.isEmpty(partNos)) {
            return Arrays.asList(new String[]{partNo});
        } else {
            return partNos;
        }
//        return similarPartRepository.findPartNosCached(partNo);
    }

    public String findRepresentPartNo(String partNo) {
        return similarPartRepository.findRepresentPartNo(partNo);
    }

    /**
     * @param partNo1
     * @param partNo2
     * @return
     */
    public boolean compareWithSimilarPartNos(String partNo1, String partNo2) {
        List<String> similarPartNos = findPartNosCached(partNo1);
        if (similarPartNos == null) {
            return StringUtils.equals(partNo1, partNo2);
        } else {
            return similarPartNos.contains(partNo2);
        }
    }
}
