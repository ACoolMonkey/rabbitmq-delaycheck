package com.hys.rabbitmq.delaycheck.controller;

import com.hys.rabbitmq.delaycheck.model.MessageContent;
import com.hys.rabbitmq.delaycheck.model.OrderInfo;
import com.hys.rabbitmq.delaycheck.mq.MsgSender;
import com.hys.rabbitmq.delaycheck.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

/**
 * 订单
 *
 * @author Robert Hou
 * @date 2020年04月23日 19:55
 **/
@Slf4j
@RestController
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private MsgSender msgSender;

    /**
     * 保存订单
     *
     * @return 处理结果
     */
    @RequestMapping("saveOrder")
    public String saveOrder() {
        OrderInfo orderInfo = new OrderInfo();
        orderInfo.setOrderNo(System.currentTimeMillis());
        orderInfo.setUserName("Robert Hou");
        orderInfo.setMoney(10000L);
        orderInfo.setProductNo(1L);
        orderInfo.setCreateTime(new Date());
        orderInfo.setUpdateTime(new Date());
        orderService.saveOrder(orderInfo);

        return "ok";
    }

    /**
     * 订单重试
     *
     * @return
     */
    @RequestMapping("retryMsg")
    public String retryMsg(@RequestBody MessageContent messageContent) {
        if (log.isDebugEnabled()) {
            log.debug("消息重新发送,messageContent：" + messageContent);
        }
        //初始发送消息
        msgSender.sendMsg(messageContent);
        //发送延时消息
        msgSender.sendDelayCheckMsg(messageContent);

        return "ok";
    }
}
