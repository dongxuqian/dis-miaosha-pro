package com.dong.dis.validator;

import com.dong.dis.util.ValidatorUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;


/**
 * 真正实现用户手机号码检查的工具，被注解@isMobile使用
 * 需要实现javax.validation.ConstraintValidator，否则不能被@Constraint参数使用
 * 前面是所属的注解，后面为类型（电话号码为String）
 * @author dong
 */
public class IsMobileValidator implements ConstraintValidator<IsMobile,String> {
    private static Logger logger = LoggerFactory.getLogger(IsMobileValidator.class);
    //用于获取检验字段是否可以为空
    private boolean required = false;

    /**
     * 用于获取注解，初始化，得到注解中的require
     * @param constraintAnnotation
     */
    @Override
    public void initialize(IsMobile constraintAnnotation) {
        required = constraintAnnotation.require();
    }

    /**
     * 检验字段是否合法
     * @param s
     * @param constraintValidatorContext
     * @return
     */
    @Override
    public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
        logger.info("是否不可以为空： "+required);
        if(required){
            return ValidatorUtil.isMobile(s);
        }else{
            return StringUtils.isEmpty(s)||ValidatorUtil.isMobile(s);
        }
    }
}
