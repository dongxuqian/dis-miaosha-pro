package com.dong.dis.constants;

/**
 * @author dong
 */

public enum MqTags {
    ORDER_CANCEL("order_cancel","取消订单"),
    ORDER_STOCK("order_stock","增加库存")
    ;
    private String tag;
    private String message;
    MqTags(String tag,String message){
        this.tag = tag;
        this.message = message;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
