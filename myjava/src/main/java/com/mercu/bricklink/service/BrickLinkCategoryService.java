package com.mercu.bricklink.service;

import com.google.gson.reflect.TypeToken;
import com.mercu.bricklink.model.category.MinifigCategory;
import com.mercu.bricklink.model.category.PartCategory;
import com.mercu.bricklink.model.category.SetCategory;
import com.mercu.bricklink.repository.category.MinifigCategoryRepository;
import com.mercu.bricklink.repository.category.PartCategoryRepository;
import com.mercu.bricklink.repository.category.SetCategoryRepository;
import com.mercu.log.LogService;
import com.mercu.utils.JsonUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toList;

@Service
public class BrickLinkCategoryService {
    Logger logger = LoggerFactory.getLogger(BrickLinkCategoryService.class);

    @Autowired
    private PartCategoryRepository partCategoryRepository;
    @Autowired
    private SetCategoryRepository setCategoryRepository;
    @Autowired
    private MinifigCategoryRepository minifigCategoryRepository;

    @Autowired
    private LogService logService;

    /**
     * @return
     */
    public List<SetCategory> findSetCategoriesAll() {
        return (List<SetCategory>)setCategoryRepository.findAll();
    }

    /**
     * @return
     */
    public List<PartCategory> findPartCategoriesAll() {
        return (List<PartCategory>)partCategoryRepository.findAll();
    }

    /**
     * 부품 카테고리의 세트내 부품 수량 전체를 계산한다.
     */
    public void updatePartCategorySetQty() {
        logService.log("updatePartCategorySetQty", "=== start !");

        List<Object[]> partQtys = partCategoryRepository.sumSetPartQuantity();
        logService.log("updatePartCategorySetQty", "partQtys : " + partQtys.size());

        partQtys.stream()
                .forEach(partQty -> {
                    Integer categoryId = (Integer)partQty[0];
                    Integer setQty = ((BigDecimal)partQty[1]).intValue();
                    logService.log("updatePartCategorySetQty", "categoryId : " + categoryId + ", setQty : "+ setQty);

                    PartCategory partCategory = partCategoryRepository.findById(categoryId).get();
                    partCategory.setSetQty(setQty);
                    partCategoryRepository.save(partCategory);
                });

        logService.log("updatePartCategorySetQty", "=== finish !");
    }
    /**
     * 부품 카테고리별 대표 이미지를 추출한다.
     */
    public void autoUpdatePartCategoryRepresentImagesAll() {
        List<Object[]> partCategories = partCategoryRepository.extractRepresentImagesAll();
        logService.log("autoUpdatePartCategoryRepresentImagesAll", "partCategories : " + partCategories.size());

        Map<Integer, List<String>> partCategoryImagesMap = partCategories.stream()
                .map(object -> {
                    PartCategory partCategory = new PartCategory();
                    partCategory.setId((Integer)object[0]);
                    partCategory.setRepImgs((String)object[1]);
                    return partCategory;
                })
                .collect(
                        Collectors.groupingBy(PartCategory::getId,
                                Collectors.mapping(PartCategory::getRepImgs, Collectors.toList())
                ));
        logService.log("autoUpdatePartCategoryRepresentImagesAll", "partCategoryImagesMap : " + partCategoryImagesMap.keySet());

        partCategoryImagesMap.entrySet().stream()
                .forEach(entry -> {
                    logService.log("autoUpdatePartCategoryRepresentImagesAll", "categoryId : " + entry.getKey() + ", repImgs : " + JsonUtils.toJson(entry.getValue()));
                    PartCategory partCategory = partCategoryRepository.findById(entry.getKey()).get();
                    partCategory.setRepImgs(JsonUtils.toJson(entry.getValue()));
                    partCategoryRepository.save(partCategory);
                });
    }
    /**
     * @return
     */
    public List<MinifigCategory> findMinifigCategoriesRoot() {
        List<MinifigCategory> minifigCategoriesAll = (List<MinifigCategory>)minifigCategoryRepository.findAll();
        return minifigCategoriesAll.stream()
                .filter(minifigCategory -> minifigCategory.getDepth() == 1)
                .collect(toList());
    }

    /**
     * @param partCategoryList
     */
    public void savePartCategoryList(List<PartCategory> partCategoryList) {
        partCategoryRepository.saveAll(partCategoryList);
    }

    /**
     * @param setCategoryList
     */
    public void saveSetCategoryList(List<SetCategory> setCategoryList) {
        setCategoryRepository.saveAll(setCategoryList);
    }

    /**
     * @param minifigCategoryList
     */
    public void saveMinifigCategoryList(List<MinifigCategory> minifigCategoryList) {
        minifigCategoryRepository.saveAll(minifigCategoryList);
    }

    /**
     * @param categoryId
     * @param representImageUrl
     */
    public void addPartCategoryRepresentImage(Integer categoryId, String representImageUrl) {
        List<String> representImageUrls = new ArrayList<>();

        String repImgsJson = partCategoryRepository.findById(categoryId).get().getRepImgs();
        if (StringUtils.isNotBlank(repImgsJson)) {
            representImageUrls = JsonUtils.toObject(repImgsJson, new TypeToken<ArrayList<String>>(){}.getType());
        }

        representImageUrls.add(representImageUrl);

        savePartCategoryRepresentImages(categoryId, representImageUrls);
    }

    public void savePartCategoryRepresentImages(Integer categoryId, List<String> representImageUrls) {
        PartCategory partCategory = partCategoryRepository.findById(categoryId).get();
        partCategory.setRepImgs(JsonUtils.toJson(representImageUrls));
        partCategoryRepository.save(partCategory);
    }

}
