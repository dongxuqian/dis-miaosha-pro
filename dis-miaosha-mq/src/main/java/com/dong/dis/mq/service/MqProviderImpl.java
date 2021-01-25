package com.dong.dis.mq.service;

import com.dong.dis.api.mq.MqProviderApi;
import com.dong.dis.api.mq.vo.SkMessage;
import com.dong.dis.mq.config.MQConfig;
import org.apache.dubbo.config.annotation.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.UUID;

@Service(interfaceClass = MqProviderApi.class)
public class MqProviderImpl implements MqProviderApi, RabbitTemplate.ConfirmCallback, RabbitTemplate.ReturnCallback {
    private static Logger logger = LoggerFactory.getLogger(MqProviderImpl.class);
    @Autowired
    private RabbitTemplate rabbitTemplate;
    public MqProviderImpl(RabbitTemplate rabbitTemplate){
        this.rabbitTemplate = rabbitTemplate;
        //设置ack回调
       rabbitTemplate.setConfirmCallback(this);
       rabbitTemplate.setReturnCallback(this);
    }
    @Override
    public void sendSkMessage(SkMessage message) {
        logger.info("MQ send message: " + message);
        // 秒杀消息关联的数据
        CorrelationData skCorrData = new CorrelationData(UUID.randomUUID().toString());
        // 第一个参数为消息队列名(此处也为routingKey)，第二个参数为发送的消息
        rabbitTemplate.convertAndSend(MQConfig.SECKILL_QUEUE, message, skCorrData);

    }

    @Override
    public void sendDelayMessage(SkMessage message) {
        rabbitTemplate.convertAndSend(MQConfig.DELAY_QUEUE,message);
    }

    @Override
    public void sendCancelOrder(SkMessage message) {
        rabbitTemplate.convertAndSend(MQConfig.ORDER_CANCEL_EXCHANGE,MQConfig.ORDER_ROUTING_KEY,message);
    }

    /**
     * MQ ack 机制
     * 验证机制，确保消息能够被消费，且不影响消息吞吐量
     */
    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        logger.info("SkMessage UUID: " + correlationData.getId());
        if (ack) {
            logger.info("SkMessage 消息消费成功！");
        } else {
            System.out.println("SkMessage 消息消费失败！");
        }

        if (cause != null) {
            logger.info("CallBackConfirm Cause: " + cause);
        }
    }

    @Override
    public void returnedMessage(Message message, int i, String s, String s1, String s2) {
        logger.info("消息丢失:exchange({}),route({}),replyCode({}),replyText({}),message:{}", s1,s2, i, s, message);
    }
}
