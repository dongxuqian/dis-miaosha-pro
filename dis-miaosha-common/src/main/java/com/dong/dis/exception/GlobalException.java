package com.dong.dis.exception;

import com.dong.dis.result.CodeMsg;

/**
 * 全局异常处理器
 * @author dong
 */
public class GlobalException extends  RuntimeException {
    private CodeMsg codeMsg;

    /**
     * 使用构造器接收CodeMsg
     * @param codeMsg
     */
    public GlobalException(CodeMsg codeMsg){
        this.codeMsg = codeMsg;
    }
    public CodeMsg getCodeMsg(){
        return codeMsg;
    }
}
