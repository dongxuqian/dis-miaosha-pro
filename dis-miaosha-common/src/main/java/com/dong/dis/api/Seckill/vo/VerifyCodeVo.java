package com.dong.dis.api.Seckill.vo;

import java.awt.image.BufferedImage;
import java.io.Serializable;

/**
 * 验证码图片及计算结果
 * @author dong
 */
public class VerifyCodeVo implements Serializable {
    //验证码图片
    private BufferedImage image;
    //验证码计算结果
    private int expResult;
    public VerifyCodeVo(){

    }
    public VerifyCodeVo(BufferedImage image,int expResult){
            this.expResult = expResult;
            this.image  = image;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getExpResult() {
        return expResult;
    }

    public void setExpResult(int expResult) {
        this.expResult = expResult;
    }
}
