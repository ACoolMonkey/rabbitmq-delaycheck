package com.hys.rabbitmq.delaycheck.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hys.rabbitmq.delaycheck.constant.MsgConstant;
import com.hys.rabbitmq.delaycheck.enumration.MsgStatusEnum;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
import com.hys.rabbitmq.delaycheck.model.MsgTxt;
import com.hys.rabbitmq.delaycheck.service.MsgContentService;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RLock;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.Date;

/**
 * 监听消息
 *
 * @author Robert Hou
 * @date 2020年04月23日 18:03
 **/
@Slf4j
@Component
public class MsgConsumer {

    @Autowired
    private MsgContentService msgContentService;

    @Autowired
    private Redisson redisson;

    /**
     * 监听消息
     *
     * @param object  传输对象
     * @param message 消息
     * @param channel 信道
     * @throws IOException IOException
     */
    @RabbitHandler
    @RabbitListener(queues = {MsgConstant.PRODUCT_TO_CALLBACK_QUEUE_NAME})
    public void consumerConfirmMsg(@Payload JSONObject object, Message message, Channel channel) throws IOException {
        MsgTxt msgTxt = JSON.toJavaObject(object, MsgTxt.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //分布式锁
        RLock lock = redisson.getLock(MsgConstant.LOCK_CALLBACK_KEY);
        try {
            if (lock.tryLock()) {
                if (log.isDebugEnabled()) {
                    log.debug("消费消息，msgTxt：" + msgTxt);
                }
                //TODO:MapStruct
                MessageContent messageContent = new MessageContent();
                messageContent.setMsgId(msgTxt.getMsgId());
                messageContent.setOrderNo(msgTxt.getOrderNo());
                messageContent.setProductNo(msgTxt.getProductNo());
                messageContent.setMsgStatus(MsgStatusEnum.CONSUMER_SUCCESS.getCode());
                messageContent.setExchange(message.getMessageProperties().getReceivedExchange());
                messageContent.setRoutingKey(message.getMessageProperties().getReceivedRoutingKey());
                messageContent.setErrCause("");
                messageContent.setMaxRetry(MsgConstant.MAX_RETRY_COUNT);
                messageContent.setCurrentRetry(-1);
                Date date = new Date();
                messageContent.setCreateTime(date);
                messageContent.setUpdateTime(date);
                //插入消息
                msgContentService.save(messageContent);
                //消息签收
                channel.basicAck(deliveryTag, false);
            } else {
                log.warn("请不要重复消费消息！msgTxt：" + msgTxt);
                channel.basicReject(deliveryTag, false);
            }
        } finally {
            lock.unlock();
        }
    }

    /**
     * 监听延时消息
     *
     * @param object  传输对象
     * @param message 消息
     * @param channel 信道
     * @throws IOException IOException
     */
    @RabbitHandler
    @RabbitListener(queues = {MsgConstant.ORDER_TO_PRODUCT_DELAY_QUEUE_NAME})
    public void consumerCheckMsg(@Payload JSONObject object, Message message, Channel channel) throws IOException {
        MsgTxt msgTxt = JSON.toJavaObject(object, MsgTxt.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        String msgId = msgTxt.getMsgId().replace("_delay", "");
        MessageContent messageContent = msgContentService.getById(msgId);
        if (messageContent == null) {
            //消费者没有发送确认消息，需要回调生产者重新发送消息
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity("http://localhost:8080/retryMsg", msgTxt, String.class);
        }
        //消息签收
        channel.basicAck(deliveryTag, false);
    }
}
