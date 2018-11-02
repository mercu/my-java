package com.mercu.bricklink.service;

import static java.util.stream.Collectors.*;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mercu.bricklink.model.category.MinifigCategory;
import com.mercu.bricklink.model.category.PartCategory;
import com.mercu.bricklink.model.category.SetCategory;
import com.mercu.bricklink.repository.category.MinifigCategoryRepository;
import com.mercu.bricklink.repository.category.PartCategoryRepository;
import com.mercu.bricklink.repository.category.SetCategoryRepository;

@Service
public class BrickLinkCategoryService {
    Logger logger = LoggerFactory.getLogger(BrickLinkCategoryService.class);

    @Autowired
    private PartCategoryRepository partCategoryRepository;
    @Autowired
    private SetCategoryRepository setCategoryRepository;
    @Autowired
    private MinifigCategoryRepository minifigCategoryRepository;

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
     * @return
     */
    public List<MinifigCategory> findMinifigCategoriesRoot() {
        List<MinifigCategory> minifigCategoriesAll = (List<MinifigCategory>)minifigCategoryRepository.findAll();
        return minifigCategoriesAll.stream()
                .filter(minifigCategory -> minifigCategory.getDepth() == 1)
                .collect(toList());
    }

}
