package com.hys.rabbitmq.delaycheck.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.hys.rabbitmq.delaycheck.constant.MsgConstants;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
import com.hys.rabbitmq.delaycheck.model.ProductInfo;
import com.hys.rabbitmq.delaycheck.service.ProductService;
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

import java.io.IOException;

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
    private ProductService productService;

    @Autowired
    private Redisson redisson;

    @Autowired
    private MsgSender msgSender;

    /**
     * 监听消息
     *
     * @param object  传输对象
     * @param message 消息
     * @param channel 信道
     * @throws IOException IOException
     */
    @RabbitHandler
    @RabbitListener(queues = {MsgConstants.ORDER_TO_PRODUCT_QUEUE_NAME})
    public void consumerMsgWithLock(@Payload JSONObject object, Message message, Channel channel) throws IOException {
        MessageContent messageContent = JSON.toJavaObject(object, MessageContent.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //分布式锁
        RLock lock = redisson.getLock(MsgConstants.LOCK_KEY);
        try {
            if (lock.tryLock()) {
                if (log.isDebugEnabled()) {
                    log.debug("消费消息，messageContent：" + messageContent);
                }
                if (messageContent.getRetry() == null || !messageContent.getRetry()) {
                    //产品库存-1
                    ProductInfo productInfo = productService.getById(messageContent.getProductNo());
                    if (productInfo.getProductNum() > 0) {
                        productInfo.setProductNum(productInfo.getProductNum() - 1);
                        productService.updateById(productInfo);
                    }
                }
                //发送一条确认消息到callback服务上
                msgSender.sendMsg(messageContent);
                //消息签收
                channel.basicAck(deliveryTag, false);
            } else {
                log.warn("请不要重复消费消息！messageContent：" + messageContent);
                channel.basicReject(deliveryTag, false);
            }
        } finally {
            lock.unlock();
        }
    }
}
