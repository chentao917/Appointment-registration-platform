package com.scuec.yygh.task.scheduled;

import com.scuec.common.rabbit.constant.MqConst;
import com.scuec.common.rabbit.service.RabbitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@EnableScheduling
public class ScheduledTask {
    //每天8点执行方法,就医题型
    //cron表达式,设置执行间隔
    @Autowired
    private RabbitService rabbitService;
//   //0 0 8 * * ? 每天8点执行
    @Scheduled(cron = "0/30 * * * * ?")
    public void taskPatient(){
        rabbitService.sendMessage(MqConst.EXCHANGE_DIRECT_TASK,MqConst.ROUTING_TASK_8,"");

    }
}
