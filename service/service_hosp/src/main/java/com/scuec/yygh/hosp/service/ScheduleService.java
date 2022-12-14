package com.scuec.yygh.hosp.service;

import com.scuec.yygh.model.hosp.Schedule;
import com.scuec.yygh.vo.hosp.ScheduleOrderVo;
import com.scuec.yygh.vo.hosp.ScheduleQueryVo;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface ScheduleService {
    void save(Map<String, Object> paramMap);

    Page<Schedule> findPageSchedule(Integer page, Integer limit, ScheduleQueryVo scheduleQueryVo);

    //删除医院排班
    void remove(String hoscode, String hosScheduleId);

    //根据医院编号 和 科室 编号,查询排班的规则数据
    Map<String, Object> getRuleSchedule(long page, long limit, String hoscode, String depcode);

    //根据医院编号.科室编号.查询排班详情信息
    List<Schedule> getDetailSchedule( String hoscode,String depcode, String workDate);

    //获取可以预约的排班数据
    Map<String,Object> getBookingScheduleRule(int page, int limit, String hoscode, String depcode);
    //获取排班id获取排版数据
    Schedule getScheduleId(String scheduleId);

    //根据排班id获取预约下单数据
    ScheduleOrderVo getScheduleOrderVo(String scheduleId);


    //更新排班数据 用于mq
    void update(Schedule schedule);
}
