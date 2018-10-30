package com.mercu.bricklink.repository;

import com.mercu.bricklink.model.info.PartInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface PartInfoRepository extends CrudRepository<PartInfo, String> {

    @Query("select p from PartInfo p where p.partNo = :itemNo")
    Optional<PartInfo> findByPartNo(@Param("itemNo") String itemNo);
}
