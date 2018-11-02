package com.mercu.bricklink.repository.match;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mercu.bricklink.model.match.MatchMyItemSetItem;

public interface MatchMyItemSetItemRepository extends CrudRepository<MatchMyItemSetItem, String> {

    @Query("select case when count(f) > 0 then true else false end from MatchMyItemSetItem f where f.matchId = :matchId and f.itemNo = :itemNo and f.colorId = :colorId")
    boolean existsMapItem(@Param("matchId") String matchId, @Param("itemNo") String itemNo, @Param("colorId") String colorId);

    @Query("select distinct f.setId, f.setNo from MatchMyItemSetItem f where f.matchId = :matchId")
    List<Object[]> distinctSetIdNoAll(@Param("matchId") String matchId);

    @Query("select count(1) from MatchMyItemSetItem f where f.setId = :setId and f.matchId = :matchId")
    int countBySetId(@Param("setId") String setId, @Param("matchId") String matchId);
}
