package com.scuec.yygh.msm.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.scuec.yygh.msm.service.MsmService;
import com.scuec.yygh.msm.utils.ConstantPropertiesUtils;
import com.scuec.yygh.msm.utils.SmsUtil;
import com.scuec.yygh.vo.msm.MsmVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class MsmServiceImpl implements MsmService {

    @Autowired
    private JavaMailSender javaMailSender;
    //发送手机验证码
    @Override
    public boolean send(String phone, String code) {

        //判断手机号是否为空
        if (StringUtils.isEmpty(phone)){
            return false;
        }

        //整合短信服务
//        String appCode ="f3ad7b931e464ea5bf31aabd8937c868";
//        String smsSignId = "2e65b1bb3d054466b82f0c9d125465e2";
//        String templateId ="908e94ccf08b4476ba6c876d13f084ad";
        String appCode = ConstantPropertiesUtils.APPCODE;
        String smsSignId = ConstantPropertiesUtils.SMSSIGN_ID;
        String templateId = ConstantPropertiesUtils.TEMPLATE_ID;
        SmsUtil.sendMessage(appCode,phone,code,"5",smsSignId,templateId);
        //设置相关参数
        return true;
    }

    @Override
    public boolean sendEmail(String email, String code) {
        if (StringUtils.isEmpty(email)){
            return false;
        }

        SimpleMailMessage simpleMailMessage = new SimpleMailMessage();
        simpleMailMessage.setSubject("挂号通项目登录验证码");
        simpleMailMessage.setText("尊敬的:"+email+"您的注册验证码为:"+code+"有效期5分钟");
        simpleMailMessage.setTo(email);
        simpleMailMessage.setFrom("chentao917@foxmail.com");


        try {
            javaMailSender.send(simpleMailMessage);
            System.out.println("message=====>" + simpleMailMessage);
        } catch (MailException ex) {
            ex.getMessage();
        }
        return true;
    }

    //mq发送短信封装
    @Override
    public boolean send(MsmVo msmVo) {
        if (!StringUtils.isEmpty(msmVo.getPhone())){
            String msg = JSONObject.toJSONString(msmVo.getParam());
            boolean isSend = this.sendEmail(msmVo.getPhone(), msg);
            return isSend;
        }
        return false;
    }



}
