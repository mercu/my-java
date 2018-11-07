package com.mercu.lego.repository;

import com.mercu.lego.model.MyPartCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MyPartCategoryRepository extends CrudRepository<MyPartCategory, Integer> {
    @Query("select m from MyPartCategory m where m.depth = :depth")
    List<MyPartCategory> findByDepth(@Param("depth") Integer depth);

    @Query("select m from MyPartCategory m where m.parentId = :parentId order by m.setQty desc, m.parts desc")
    List<MyPartCategory> findByParentCategoryId(@Param("parentId") Integer parentCategoryId);

    @Query("select m from MyPartCategory m where m.blCategoryId = :blCategoryId")
    MyPartCategory findByBlCategoryId(@Param("blCategoryId") Integer blCategoryId);
}
