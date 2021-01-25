package com.dong.dis.exception;

import com.dong.dis.result.CodeMsg;
import com.dong.dis.result.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.validation.BindException;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * 全局异常处理器（底层使用方法拦截的方法完成，和aop一样）
 *在异常发生时，将会调用这里的方法给客户端一个响应
 * @author dong
 */
@ResponseBody
@ControllerAdvice
public class GlobalExceptionHandler {
    private static Logger logger = LoggerFactory.getLogger(GlobalException.class);

    /**
     * 异常处理
     * @param request
     * @param e
     * @return  json数据
     */
    @ExceptionHandler(value = Exception.class)//这个注解来确定处理什么异常，这里是所有
    public Result<String> exceptionHandler(HttpServletRequest request, Exception e){
        logger.info("出现异常");
        e.printStackTrace();
        //如果是自定义的全局异常，则按自定义的方式处理，否则按默认方式处理
        if(e instanceof  GlobalException){
            logger.debug("common模块的异常");
            GlobalException exception = (GlobalException)e ;
            return Result.error(exception.getCodeMsg());//向客户端返回异常信息
        }else if(e instanceof BindException){
            BindException bindException = (BindException) e;  //validation 错误
            List<ObjectError> errors = bindException.getAllErrors();
            ObjectError error = errors.get(0);
            String message = error.getDefaultMessage();
            return Result.error(CodeMsg.BIND_ERROR.fillArgs(message)); //动态拼接错误信息到已定义的信息上
        }else{
            return Result.error(CodeMsg.SERVER_ERROR);
        }
    }
}
