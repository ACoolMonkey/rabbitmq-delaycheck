package com.hys.rabbitmq.delaycheck.mq;

import com.alibaba.fastjson.JSON;
import com.hys.rabbitmq.delaycheck.constant.MsgConstant;
import com.hys.rabbitmq.delaycheck.enumuration.OrderStatusEnum;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
import com.hys.rabbitmq.delaycheck.model.OrderInfo;
import com.hys.rabbitmq.delaycheck.service.OrderService;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ConfirmCallback;
import org.springframework.amqp.rabbit.core.RabbitTemplate.ReturnCallback;
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
    @Autowired
    private OrderService orderService;

    private final ConfirmCallback CONFIRM_CALLBACK = (correlationData, ack, cause) -> {
        String id = correlationData.getId();
        if (ack) {
            if (log.isDebugEnabled()) {
                log.debug("成功发送消息，id：" + id);
            }
        } else {
            dealMsgNack(id);
        }
    };

    /**
     * 发送消息
     *
     * @param messageContent 消息
     */
    public void sendMsg(MessageContent messageContent) {
        if (log.isDebugEnabled()) {
            log.debug("发送消息id：" + messageContent.getMsgId());
        }
        rabbitTemplate.setConfirmCallback(CONFIRM_CALLBACK);
        CorrelationData correlationData = new CorrelationData(messageContent.getMsgId() + "_" + messageContent.getOrderNo());
        rabbitTemplate.convertAndSend(MsgConstant.ORDER_TO_PRODUCT_EXCHANGE_NAME, MsgConstant.ORDER_TO_PRODUCT_ROUTING_KEY, JSON.toJSON(messageContent), correlationData);
    }

    /**
     * 发送延时消息
     *
     * @param messageContent 消息
     */
    public void sendDelayCheckMsg(MessageContent messageContent) {
        if (log.isDebugEnabled()) {
            log.debug("发送延时消息id：" + messageContent.getMsgId());
        }
        rabbitTemplate.setConfirmCallback(CONFIRM_CALLBACK);
        CorrelationData correlationData = new CorrelationData(messageContent.getMsgId() + "_" + messageContent.getOrderNo() + "_delay");
        rabbitTemplate.convertAndSend(MsgConstant.ORDER_TO_PRODUCT_DELAY_EXCHANGE_NAME, MsgConstant.ORDER_TO_PRODUCT_DELAY_ROUTING_KEY, JSON.toJSON(messageContent), message -> {
            //设置延时时间
            message.getMessageProperties().setHeader("x-delay", MsgConstant.DELAY_TIME);
            return message;
        }, correlationData);
    }

    /**
     * 处理消息失败
     *
     * @param id 消息id
     */
    private void dealMsgNack(String id) {
        if (!id.contains("delay")) {
            log.error("发送业务消息失败！，id：" + id);
            long orderNo = Long.parseLong(id.split("_")[1]);
            //将订单置为作废状态
            OrderInfo orderInfo = new OrderInfo();
            orderInfo.setOrderNo(orderNo);
            orderInfo.setOrderStatus(OrderStatusEnum.ERROR.getCode());
            orderService.updateById(orderInfo);
        } else {
            log.error("发送延时消息失败！，id：" + id);
        }
    }
}
