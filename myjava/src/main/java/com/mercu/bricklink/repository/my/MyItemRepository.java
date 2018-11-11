package com.mercu.bricklink.repository.my;

import com.mercu.bricklink.model.my.MyItem;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import javax.transaction.Transactional;
import java.util.List;

public interface MyItemRepository extends CrudRepository<MyItem, String> {

    @Query("select m from MyItem m join PartInfo p on p.partNo = m.itemNo")
    List<MyItem> findList(Pageable pageable);

    @Query("select m from MyItem m where m.itemType = :itemType and m.itemNo = :itemNo and m.colorId = :colorId and m.whereCode = :whereCode")
    MyItem findById(@Param("itemType") String itemType, @Param("itemNo") String itemNo, @Param("colorId") String colorId, @Param("whereCode") String whereCode);

    @Query("select m from MyItem m where m.whereCode = :whereCode and m.whereMore = :whereMore")
    List<MyItem> findByWhere(@Param("whereCode") String whereCode, @Param("whereMore") String whereMore);

    @Transactional
    @Modifying
    @Query("delete from MyItem m where m.whereCode = :whereCode and m.whereMore = :whereMore")
    void deleteByWhere(@Param("whereCode") String whereCode, @Param("whereMore") String whereMore);

}
