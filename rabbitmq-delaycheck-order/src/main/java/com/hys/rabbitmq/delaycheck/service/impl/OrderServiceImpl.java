package com.hys.rabbitmq.delaycheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hys.rabbitmq.delaycheck.mapper.OrderMapper;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
import com.hys.rabbitmq.delaycheck.model.OrderInfo;
import com.hys.rabbitmq.delaycheck.mq.MsgSender;
import com.hys.rabbitmq.delaycheck.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
        //保存到数据库
        save(orderInfo);
        //构建消息发送对象
        MessageContent messageContent = new MessageContent();
        messageContent.setMsgId(UUID.randomUUID().toString());
        messageContent.setOrderNo(orderInfo.getOrderNo());
        messageContent.setProductNo(orderInfo.getProductNo());
        //初始发送消息
        msgSender.sendMsg(messageContent);
        //发送延时消息
        msgSender.sendDelayCheckMsg(messageContent);
    }
}
