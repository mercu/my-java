package com.mercu.bricklink.repository.similar;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import com.mercu.bricklink.model.similar.SimilarPart;

import java.util.List;

public interface SimilarPartRepository extends CrudRepository<SimilarPart, String> {

    @Query("select s from SimilarPart s where s.partNo = :partNo")
    SimilarPart findByPartNo(@Param("partNo") String partNo);

    @Query(value = "select si2.partNo " +
           "from bl_similar_part si " +
           "join bl_similar_part si2 on si2.similarId = si.similarId " +
           "where si.partNo = :partNo " +
           "  and si2.partNo <> :partNo",
            nativeQuery = true)
    List<String> findPartNos(@Param("partNo") String partNo);

    @Query(value = "select si2.partNo " +
            "from bl_similar_part si1 " +
            "join bl_similar_part si2 on si2.id = si1.similarId " +
            "where si1.partNo = :partNo",
            nativeQuery = true)
    String findRepresentPartNo(@Param("partNo") String partNo);
}
