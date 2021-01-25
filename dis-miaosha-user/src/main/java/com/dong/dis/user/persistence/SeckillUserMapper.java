package com.dong.dis.user.persistence;

import com.dong.dis.user.domain.SeckillUser;
import org.apache.ibatis.annotations.*;

/**
 * seckill_user表交互接口
 * @author dong
 */
@Mapper
public interface SeckillUserMapper {
   @Select("select * from seckill_user where phone = #{phone}")
   SeckillUser getUserByPhone(@Param("phone") Long phone);
   @Update("update secKill_user set password = # {password} where id = #{uuid}")
   void updatePassword (SeckillUser updateUser);

   long insertUser(SeckillUser seckillUser);
}
