package com.hys.rabbitmq.delaycheck.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hys.rabbitmq.delaycheck.constant.MsgConstants;
import com.hys.rabbitmq.delaycheck.enumration.MsgStatusEnum;
import com.hys.rabbitmq.delaycheck.mapstruct.MessageMapper;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
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
    @Autowired
    private MessageMapper messageMapper;

    /**
     * 监听消息
     *
     * @param object  传输对象
     * @param message 消息
     * @param channel 信道
     * @throws IOException IOException
     */
    @RabbitHandler
    @RabbitListener(queues = {MsgConstants.PRODUCT_TO_CALLBACK_QUEUE_NAME})
    public void consumerConfirmMsg(@Payload JSONObject object, Message message, Channel channel) throws IOException {
        MessageContent mc = JSON.toJavaObject(object, MessageContent.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //分布式锁
        RLock lock = redisson.getLock(MsgConstants.LOCK_CALLBACK_KEY);
        try {
            if (lock.tryLock()) {
                if (log.isDebugEnabled()) {
                    log.debug("消费消息，messageContent：" + mc);
                }
                MessageContent messageContent = messageMapper.messageMapper(mc, message);
                //插入消息
                msgContentService.save(messageContent);
                //消息签收
                channel.basicAck(deliveryTag, false);
            } else {
                log.warn("请不要重复消费消息！messageContent：" + mc);
                channel.basicReject(deliveryTag, false);
            }
        } catch (Exception e) {
            //更新消息表状态为消费失败
            MessageContent messageContent = messageMapper.failMessageMapper(mc, message, e);
            msgContentService.saveOrUpdate(messageContent);
            channel.basicReject(deliveryTag, false);
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
    @RabbitListener(queues = {MsgConstants.ORDER_TO_PRODUCT_DELAY_QUEUE_NAME})
    public void consumerCheckMsg(@Payload JSONObject object, Message message, Channel channel) throws IOException {
        MessageContent mc = JSON.toJavaObject(object, MessageContent.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();

        MessageContent messageContent = msgContentService.getById(mc.getMsgId());
        if (messageContent == null || !messageContent.getMsgStatus().equals(MsgStatusEnum.CONSUMER_SUCCESS.getCode())) {
            //消费者没有发送确认消息、或者监听执行过程中失败，需要回调生产者重新发送消息
            Integer maxRetry = mc.getMaxRetry() == null ? MsgConstants.MAX_RETRY_COUNT : mc.getMaxRetry();
            int nextRetry = mc.getCurrentRetry() == null ? 1 : mc.getCurrentRetry() + 1;
            if (nextRetry >= maxRetry) {
                log.error("重试次数超过最大次数!messageContent：" + mc);
                mc.setMsgStatus(MsgStatusEnum.CONSUMER_FAIL.getCode());
                mc.setErrCause("重试次数超过最大次数!");
                mc.setCurrentRetry(nextRetry);
                mc.setUpdateTime(new Date());
                msgContentService.updateById(mc);
                channel.basicReject(deliveryTag, false);
                return;
            }
            mc = messageMapper.retryMessageMapper(mc, message, maxRetry, nextRetry);
            msgContentService.saveOrUpdate(mc);
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.postForEntity("http://localhost:8080/retryMsg", mc, String.class);
        }
        //消息签收
        channel.basicAck(deliveryTag, false);
    }
}
