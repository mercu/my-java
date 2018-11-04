package com.mercu.bricklink.repository.category;

import com.mercu.bricklink.model.category.PartCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface PartCategoryRepository extends CrudRepository<PartCategory, Integer> {

    @Query(value = "select cr.id, p.img as repImgs " +
            "from ( " +
            "  select c.id, sp.itemNo, sp.cnt " +
            "    , ROW_NUMBER() OVER(PARTITION BY id ORDER BY cnt desc) AS crank " +
            "  from bl_part_category c " +
            "  left join ( " +
            "    select s.itemNo, p.categoryId, count(1) cnt " +
            "    from bl_set_item s " +
            "    join bl_part_info p on p.partNo = s.itemNo " +
            "    group by s.itemNo, p.categoryId " +
            "  ) sp on sp.categoryId = c.id " +
            ") cr " +
            "join bl_part_info p on p.partNo = cr.itemNo " +
            "where cr.crank <= 1 " +
            "  or (cr.cnt >= 100 and cr.crank <= 5) ",
            nativeQuery = true
    )
    List<Object[]> extractRepresentImagesAll();

    @Query(value = "select c.id, p.qty " +
            "from bl_part_category c " +
            "join ( " +
            "  select p.categoryId, sum(s.qty) qty " +
            "  from bl_set_item s " +
            "  join bl_part_info p on p.partNo = s.itemNo " +
            "  where s.categoryType = 'P' " +
            "  group by p.categoryId " +
            ") p on p.categoryId = c.id " +
            "where c.type = 'P' ",
            nativeQuery = true
    )
    List<Object[]> sumSetPartQuantity();
}
