package com.mercu.bricklink.repository.info;

import com.mercu.bricklink.model.info.SetInfo;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Map;

public interface SetInfoRepository extends CrudRepository<SetInfo, String> {

    @Query("select s from SetInfo s where s.setBrief like concat('%',:year)")
    List<SetInfo> findAllByYear(@Param("year") String year);

    @Query("select s from SetInfo s where s.setNo = :setNo")
    SetInfo findBySetNo(@Param("setNo") String setNo);

    @Query("select s from SetInfo s where s.blSetNo = :blSetNo")
    SetInfo findByBlSetNo(@Param("blSetNo") String blSetNo);

}
