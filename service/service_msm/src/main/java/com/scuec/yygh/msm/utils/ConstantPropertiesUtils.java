package com.scuec.yygh.msm.utils;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantPropertiesUtils implements InitializingBean {

    @Value("${appCode}")
    private String appCode;

    @Value("${smsSignId}")
    private String smsSignId;

    @Value("${templateId}")
    private String templateId;


    public static String APPCODE;
    public static String SMSSIGN_ID;
    public static String TEMPLATE_ID;

    @Override
    public void afterPropertiesSet() throws Exception {
        APPCODE=appCode;
        SMSSIGN_ID=smsSignId;
        TEMPLATE_ID=templateId;

    }
}
