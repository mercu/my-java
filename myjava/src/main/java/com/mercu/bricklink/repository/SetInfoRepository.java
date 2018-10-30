package com.mercu.bricklink.repository;

import com.mercu.bricklink.model.info.SetInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SetInfoRepository extends CrudRepository<SetInfo, String> {

    @Query("select s from SetInfo s where s.setBrief like concat('%',:year)")
    List<SetInfo> findAllByYear(@Param("year") String year);
}
