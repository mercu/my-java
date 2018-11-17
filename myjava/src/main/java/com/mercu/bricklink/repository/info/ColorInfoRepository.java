package com.mercu.bricklink.repository.info;

import com.mercu.bricklink.model.info.ColorInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ColorInfoRepository extends CrudRepository<ColorInfo, String> {

    // {/* 색상 : allColorPartImgUrls from bl_set_item where itemNo = partNo*/}
    @Query("select si.colorId " +
            "from SetItem si " +
            "where si.itemNo = :partNo " +
            "  and si.categoryType = 'P' " +
            "group by si.colorId " +
            "order by sum(si.qty) desc")
    List<String> findByPartNo(@Param("partNo") String partNo);
}
