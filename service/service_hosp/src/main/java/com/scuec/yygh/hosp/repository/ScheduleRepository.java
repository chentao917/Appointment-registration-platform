package com.scuec.yygh.hosp.repository;

import com.scuec.yygh.model.hosp.Schedule;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Date;
import java.util.List;

public interface ScheduleRepository extends MongoRepository<Schedule,String> {
    //根据 医院编号和排班编号查询
    Schedule getScheduleByHoscodeAndHosScheduleId(String hoscode, String hosScheduleId);

    //根据医院编号.科室编号.查询排班详情信息
    List<Schedule> findScheduleByHoscodeAndDepcodeAndWorkDate(String hoscode, String depcode, Date toDate);

    //获取排班信息
    Schedule getScheduleById(String scheduleId);
}
