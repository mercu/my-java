package com.mercu.lego.repository;

import com.mercu.lego.model.MyPartCategory;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface MatchMyItemRepository extends CrudRepository<MyPartCategory, Integer> {
    @Query("select distinct mmsr.matchId\n" +
            "from MatchMyItemSetItemRatio mmsr\n" +
            "order by mmsr.matchId desc")
    List<String> findMatchIds();

}
