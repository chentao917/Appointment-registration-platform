package com.scuec.yygh.user.service;

import com.scuec.yygh.model.user.UserInfo;
import com.scuec.yygh.vo.user.LoginVo;
import com.scuec.yygh.vo.user.UserAuthVo;
import com.scuec.yygh.vo.user.UserInfoQueryVo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;

import java.util.Map;

public interface UserInfoService extends IService<UserInfo> {
    //用户手机号登录接口
    Map<String, Object> loginUser(LoginVo loginVo);

    //用户邮箱登录接口
    Map<String, Object> loginEmail(LoginVo loginVo);

    //根据openid查询
    UserInfo selectWxInfoOpenId(String openid);
    //用户认证接口
    void userAuth(Long userId, UserAuthVo userAuthVo);

    //用户列表(条件查询带分页功能)
    IPage<UserInfo> selectPage(Page<UserInfo> pageParam, UserInfoQueryVo userInfoQueryVo);

    //用户锁定
    void lock(Long userId, Integer status);

    //用户详情
    Map<String, Object> show(Long userId);

    //认证审批
    void approval(Long userId, Integer authStatus);
}
