package com.scuec.yygh.msm.service;

import com.scuec.yygh.vo.msm.MsmVo;

public interface MsmService {

    //发送手机验证码
    boolean send(String phone, String code);

    boolean sendEmail(String email, String code);

    //mq使用发送短信
    boolean send(MsmVo msmVo);
}
