package com.scuec.yygh.user.service.impl;

import com.scuec.yygh.common.exception.YyghException;
import com.scuec.yygh.common.helper.JwtHelper;
import com.scuec.yygh.common.result.ResultCodeEnum;
import com.scuec.yygh.enums.AuthStatusEnum;
import com.scuec.yygh.model.user.Patient;
import com.scuec.yygh.model.user.UserInfo;
import com.scuec.yygh.user.mapper.UserInfoMapper;
import com.scuec.yygh.user.service.PatientService;
import com.scuec.yygh.user.service.UserInfoService;
import com.scuec.yygh.vo.user.LoginVo;
import com.scuec.yygh.vo.user.UserAuthVo;
import com.scuec.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class UserInfoServiceImpl extends
        ServiceImpl<UserInfoMapper, UserInfo> implements UserInfoService {

    @Autowired
    private RedisTemplate<String, String> redisTemplate;
    @Autowired
    private PatientService patientService;

    //用户手机号登录接口
    @Override
    public Map<String, Object> loginUser(LoginVo loginVo) {
        //从loginVo获取输入的手机号和验证码
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();

        //判断这些值是否为空
        if (StringUtils.isEmpty(phone) || StringUtils.isEmpty(code)) {
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //TODO 判断手机验证码和输入验证码是否一致

        //判断是否第一次登录:根据手机号查询数据库,如果不存在相同手机号就是第一次登录
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
        wrapper.eq("phone", phone);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        if (userInfo == null) {
            //第一次使用这个手机号登录
            //添加信息到数据库
            userInfo = new UserInfo();
            userInfo.setName("");
            userInfo.setPhone(phone);
            userInfo.setStatus(1);
            baseMapper.insert(userInfo);
        }

        //校验是否被禁用
        if (userInfo.getStatus() == 0) {
            throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
        }


        //返回登录信息

        //返回登录用户名

        //TODO token生成 返回token信息
        Map<String, Object> map = new HashMap<>();
        String name = userInfo.getName();
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getNickName();
        }
        if (StringUtils.isEmpty(name)) {
            name = userInfo.getPhone();
        }
        map.put("name", name);

        //TODO jwt的 token生成
        String token = JwtHelper.createToken(userInfo.getId(), name);
        map.put("token", token);

        return map;
    }

    //邮箱登录
    @Override
    public Map<String, Object> loginEmail(LoginVo loginVo) {
        String phone = loginVo.getPhone();
        String code = loginVo.getCode();
        code=  "\""+code+ "\"";

//        System.out.println(code);
//        System.out.println(phone);
        //校验参数
        if (StringUtils.isEmpty(phone)||StringUtils.isEmpty(code)){
            throw new YyghException(ResultCodeEnum.PARAM_ERROR);
        }

        //校验校验验证码
        String mobleCode = redisTemplate.opsForValue().get(phone);
//        System.out.println(mobleCode);
        if(!code.equals(mobleCode)) {
            throw new YyghException(ResultCodeEnum.CODE_ERROR);
        }else {

            QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();
            wrapper.eq("phone", loginVo.getPhone());
            UserInfo userInfo = baseMapper.selectOne(wrapper);
            if (userInfo == null) {    //第一次使用这个手机号登录
                //添加数据到数据库
                userInfo = new UserInfo();
                userInfo.setName("");
                userInfo.setPhone(loginVo.getPhone());
                userInfo.setStatus(1);
                baseMapper.insert(userInfo);
            }

            //校验是否被禁用
            if (userInfo.getStatus() == 0) {
                throw new YyghException(ResultCodeEnum.LOGIN_DISABLED_ERROR);
            }

            //不是第一次直接登录
            HashMap<String, Object> map = new HashMap<>();
            //返回登录用户名
            String name = userInfo.getName();
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getNickName();
            }
            if (StringUtils.isEmpty(name)) {
                name = userInfo.getPhone();
            }
            map.put("name", name);

            //返回token信息
            String token = JwtHelper.createToken(userInfo.getId(), name);
            map.put("token", token);
            redisTemplate.delete(phone);
            //返回登录信息
            return map;
        }
    }

    @Override
    public UserInfo selectWxInfoOpenId(String openid) {
        QueryWrapper<UserInfo> wrapper = new QueryWrapper();
        wrapper.eq("openid", openid);
        UserInfo userInfo = baseMapper.selectOne(wrapper);
        return userInfo;
    }

    //用户认证接口
    @Override
    public void userAuth(Long userId, UserAuthVo userAuthVo) {
        //根据用户id查询用户信息
        UserInfo userInfo = baseMapper.selectById(userId);
        //设置认证信息
        userInfo.setName(userAuthVo.getName());
        userInfo.setCertificatesType(userAuthVo.getCertificatesType());
        userInfo.setCertificatesNo(userAuthVo.getCertificatesNo());
        userInfo.setAuthStatus(AuthStatusEnum.AUTH_RUN.getStatus());
        //进行信息更新
        baseMapper.updateById(userInfo);
    }

    //用户列表(条件查询带分页功能)
    @Override
    public IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo) {
        //UserQueryVo获取条件值
        String name = userInfoQueryVo.getKeyword();//用户名称
        Integer status = userInfoQueryVo.getStatus();//用户状态
        Integer authStatus = userInfoQueryVo.getAuthStatus();//认证状态
        String createTimeBegin = userInfoQueryVo.getCreateTimeBegin();//开始时间
        String createTimeEnd = userInfoQueryVo.getCreateTimeEnd();//结束时间

        //对条件值进行飞空判断
        QueryWrapper<UserInfo> wrapper = new QueryWrapper<>();

        if(!StringUtils.isEmpty(name)) {
            wrapper.like("name",name);
        }
        if(!StringUtils.isEmpty(status)) {
            wrapper.eq("status",status);
        }
        if(!StringUtils.isEmpty(authStatus)) {
            wrapper.eq("auth_status",authStatus);
        }
        if(!StringUtils.isEmpty(createTimeBegin)) {
            wrapper.ge("create_time",createTimeBegin);
        }
        if(!StringUtils.isEmpty(createTimeEnd)) {
            wrapper.le("create_time",createTimeEnd);
        }

        //调用mapper方法
        IPage<UserInfo> pages = baseMapper.selectPage(pageParam, wrapper);

        //编号变成对应的值的封装
        pages.getRecords().stream().forEach(item->{
            this.packageUserInfo(item);
        });

        return pages;

    }

    //用户锁定
    @Override
    public void lock(Long userId, Integer status) {
        if (status.intValue()==0||status.intValue()==1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setStatus(status);
            baseMapper.updateById(userInfo);
        }
    }

    //用户详情
    @Override
    public Map<String, Object> show(Long userId) {
        Map<String,Object> map = new HashMap<>();
        //根据userid查询用户信息
        UserInfo userInfo = this.packageUserInfo(baseMapper.selectById(userId));
        map.put("userInfo",userInfo);
        //根据userid查询出就诊人信息,就诊人信息需要在Patient中填写
        List<Patient> patientList = patientService.findAllUserId(userId);

        map.put("patientList",patientList);

        return map;
    }

    //认证审批
    @Override
    public void approval(Long userId, Integer authStatus) {
        if (authStatus.intValue()==2||authStatus.intValue()==-1){
            UserInfo userInfo = baseMapper.selectById(userId);
            userInfo.setAuthStatus(authStatus);
            baseMapper.updateById(userInfo);
        }
    }

    private UserInfo packageUserInfo(UserInfo userInfo) {
        //处理认证状态编码
        userInfo.getParam().put("authStatusString",AuthStatusEnum.getStatusNameByStatus(userInfo.getAuthStatus()));

        //处理用户状态0 1
        String statusString = userInfo.getStatus().intValue()== 0?"锁定" : "正常";
        userInfo.getParam().put("statusString",statusString);

        return userInfo;
    }
}
