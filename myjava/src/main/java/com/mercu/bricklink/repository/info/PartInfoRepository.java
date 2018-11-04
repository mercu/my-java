package com.mercu.bricklink.repository.info;

import com.mercu.bricklink.model.info.PartInfo;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PartInfoRepository extends CrudRepository<PartInfo, String> {

    @Query("select p from PartInfo p where p.categoryId = :categoryId order by p.setQty desc")
    List<PartInfo> findAllByCategoryId(@Param("categoryId") Integer categoryId);

    @Query("select p from PartInfo p where p.categoryId = :categoryId order by p.setQty desc")
    List<PartInfo> findAllByCategoryId(@Param("categoryId") Integer categoryId, Pageable pageable);

    @Query("select p from PartInfo p where p.partNo = :itemNo")
    Optional<PartInfo> findByPartNo(@Param("itemNo") String itemNo);

    @Query("select count(1) from PartInfo p where p.categoryId = :categoryId")
    Integer countPartsByCategoryId(@Param("categoryId") Integer categoryId);

}
