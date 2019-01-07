package com.mercu.bricklink.repository.map;

import com.mercu.bricklink.model.map.SetItem;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface SetItemRepository extends CrudRepository<SetItem, String> {

    @Query("select case when count(s) > 0 then true else false end from SetItem s where s.setId = :setId")
    boolean existsBySetId(@Param("setId") String setId);

    @Query("select s from SetItem s where s.setId = :setId")
    List<SetItem> findBySetId(@Param("setId") String setId);

    @Query("select s from SetItem s where s.setNo = :setNo")
    List<SetItem> findBySetNo(@Param("setNo") String setNo);

    @Query("select s from SetItem s where s.itemNo = :itemNo")
    List<SetItem> findByItemNo(@Param("itemNo") String itemNo);

    @Query("select s from SetItem s where s.itemNo = :itemNo and s.colorId = :colorId")
    List<SetItem> findByItemAndColor(@Param("itemNo") String itemNo, @Param("colorId") String colorId);

    @Query("select s from SetItem s join SetInfo si on si.id = s.setId and si.year = :year where s.itemNo = :itemNo and s.colorId = :colorId")
    List<SetItem> findByItemAndColorByYear(@Param("itemNo") String itemNo, @Param("colorId") String colorId, @Param("year") Integer year);

    @Query("select s from SetItem s where s.setId = :setId and s.itemNo = :itemNo and s.colorId = :colorId")
    SetItem findSetPart(@Param("setId") String setId, @Param("itemNo") String itemNo, @Param("colorId") String colorId);

    @Query("select count(1) from SetItem s where s.setId = :setId and s.categoryType = :categoryType")
    int countItemsBySetId(@Param("setId") String setId, @Param("categoryType") String categoryType);

    @Transactional
    @Modifying
    @Query("delete from SetItem s where s.setNo = :setNo")
    void deleteAllBySetNo(@Param("setNo") String setNo);
}
