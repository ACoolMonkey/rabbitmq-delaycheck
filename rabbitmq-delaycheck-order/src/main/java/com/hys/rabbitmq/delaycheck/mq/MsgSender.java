package com.hys.rabbitmq.delaycheck.mq;

import com.alibaba.fastjson.JSON;
import com.hys.rabbitmq.delaycheck.constant.MsgConstant;
import com.hys.rabbitmq.delaycheck.model.MsgTxt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 发送消息
 *
 * @author Robert Hou
 * @date 2020年04月23日 16:18
 **/
@Slf4j
@Component
public class MsgSender {

    @Autowired
    private RabbitTemplate rabbitTemplate;

    /**
     * 发送消息
     *
     * @param msgTxt 消息
     */
    public void sendMsg(MsgTxt msgTxt) {
        if (log.isDebugEnabled()) {
            log.debug("发送消息id：" + msgTxt.getMsgId());
        }
        CorrelationData correlationData = new CorrelationData(msgTxt.getMsgId() + "_" + msgTxt.getOrderNo());
        rabbitTemplate.convertAndSend(MsgConstant.ORDER_TO_PRODUCT_EXCHANGE_NAME, MsgConstant.ORDER_TO_PRODUCT_ROUTING_KEY, JSON.toJSON(msgTxt), correlationData);
    }

    /**
     * 发送延时消息
     *
     * @param msgTxt 消息
     */
    public void sendDelayCheckMsg(MsgTxt msgTxt) {
        if (log.isDebugEnabled()) {
            log.debug("发送延时消息id：" + msgTxt.getMsgId());
        }
        CorrelationData correlationData = new CorrelationData(msgTxt.getMsgId() + "_" + msgTxt.getOrderNo() + "_delay");
        rabbitTemplate.convertAndSend(MsgConstant.ORDER_TO_PRODUCT_DELAY_EXCHANGE_NAME, MsgConstant.ORDER_TO_PRODUCT_DELAY_ROUTING_KEY, JSON.toJSON(msgTxt), message -> {
            //设置延时时间
            message.getMessageProperties().setHeader("x-delay", MsgConstant.DELAY_TIME);
            return message;
        }, correlationData);
    }
}
