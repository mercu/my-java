package com.mercu.lego.service;

import com.google.gson.reflect.TypeToken;
import com.mercu.bricklink.model.CategoryType;
import com.mercu.lego.model.MyPartCategory;
import com.mercu.lego.repository.MyPartCategoryRepository;
import com.mercu.utils.JsonUtils;
import com.mercu.utils.NumberUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.toList;

@Service
public class MyCategoryService {
    @Autowired
    private MyPartCategoryRepository myPartCategoryRepository;

    public List<MyPartCategory> findPartCategoriesAll() {
        return (List<MyPartCategory>)myPartCategoryRepository.findAll();
    }

    /**
     * @param depth
     * @return
     */
    public List<MyPartCategory> findPartCategoriesByDepth(Integer depth) {
        return myPartCategoryRepository.findByDepth(depth).stream()
                .map(myPartCategory -> propagateChildRepresentImageUrls(myPartCategory))
                .collect(toList());
    }

    private MyPartCategory propagateChildRepresentImageUrls(MyPartCategory myPartCategory) {
        if (StringUtils.isBlank(myPartCategory.getRepImgs())) {
            myPartCategory.setRepImgs(JsonUtils.toJson(
                    findPartCategoriesByParentId(myPartCategory.getId()).stream()
                            .map(childPartCategory -> firstRepresentImageUrl(childPartCategory))
                            .filter(imgUrl -> Objects.nonNull(imgUrl))
                            .collect(toList())
            ));
        }
        return myPartCategory;
    }

    public List<MyPartCategory> findPartCategoriesByParentId(Integer parentCategoryId) {
        List<MyPartCategory> partCategories = myPartCategoryRepository.findByParentCategoryId(parentCategoryId);
        partCategories = partCategories.stream()
                .map(myPartCategory -> {
                    if (Objects.nonNull(myPartCategory.getBlCategoryId())) {

                    }
                    return propagateChildRepresentImageUrls(myPartCategory);
                })
                .sorted(Comparator.comparing(myPartCategory -> NumberUtils.intValueDefault(((MyPartCategory)myPartCategory).getSortOrder(), 0))
                        .thenComparing(myPartCategory -> NumberUtils.intValueDefault(((MyPartCategory)myPartCategory).getSetQty(), 0)).reversed())
                .collect(toList());
        return partCategories;
    }

    /**
     * @param id
     * @return
     */
    public MyPartCategory findById(Integer id) {
        MyPartCategory myPartCategory = myPartCategoryRepository.findById(id).orElse(null);
        return myPartCategory;
    }

    private String repImg(MyPartCategory myPartCategory) {
        if (StringUtils.isBlank(myPartCategory.getRepImgs())) return null;

        List<String> repImgs = JsonUtils.toObject(myPartCategory.getRepImgs(), new TypeToken<ArrayList<String>>(){}.getType());
        if (repImgs.isEmpty()) {
            return null;
        } else {
            return repImgs.get(0);
        }
    }

    /**
     * @param blCategoryId
     * @return
     */
    public MyPartCategory findByBlCategoryId(Integer blCategoryId) {
        return myPartCategoryRepository.findByBlCategoryId(blCategoryId);
    }

    private String firstRepresentImageUrl(MyPartCategory childPartCategory) {
        List<String> imgUrls = JsonUtils.toObject(
                childPartCategory.getRepImgs(),
                new TypeToken<ArrayList<String>>(){}.getType());

        if (Objects.nonNull(imgUrls) && !imgUrls.isEmpty()) {
            return imgUrls.get(0);
        } else {
            return null;
        }
    }

    /**
     * 최상위 카테고리를 구함 By BlCategoryId
     * @param blCategoryId
     * @return
     */
    public MyPartCategory findRootCategoryByBlCategoryId(Integer blCategoryId) {
        MyPartCategory myPartCategory = myPartCategoryRepository.findByBlCategoryId(blCategoryId);

        if (myPartCategory.getDepth() == 0) return myPartCategory;
        return findRootCategory(myPartCategory.getParentId());
    }

    /**
     * 최상위 카테고리를 구함 By categoryId
     * @param categoryId
     * @return
     */
    public MyPartCategory findRootCategory(Integer categoryId) {
        MyPartCategory myPartCategory = myPartCategoryRepository.findById(categoryId).get();

        if (myPartCategory.getDepth() == 0) return myPartCategory;
        return findRootCategory(myPartCategory.getParentId());
    }

    public void save(MyPartCategory myPartCategory) {
        myPartCategoryRepository.save(myPartCategory);
    }

    public void newPartCatergory(String name, Integer parentId) {
        MyPartCategory myPartCategory = new MyPartCategory();
        myPartCategory.setName(name);
        myPartCategory.setParentId(parentId);
        myPartCategory.setType(CategoryType.P.getCode());
        myPartCategory.setDepth(parentId == 0 ? 0 : myPartCategoryRepository.findById(parentId).get().getDepth() + 1);

        myPartCategoryRepository.save(myPartCategory);
    }

    public void movePartCatergory(Integer categoryIdFrom, Integer parentIdTo) {
        // BL 카테고리 하위로는 옮길 수 없다!
        if (isBlCategory(parentIdTo)) {
            throw new RuntimeException("BL 카테고리 하위로는 옮길 수 없습니다!");
        }

        // 카테고리 이동
        MyPartCategory targetPartCategory = myPartCategoryRepository.findById(categoryIdFrom).get();
        Integer parentIdFrom = targetPartCategory.getParentId();
        targetPartCategory.setParentId(parentIdTo);
        targetPartCategory.setDepth(parentIdTo == 0 ? 0 : myPartCategoryRepository.findById(parentIdTo).get().getDepth() + 1);

        myPartCategoryRepository.save(targetPartCategory);

        // 부품 수 재계산 (parts, setQty)
        sumChildParts(parentIdFrom);
        sumChildParts(parentIdTo);
    }

    private boolean isBlCategory(Integer categoryId) {
        MyPartCategory myPartCategory = myPartCategoryRepository.findById(categoryId).get();
        return Objects.nonNull(myPartCategory.getBlCategoryId()) && myPartCategory.getBlCategoryId() > 0;
    }

    public void sumChildParts(Integer categoryId) {
        if (categoryId == 0) return;
        if (isBlCategory(categoryId)) {
            throw new RuntimeException("BL 카테고리는 재계산 할 수 없습니다!");
        }

        List<MyPartCategory> childCategories = myPartCategoryRepository.findByParentCategoryId(categoryId);

        MyPartCategory myPartCategory = myPartCategoryRepository.findById(categoryId).get();
        myPartCategory.setParts(childCategories.stream()
                .filter(childCategory -> Objects.nonNull(childCategory.getParts()))
                .mapToInt(childCategory -> childCategory.getParts())
                .sum());
        myPartCategory.setSetQty(childCategories.stream()
                .filter(childCategory -> Objects.nonNull(childCategory.getSetQty()))
                .mapToInt(childCategory -> childCategory.getSetQty())
                .sum());

        myPartCategoryRepository.save(myPartCategory);
    }

}
