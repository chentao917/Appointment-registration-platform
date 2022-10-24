package com.scuec.yygh.hosp.service;

import com.scuec.yygh.model.hosp.HospitalSet;
import com.scuec.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.extension.service.IService;


public interface HospitalSetService extends IService<HospitalSet> {
    String getSignKey(String hoscode);

    //获取医院签名信息
    SignInfoVo getSignInfoVo(String hoscode);
}
