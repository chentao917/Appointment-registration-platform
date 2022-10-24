package com.scuec.yygh.order.service.impl;

import com.alibaba.fastjson.JSONObject;
import com.scuec.yygh.common.exception.YyghException;
import com.scuec.yygh.common.helper.HttpRequestHelper;
import com.scuec.yygh.common.result.ResultCodeEnum;
import com.scuec.yygh.enums.OrderStatusEnum;
import com.scuec.yygh.enums.PaymentStatusEnum;
import com.scuec.yygh.enums.PaymentTypeEnum;
import com.scuec.yygh.hosp.client.HospitalFeignClient;
import com.scuec.yygh.model.order.OrderInfo;
import com.scuec.yygh.model.order.PaymentInfo;
import com.scuec.yygh.order.mapper.PaymentMapper;
import com.scuec.yygh.order.service.OrderService;
import com.scuec.yygh.order.service.PaymentService;
import com.scuec.yygh.vo.order.SignInfoVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Service
public class PaymentServiceImpl extends
        ServiceImpl<PaymentMapper, PaymentInfo> implements PaymentService {

    @Autowired
    private OrderService orderService;
    @Autowired
    private HospitalFeignClient hospitalFeignClient;
    //向支付记录表中添加信息
    @Override
    public void savePaymentInfo(OrderInfo order, Integer paymentType) {
        //根据订单id 和 支付类型,查询支付记录表是否存在相同订单
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("order_id",order.getId());
        wrapper.eq("payment_type",paymentType);
        Integer count = baseMapper.selectCount(wrapper);

        if (count>0){
            return;
        }
        //添加记录
        PaymentInfo paymentInfo = new PaymentInfo();
        paymentInfo.setCreateTime(new Date());
        paymentInfo.setOrderId(order.getId());
        paymentInfo.setPaymentType(paymentType);
        paymentInfo.setOutTradeNo(order.getOutTradeNo());
        paymentInfo.setPaymentStatus(PaymentStatusEnum.UNPAID.getStatus());
        String subject = new DateTime(order.getReserveDate()).toString("yyyy-MM-dd")+"|"+order.getHosname()+"|"+order.getDepname()+"|"+order.getTitle();
        paymentInfo.setSubject(subject);
        paymentInfo.setTotalAmount(order.getAmount());
        baseMapper.insert(paymentInfo);

    }

//    //更新订单状态
//    @Override
//    public void paySuccess(String out_trade_no,Map<String, String> resultMap) {
//        //1 根据订单编号得到支付记录
//        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
//        wrapper.eq("out_trade_no",out_trade_no);
//        wrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
//        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
//
//        //2 更新支付记录信息
//        paymentInfo.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
//        paymentInfo.setTradeNo(resultMap.get("transaction_id"));
//        paymentInfo.setCallbackTime(new Date());
//        paymentInfo.setCallbackContent(resultMap.toString());
//        baseMapper.updateById(paymentInfo);
//
//        //3 根据订单号得到订单信息
//        //4 更新订单信息
//        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
//        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
//        orderService.updateById(orderInfo);
//
//        //5 调用医院接口,更新订单支付信息
//        SignInfoVo signInfoVo = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
//        Map<String,Object> reqMap = new HashMap<>();
//        reqMap.put("hoscode",orderInfo.getHoscode());
//        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
//        reqMap.put("timeStamp", HttpRequestHelper.getTimestamp());
//        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
//        reqMap.put("sign",sign);
//        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl() + "/order/updatePayStatus");
//
//    }


    /**
     * 支付成功
     */
    @Override
    public void paySuccess(String out_trade_no,Map<String,String> paramMap) {
        //1 根据订单编号得到支付记录
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("out_trade_no",out_trade_no);
        wrapper.eq("payment_type", PaymentTypeEnum.WEIXIN.getStatus());
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        if (null == paymentInfo) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }
        if (paymentInfo.getPaymentStatus() != PaymentStatusEnum.UNPAID.getStatus()) {
            return;
        }
//        修改支付状态
        PaymentInfo paymentInfoUpd = new PaymentInfo();
        paymentInfoUpd.setPaymentStatus(PaymentStatusEnum.PAID.getStatus());
        paymentInfoUpd.setTradeNo(paramMap.get("transaction_id"));
        paymentInfoUpd.setCallbackTime(new Date());
        paymentInfoUpd.setCallbackContent(paramMap.toString());
        baseMapper.updateById(paymentInfoUpd);

        //修改订单状态
        OrderInfo orderInfo = orderService.getById(paymentInfo.getOrderId());
        orderInfo.setOrderStatus(OrderStatusEnum.PAID.getStatus());
        orderService.updateById(orderInfo);
        // 调用医院接口，通知更新支付状态
        SignInfoVo signInfoVo
                = hospitalFeignClient.getSignInfoVo(orderInfo.getHoscode());
//        if(null == signInfoVo) {
//            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
//        }
        Map<String, Object> reqMap = new HashMap<>();
        reqMap.put("hoscode",orderInfo.getHoscode());
        reqMap.put("hosRecordId",orderInfo.getHosRecordId());
        reqMap.put("timestamp", HttpRequestHelper.getTimestamp());
        String sign = HttpRequestHelper.getSign(reqMap, signInfoVo.getSignKey());
        reqMap.put("sign", sign);
        JSONObject result = HttpRequestHelper.sendRequest(reqMap, signInfoVo.getApiUrl()+"/order/updatePayStatus");
//        if(result.getInteger("code") != 200) {
//            throw new YyghException(result.getString("message"), ResultCodeEnum.FAIL.getCode());
//        }
    }

    /**
     * 获取支付记录
     * @param orderId
     * @param paymentType
     * @return
     */
    @Override
    public PaymentInfo getPaymentInfo(Long orderId, Integer paymentType) {
        QueryWrapper<PaymentInfo> wrapper = new QueryWrapper();
        wrapper.eq("order_id",orderId);
        wrapper.eq("payment_type",paymentType);
        PaymentInfo paymentInfo = baseMapper.selectOne(wrapper);
        return paymentInfo;
    }

}
