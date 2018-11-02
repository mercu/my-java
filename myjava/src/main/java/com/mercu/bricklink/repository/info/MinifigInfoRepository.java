package com.mercu.bricklink.repository.info;

import com.mercu.bricklink.model.info.MinifigInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface MinifigInfoRepository extends CrudRepository<MinifigInfo, String> {

    @Query("select m from MinifigInfo m where m.minifigNo = :itemNo")
    Optional<MinifigInfo> findByMinifigNo(@Param("itemNo") String itemNo);
}
