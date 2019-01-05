package com.mercu.bricklink.repository.similar;

import com.mercu.bricklink.model.similar.SimilarColor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SimilarColorRepository extends CrudRepository<SimilarColor, String> {

    @Query("select s from SimilarColor s where s.colorId = :colorId")
    SimilarColor findByColorId(@Param("colorId") String colorId);

    @Query(value = "select si2.colorId " +
           "from br_similar_color si " +
           "join br_similar_color si2 on si2.similarId = si.similarId " +
           "where si.colorId = :colorId " +
           "  and si2.colorId <> :colorId",
            nativeQuery = true)
    List<String> findColorIds(@Param("colorId") String colorId);

    @Query(value = "select si2.colorId " +
            "from br_similar_color si1 " +
            "join br_similar_color si2 on si2.id = si1.similarId " +
            "where si1.colorId = :colorId",
            nativeQuery = true)
    String findRepresentColorId(@Param("colorId") String colorId);
}
