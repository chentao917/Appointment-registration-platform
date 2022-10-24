package com.scuec.yygh.msm.controller;

import com.scuec.yygh.common.result.Result;
import com.scuec.yygh.msm.service.MsmService;
import com.scuec.yygh.msm.utils.RandomUtil;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/msm")
//@CrossOrigin
public class MsmApiController {
    @Autowired
    private MsmService msmService;

    @Resource
    private RedisTemplate<String,String> redisTemplate;
    //发送手机验证码
    @GetMapping("send/{phone}")
    public Result sendCode(@PathVariable String phone){
        //从redis中获取验证码,如果获取的到,返回ok
        //key是手机号,value为验证码
        String code = redisTemplate.opsForValue().get(phone);
        if (!StringUtils.isEmpty(code)){
            return Result.ok();
        }

        //如果从redis获取不到
        // 生成验证码
        code = RandomUtil.getSixBitRandom();

        //调用service方法,,通过整合短信服务进行发送
        boolean isSend = msmService.send(phone,code);
        //生成验证码放到redis里面,设置有效的时间
        if (isSend){
            redisTemplate.opsForValue().set(phone,code,5, TimeUnit.MINUTES);
            return Result.ok();
        }else{
            return Result.fail().message("发送短信失败");
        }
    }


    //发送邮箱验证码
    @ApiOperation(value = "邮箱验证码")
    @GetMapping("sendEmail/{email}")
    public Result sendEmailCode(@PathVariable String email){
        //从redis中获取验证码,如果获取的到,返回ok
        //key是邮箱号,value为验证码
        String code = redisTemplate.opsForValue().get(email);
        if (!StringUtils.isEmpty(code)){
            return Result.ok();
        }

        //如果从redis获取不到
        // 生成验证码
        code = RandomUtil.getSixBitRandom();
        //调用service方法,,通过整合短信服务进行发送
        boolean isSend = msmService.sendEmail(email,code);
        //生成验证码放到redis里面,设置有效的时间
        if (isSend){
            redisTemplate.opsForValue().set(email,code,5, TimeUnit.MINUTES);
            return Result.ok();
        }else{
            return Result.fail().message("发送短信失败");
        }
    }
}
