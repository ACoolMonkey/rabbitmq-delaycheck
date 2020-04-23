package com.hys.rabbitmq.delaycheck.mq;

import com.alibaba.fastjson.JSON;
import com.hys.rabbitmq.delaycheck.constant.MsgConstant;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
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
     * @param messageContent 消息
     */
    public void sendMsg(MessageContent messageContent) {
        if (log.isDebugEnabled()) {
            log.debug("发送消息id：" + messageContent.getMsgId());
        }
        CorrelationData correlationData = new CorrelationData(messageContent.getMsgId() + "_" + messageContent.getOrderNo());
        rabbitTemplate.convertAndSend(MsgConstant.ORDER_TO_PRODUCT_EXCHANGE_NAME, MsgConstant.PRODUCT_TO_CALLBACK_ROUTING_KEY, JSON.toJSON(messageContent), correlationData);
    }
}
