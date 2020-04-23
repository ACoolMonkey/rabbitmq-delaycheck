package com.hys.rabbitmq.delaycheck.mq;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hys.rabbitmq.delaycheck.constant.MsgConstant;
import com.hys.rabbitmq.delaycheck.model.MsgTxt;
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
    @RabbitListener(queues = {MsgConstant.ORDER_TO_PRODUCT_QUEUE_NAME})
    public void consumerMsgWithLock(@Payload JSONObject object, Message message, Channel channel) throws IOException {
        MsgTxt msgTxt = JSON.toJavaObject(object, MsgTxt.class);
        long deliveryTag = message.getMessageProperties().getDeliveryTag();
        //分布式锁
        RLock lock = redisson.getLock(MsgConstant.LOCK_KEY);
        try {
            if (lock.tryLock()) {
                if (log.isDebugEnabled()) {
                    log.debug("消费消息，msgTxt：" + msgTxt);
                }
                //产品库存-1
                ProductInfo productInfo = productService.getById(msgTxt.getProductNo());
                if (productInfo.getProductNum() > 0) {
                    productInfo.setProductNum(productInfo.getProductNum() - 1);
                    productService.updateById(productInfo);
                }
                //发送一条确认消息到callback服务上
                msgSender.sendMsg(msgTxt);
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
}
