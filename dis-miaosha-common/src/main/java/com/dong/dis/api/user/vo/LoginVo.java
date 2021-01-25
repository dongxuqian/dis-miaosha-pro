package com.dong.dis.api.user.vo;



import com.dong.dis.validator.IsMobile;
import org.hibernate.validator.constraints.Length;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * 用于接收客户端请求中的表单数据，使用JSR303完成校验
 * @author dong
 */
public class LoginVo implements Serializable {
    @NotNull
    @IsMobile  //自定义校验
    private String mobile;
    @NotNull
    @Length(min = 32)
    private String password;

    @Override
    public String toString() {
        return "LoginVo{" +
                "mobile='" + mobile + '\'' +
                ", password='" + password + '\'' +
                '}';
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
