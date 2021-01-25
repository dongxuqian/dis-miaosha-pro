package com.dong.dis.util;

import org.springframework.util.StringUtils;

import java.util.regex.Pattern;

/**
 * 手机号码格式校验工具
 */
public class ValidatorUtil {
    /**
     * 正则表达式验证
     */
    public  static  final  String REGEX_MOBILE = "^((13[0-9])|(14[5,7,9])|(15([0-3]|[5-9]))|(166)|(17[0,1,3,5,6,7,8])|(18[0-9])|(19[8|9]))\\d{8}$";
    public static boolean isMobile(String mobile){
        if(StringUtils.isEmpty(mobile)){
            return false;
        }
        return Pattern.matches(REGEX_MOBILE,mobile);
    }

//    public static void main(String[] args) {
//        System.out.println(isMobile("13373410066"));
//    }
}
