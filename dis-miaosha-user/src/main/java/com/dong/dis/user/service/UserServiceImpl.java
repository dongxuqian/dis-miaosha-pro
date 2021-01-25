package com.dong.dis.user.service;

import com.dong.dis.api.cache.DLockApi;
import com.dong.dis.api.cache.RedisServiceApi;
import com.dong.dis.api.cache.vo.SkUserKeyPrefix;
import com.dong.dis.api.user.UserServiceApi;
import com.dong.dis.api.user.vo.LoginVo;
import com.dong.dis.api.user.vo.RegisterVo;
import com.dong.dis.api.user.vo.UserInfoVo;
import com.dong.dis.api.user.vo.UserVo;
import com.dong.dis.exception.GlobalException;
import com.dong.dis.result.CodeMsg;
import com.dong.dis.user.domain.SeckillUser;
import com.dong.dis.user.persistence.SeckillUserMapper;
import com.dong.dis.util.MD5Util;
import com.dong.dis.util.UUIDUtil;
import org.apache.dubbo.config.annotation.Reference;
import org.apache.dubbo.config.annotation.Service;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.Valid;
import java.util.Date;

@Service(interfaceClass = UserServiceApi.class)
public class UserServiceImpl implements  UserServiceApi {
    private static Logger logger = LoggerFactory.getLogger(UserServiceApi.class);
    /**
     * 操作user表
     */
    @Autowired
    SeckillUserMapper userMapper;

    @Reference(interfaceClass = RedisServiceApi.class)
    /**
     * 调用redis
     */
    private RedisServiceApi redisService;
    @Reference(interfaceClass = DLockApi.class)
    /**
     * 分布式锁
     */
    private DLockApi dLock;
    @Override
    public int login(String username, String password) {
        return 0;
    }

    /**
     * 用户登录, 要么处理成功返回true，否则会抛出全局异常
     * 抛出的异常信息会被全局异常接收，全局异常会将异常信息传递到全局异常处理器
     *
     * @param loginVo 封装了客户端请求传递过来的数据（即账号密码）
     *                （使用post方式，请求参数放在了请求体中，这个参数就是获取请求体中的数据）
     * @return 用户token
     */
    @Override
    public String login(@Valid LoginVo loginVo) {
        logger.info(loginVo.toString());
        String mobile = loginVo.getMobile();
        String password = loginVo.getPassword();
        //判断手机号是否存在（先缓存，在数据库）
        SeckillUser user = this.getSeckillUserByPhone(Long.parseLong(mobile));
        if(user==null){
            throw new GlobalException(CodeMsg.MOBILE_NOT_EXIST);
        }
        logger.info("用户"+user.toString());
        String prepassword = user.getPassword();
        String salt = user.getSalt();
        String pass = MD5Util.formPassToDbPass(password,salt);
        if(!pass.equals(prepassword)){
            throw new GlobalException(CodeMsg.PASSWORD_ERROR);
        }
        //登陆成功，更新cookie
        String token = UUIDUtil.uuid();
        // 每次访问都会生成一个新的session存储于redis和反馈给客户端，一个session对应存储一个user对象
        redisService.set(SkUserKeyPrefix.TOKEN,token,user);
        return token;
    }

    /**
     * 根据phone查询秒杀用户信息
     * 对象级缓存
     * 先查redis在查数据库
     * @param phone
     * @return
     */
    public SeckillUser getSeckillUserByPhone(long phone){
        //redis
        SeckillUser user = redisService.get(SkUserKeyPrefix.SK_USER_PHONE,"_"+phone,SeckillUser.class);
        if(user!=null){
            return user;
        }
        //查数据库，并写入缓存
        user = userMapper.getUserByPhone(phone);
        if(user!=null){
            redisService.set(SkUserKeyPrefix.SK_USER_PHONE,"_"+phone,user);
        }
        return user;
    }
    @Override
    public UserVo getUserByPhone(long phone) {
        UserVo userVo = new UserVo();
        SeckillUser user = userMapper.getUserByPhone(phone);
        userVo.setUuid(user.getUuid());
        userVo.setSalt(user.getSalt());
        userVo.setRegisterDate(user.getRegisterDate());
        userVo.setPhone(user.getPhone());
        userVo.setPassword(user.getPassword());
        userVo.setNickName(user.getNickname());
        userVo.setLoginCount(user.getLoginCount());
        userVo.setLastLoginDate(user.getLastLoginDate());
        userVo.setHead(user.getHead());
        return userVo;
    }

    @Override
    public UserInfoVo updateUserInfo(UserInfoVo userInfoVo) {
        return null;
    }

    @Override
    public UserInfoVo getUserInfo(int uuid) {
        return null;
    }

    @Override
    public CodeMsg register(RegisterVo userModel) {
        //加锁
        String uniqueValue = UUIDUtil.uuid()+"-"+Thread.currentThread().getId();
        String lockKey = "redis-lock"+userModel.getPhone();
        boolean lock = dLock.lock(lockKey,uniqueValue,60*1000);
        if(!lock){
            return CodeMsg.WAIT_REGISTER_DONE;
        }
        logger.debug("注册接口加锁成功");
        //检查用户是否注册
        SeckillUser user = this.getSeckillUserByPhone(userModel.getPhone());
        if(user!=null){
            dLock.unlock(lockKey,uniqueValue);
            return CodeMsg.USER_EXIST;
        }
        //生成SkUser对象
        SeckillUser newUser = new SeckillUser();
        newUser.setPhone(userModel.getPhone());
        newUser.setNickname(userModel.getNickname());
        newUser.setHead(userModel.getHead());
        newUser.setSalt(MD5Util.SALT);
        String dbPass = MD5Util.formPassToDbPass(userModel.getPassword(), MD5Util.SALT);
        newUser.setPassword(dbPass);
        Date date = new Date(System.currentTimeMillis());
        newUser.setRegisterDate(date);
        //写入数据库
        logger.info(newUser.toString());
        long id = userMapper.insertUser(newUser);
        boolean  unlock = dLock.unlock(lockKey,uniqueValue);
        if(!unlock){
            return CodeMsg.REGISTER_FAIL;
        }
        logger.debug("注册接口解锁成功");
        if(id>0){
            return CodeMsg.SUCCESS;
        }
        return CodeMsg.REGISTER_FAIL;
    }

}
