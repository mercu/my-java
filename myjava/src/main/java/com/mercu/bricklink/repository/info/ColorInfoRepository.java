package com.mercu.bricklink.repository.info;

import com.mercu.bricklink.model.info.ColorInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ColorInfoRepository extends CrudRepository<ColorInfo, String> {

    // {/* 색상 : allColorPartImgUrls from bl_set_item where itemNo = partNo*/}
    @Query(value = "select colorId " +
            "from ( " +
            "select colorId, sum(qty) qty " +
            "from bl_set_item " +
            "where itemNo = :partNo " +
            "  and categoryType = 'P' " +
            "group by colorId " +
            " " +
            "union  " +
            " " +
            "select colorId, sum(qty) qty " +
            "from bl_my_item " +
            "where itemType = 'P' " +
            "  and itemNo = :partNo " +
            "group by colorId " +
            ") a " +
            "group by colorId " +
            "order by sum(qty) desc ", nativeQuery = true)
    List<String> findByPartNo(@Param("partNo") String partNo);
}
