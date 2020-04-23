package com.hys.rabbitmq.delaycheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hys.rabbitmq.delaycheck.constant.MsgConstant;
import com.hys.rabbitmq.delaycheck.enumuration.MsgStatusEnum;
import com.hys.rabbitmq.delaycheck.mapper.OrderMapper;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
import com.hys.rabbitmq.delaycheck.model.MsgTxt;
import com.hys.rabbitmq.delaycheck.model.OrderInfo;
import com.hys.rabbitmq.delaycheck.mq.MsgSender;
import com.hys.rabbitmq.delaycheck.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.UUID;

/**
 * 订单
 *
 * @author Robert Hou
 * @date 2020年04月23日 15:31
 **/
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, OrderInfo> implements OrderService {

    @Autowired
    private MsgSender msgSender;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveOrder(OrderInfo orderInfo) {
        //构建消息对象
        MessageContent messageContent = buildMessageContent(orderInfo.getOrderNo(), orderInfo.getProductNo());
        //保存到数据库
        save(orderInfo);
        //构建消息发送对象
        MsgTxt msgTxt = new MsgTxt();
        msgTxt.setMsgId(messageContent.getMsgId());
        msgTxt.setOrderNo(orderInfo.getOrderNo());
        msgTxt.setProductNo(orderInfo.getProductNo());
        //初始发送消息
        msgSender.sendMsg(msgTxt);
        //发送延时消息
        msgSender.sendDelayCheckMsg(msgTxt);
    }

    /**
     * 构建消息对象
     *
     * @param orderNo   订单编号
     * @param productNo 产品编号
     * @return 消息
     */
    //TODO:是否删除？
    private MessageContent buildMessageContent(Long orderNo, Long productNo) {
        MessageContent messageContent = new MessageContent();
        messageContent.setMsgId(UUID.randomUUID().toString());
        messageContent.setOrderNo(orderNo);
        messageContent.setProductNo(productNo);
        messageContent.setMsgStatus(MsgStatusEnum.SENDING.getCode());
        messageContent.setExchange(MsgConstant.ORDER_TO_PRODUCT_EXCHANGE_NAME);
        messageContent.setRoutingKey(MsgConstant.ORDER_TO_PRODUCT_QUEUE_NAME);
        messageContent.setErrCause("");
        messageContent.setMaxRetry(MsgConstant.MSG_RETRY_COUNT);
        Date date = new Date();
        messageContent.setCreateTime(date);
        messageContent.setUpdateTime(date);
        return messageContent;
    }
}
