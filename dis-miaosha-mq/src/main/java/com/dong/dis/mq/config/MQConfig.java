package com.dong.dis.mq.config;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.DirectExchange;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;

import java.util.HashMap;
import java.util.Map;


/**
 * 通过配置文件获取消息队列
 * @author dong
 */
@Configuration
public class MQConfig {
    //队列名
    public static final String SECKILL_QUEUE = "seckill.queue";
    public static final  String DELAY_QUEUE = "delay.queue";
    public static final String  ORDER_CANCEL_QUEUE = "order.cancel.queue";
 //   public static  final String  ORDER_STOCK_QUEUE ="order.stock.queue";

    /**
     * 秒杀 routing key,生产者沿着routingkey将消息投递到exchange中
     */
    public static  final String SK_ROUTING_KEY = "routing.sk";
    public static final String DELAY_ROUTING_KEY = "routing.delay";
    public static final String SK_EXCHANGE = "seckill.exchange";
    public static final String DELAY_EXCHANGE = "delay.exchange";
    public static final String DELAY_ROUTING_KEY1 = "routing.delay1";
    public static  final String ORDER_CANCEL_EXCHANGE = "order.exchange";
    public static  final String ORDER_ROUTING_KEY = "routing.order";
   // public  static  final String STOCK_ROUTING_KEY = "routing.stock";
    /**
     * Direct 模式 交换机exchange，用于生成秒杀的queue
     */
    @Bean
    public DirectExchange orderExchange(){
        return new DirectExchange(SK_EXCHANGE);
    }
    @Bean
    public DirectExchange delayExchange(){
        return new DirectExchange(DELAY_EXCHANGE);
    }
    @Bean
    public DirectExchange cancelExchange(){
        return new DirectExchange(ORDER_CANCEL_EXCHANGE);
    }
    @Bean
    public Binding cancelBinding(){
        return BindingBuilder.bind(orderQueue()).to(cancelExchange()).with(ORDER_ROUTING_KEY);
    }
//    @Bean
//    public Binding stockBinding(){
//        return  BindingBuilder.bind(stockQueue()).to(cancelExchange()).with(STOCK_ROUTING_KEY);
//    }
    @Bean
    public Binding orderBinding(){
        return BindingBuilder.bind(seckillQueue()).to(orderExchange()).with(SK_ROUTING_KEY);
    }
    @Bean
    public Binding delayBinding(){
        return BindingBuilder.bind(delayQueue()).to(delayExchange()).with(DELAY_ROUTING_KEY);
    }
    @Bean
    public Binding delay1Binding(){
        return BindingBuilder.bind(delayQueue1()).to(delayExchange()).with(DELAY_ROUTING_KEY1);
    }
    @Bean
    public Queue seckillQueue(){
        return new Queue(SECKILL_QUEUE ,true);
    }
    @Bean
    public Queue delayQueue1(){
        Map<String,Object> params = new HashMap<>();
        params.put("x-dead-letter-exchange",DELAY_EXCHANGE);
        params.put("x-dead-letter-routing-key",DELAY_ROUTING_KEY);
        params.put("x-message-ttl", 5 * 1000);
        return new Queue(SECKILL_QUEUE,true,false,false,params);
    }
    @Bean
    public Queue delayQueue(){
        return new Queue(DELAY_QUEUE,true);
    }
    @Bean
    public Queue orderQueue(){
        return new Queue(ORDER_CANCEL_QUEUE,true);
    }
//    @Bean
//    public Queue stockQueue(){
//        return new Queue(ORDER_STOCK_QUEUE,true);
//    }
    @Bean
    @Scope("prototype")
    public RabbitTemplate rabbitTemplate (ConnectionFactory connectionFactory){
        RabbitTemplate template = new RabbitTemplate(connectionFactory);
        return template;
    }
}
