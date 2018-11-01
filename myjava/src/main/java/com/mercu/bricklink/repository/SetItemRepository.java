package com.mercu.bricklink.repository;

import com.mercu.bricklink.model.map.SetItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SetItemRepository extends CrudRepository<SetItem, String> {

    @Query("select case when count(s) > 0 then true else false end from SetItem s where s.setId = :setId")
    boolean existsBySetId(@Param("setId") String setId);

    @Query("select s from SetItem s where s.setNo = :setNo")
    List<SetItem> findBySetNo(@Param("setNo") String setNo);

    @Query("select s from SetItem s where s.itemNo = :itemNo and s.colorId = :colorId")
    List<SetItem> findByItemAndColor(@Param("itemNo") String itemNo, @Param("colorId") String colorId);
}
