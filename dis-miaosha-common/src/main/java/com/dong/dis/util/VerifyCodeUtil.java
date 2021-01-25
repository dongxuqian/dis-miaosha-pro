package com.dong.dis.util;

import com.dong.dis.api.Seckill.vo.VerifyCodeVo;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

public class VerifyCodeUtil {
    //用于生成运算符
    private static char[] ops = new char[]{'+','-','*'};
    /**
     * 创建验证码
     */
    public static VerifyCodeVo createVerifyCode(){
        int width = 80;
        int height = 32;
        //create the image
        BufferedImage image = new BufferedImage(width,height,BufferedImage.TYPE_INT_RGB);
        Graphics g  = image.getGraphics();
        //选择颜色
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0,0,width,height);
        //边界
        g.setColor(Color.BLACK);
        g.drawRect(0,0,width-1,height-1);
        Random rdm = new Random();
        //干扰
        for(int i = 0;i<50;i++){
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x,y,0,0);
        }
        //产生随机验证码
        String verifyCode = generateVerifyCode(rdm);
        g.setColor(new Color(0,100,0));
        g.setFont(new Font("Candara", Font.BOLD,24));
        g.drawString(verifyCode,8,24);
        g.dispose();
        // 计算表达式值，并把把验证码值存到redis中
        int expResult = calc(verifyCode);
        //输出图片和结果
        return new VerifyCodeVo(image, expResult);
    }

    /**
     * 生成验证码，只含有+/-/*
     * @param rdm
     * @return
     */
    private static String generateVerifyCode(Random rdm){
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3  = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        String exp = " "+num1+op1+num2+op2+num3;
        return exp;
    }

    /**
     * 使用 ScriptEngine 计算验证码中的数学表达式的值
     * @param exp
     * @return
     */
    private static int calc(String exp) {
        try {
            ScriptEngineManager manager = new ScriptEngineManager();
            ScriptEngine engine = manager.getEngineByName("JavaScript");
            return (Integer) engine.eval(exp);// 表达式计算
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

}
