package com.hys.rabbitmq.delaycheck.config;

import com.hys.rabbitmq.delaycheck.constant.MsgConstants;
import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * RabbitMQ配置类
 *
 * @author Robert Hou
 * @date 2020年04月23日 04:59
 **/
@Configuration
public class RabbitConfig {

    @Bean
    public DirectExchange orderToProductExchange() {
        return new DirectExchange(MsgConstants.ORDER_TO_PRODUCT_EXCHANGE_NAME, true, false);
    }

    @Bean
    public Queue orderToProductQueue() {
        return new Queue(MsgConstants.ORDER_TO_PRODUCT_QUEUE_NAME, true);
    }

    @Bean
    public Binding orderToProductBinding() {
        return BindingBuilder.bind(orderToProductQueue()).to(orderToProductExchange()).with(MsgConstants.ORDER_TO_PRODUCT_ROUTING_KEY);
    }

    @Bean
    public Queue productToCallBackQueue() {
        return new Queue(MsgConstants.PRODUCT_TO_CALLBACK_QUEUE_NAME, true);
    }

    @Bean
    public Binding productToCallBackBinding() {
        return BindingBuilder.bind(productToCallBackQueue()).to(orderToProductExchange()).with(MsgConstants.PRODUCT_TO_CALLBACK_ROUTING_KEY);
    }

    @Bean
    public CustomExchange delayCheckExchange() {
        Map<String, Object> args = new HashMap<>(2);
        args.put("x-delayed-type", "direct");
        return new CustomExchange(MsgConstants.ORDER_TO_PRODUCT_DELAY_EXCHANGE_NAME, "x-delayed-message", true, false, args);
    }

    @Bean
    public Queue delayCheckQueue() {
        return new Queue(MsgConstants.ORDER_TO_PRODUCT_DELAY_QUEUE_NAME, true);
    }

    @Bean
    public Binding delayCheckBinding() {
        return BindingBuilder.bind(delayCheckQueue()).to(delayCheckExchange()).with(MsgConstants.ORDER_TO_PRODUCT_DELAY_ROUTING_KEY).noargs();
    }
}
