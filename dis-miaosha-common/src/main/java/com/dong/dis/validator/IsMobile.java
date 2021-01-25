package com.dong.dis.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

/**
 * 手机号码的校验注解
 * @author dong
 */
//作用在什么上
@Target({ElementType.METHOD,ElementType.FIELD,ElementType.ANNOTATION_TYPE,ElementType.CONSTRUCTOR,ElementType.TYPE_USE,ElementType.PARAMETER})
//保留到运行时
@Retention(RetentionPolicy.RUNTIME)
@Documented
//使用哪个类来校验，要实现特定接口
@Constraint(validatedBy = {IsMobileValidator.class})
public @interface IsMobile {
    //默认不为空
    boolean require() default  true;
    //校验不通过的提示信息
    String message() default  "手机号码格式有误!";
    //下面为@constrai 要求，默认为空
    Class<?>[] groups() default {};
    Class<?extends Payload>[] payload() default {};
}
