package com.dong.dis.api.mq;

import com.dong.dis.api.mq.vo.SkMessage;

/**
 * 消息队列服务
 * @author dong
 */
public interface MqProviderApi {
    /**
     * 将消息投递到mq中（direct模式）
     */
    void sendSkMessage(SkMessage message);
    void sendDelayMessage(SkMessage message);
    void sendCancelOrder(SkMessage message);
}
