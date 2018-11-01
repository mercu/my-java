package com.mercu.bricklink.repository;

import com.mercu.bricklink.model.map.FindItemSet;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface FindItemSetRepository extends CrudRepository<FindItemSet, String> {

    @Query("select case when count(f) > 0 then true else false end from FindItemSet f where f.mapId = :mapId and f.itemNo = :itemNo")
    boolean existsMapItem(@Param("mapId") String mapId, @Param("itemNo") String itemNo);
}
