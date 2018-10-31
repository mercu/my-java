package com.mercu.bricklink.repository;

import com.mercu.bricklink.model.map.SetItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface SetItemRepository extends CrudRepository<SetItem, String> {

    @Query("select case when count(s) > 0 then true else false end from SetItem s where s.setId = :setId")
    boolean existsBySetId(@Param("setId") String setId);
}
