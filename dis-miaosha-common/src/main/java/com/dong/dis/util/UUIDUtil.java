package com.dong.dis.util;

import java.util.UUID;

/**
 * 生成 session
 */
public class UUIDUtil {
    public  static  String uuid(){
        return UUID.randomUUID().toString().replace("-","");
    }
}
