package com.mercu.bricklink.repository.similar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mercu.bricklink.model.similar.SimilarPart;

public interface SimilarPartRepository extends CrudRepository<SimilarPart, String> {

    @Query("select s from SimilarPart s where s.partNo = :partNo")
    SimilarPart findByPartNo(@Param("partNo") String partNo);
}
