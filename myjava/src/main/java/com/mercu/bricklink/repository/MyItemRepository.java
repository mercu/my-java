package com.mercu.bricklink.repository;

import com.mercu.bricklink.model.my.MyItem;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

public interface MyItemRepository extends CrudRepository<MyItem, String> {

    @Query("select m from MyItem m where m.itemType = :itemType and m.itemNo = :itemNo and m.colorId = :colorId and m.whereCode = :whereCode")
    MyItem findById(@Param("itemType") String itemType, @Param("itemNo") String itemNo, @Param("colorId") String colorId, @Param("whereCode") String whereCode);
}
