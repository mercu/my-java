package com.mercu.lego.repository;

import com.mercu.lego.model.match.MatchMyItemSetItemRatio;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface MatchMyItemSetItemRatioRepository extends CrudRepository<MatchMyItemSetItemRatio, String> {
    @Query("select distinct mmsr.matchId\n" +
            "from MatchMyItemSetItemRatio mmsr\n" +
            "order by mmsr.matchId desc")
    List<String> findMatchIds();

    @Query("select mmsr\n" +
            "from MatchMyItemSetItemRatio mmsr\n" +
            "where mmsr.matchId = :matchId\n" +
            "  and mmsr.matched > 20\n" +
            "order by mmsr.ratio desc, mmsr.matched desc")
    List<MatchMyItemSetItemRatio> findMatchSetList(@Param("matchId") String matchId, Pageable pageable);

}
