package com.dong.dis.result;

import java.io.Serializable;

public class Result<T> implements Serializable {
    private int code;
    private  String msg;
    private  T data;
    private Result(T data){
        this.code = 0;
        this.msg = "success";
        this.data =data;
    }
    private Result(CodeMsg codeMsg){
        if(codeMsg==null){
            return ;
        }
        this.code = codeMsg.getCode();
        this.msg = codeMsg.getMsg();
    }
    public int getCode(){
        return code;
    }
    public static <T> Result<T> info(CodeMsg serverError) {
        return new Result<T>(serverError);
    }
    public static <T> Result<T> success(T data){
        return new Result<>(data);
    }
    public static <T> Result<T> success(CodeMsg serverError){
        return new Result<T>(serverError);
    }
    public static <T> Result<T> error(CodeMsg serverError){
        return new Result<T>(serverError);
    }
    public String getMsg(){
        return msg;
    }
    public T getData(){
        return data;
    }
}
