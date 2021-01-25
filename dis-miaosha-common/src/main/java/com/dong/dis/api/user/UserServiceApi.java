package com.dong.dis.api.user;

import com.dong.dis.api.user.vo.LoginVo;
import com.dong.dis.api.user.vo.RegisterVo;
import com.dong.dis.api.user.vo.UserInfoVo;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.result.CodeMsg;

import javax.validation.Valid;

/**
 * 用于用户交互api
 * @author dong
 */
public interface UserServiceApi {
    String COOKIE_NAME_TOKEN = "token";
    int login(String username ,String password);

    /**
     * 登录
     * @param loginVo
     * @return
     */
    String login (@Valid LoginVo loginVo);

    /**
     * 根据phone获取用户
     * @param phone
     * @return
     */
    UserVo getUserByPhone(long phone);

    /**
     * 更新用户信息
     * @param userInfoVo
     * @return
     */
    UserInfoVo updateUserInfo(UserInfoVo userInfoVo);

    /**
     * 获取用户信息
     * @param uuid
     * @return
     */
    UserInfoVo getUserInfo(int uuid);

    /**
     * 注册
     * @param userModel
     * @return
     */
    CodeMsg register(RegisterVo userModel);
}
