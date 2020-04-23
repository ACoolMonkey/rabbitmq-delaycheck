package com.hys.rabbitmq.delaycheck.mq;

import com.hys.rabbitmq.delaycheck.enumuration.OrderStatusEnum;
import com.hys.rabbitmq.delaycheck.model.OrderInfo;
import com.hys.rabbitmq.delaycheck.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.connection.CorrelationData;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 消息确认
 *
 * @author Robert Hou
 * @date 2020年04月23日 16:56
 **/
@Slf4j
@Component
public class MsgConfirmListener implements RabbitTemplate.ConfirmCallback {

    @Autowired
    private OrderService orderService;

    @Override
    public void confirm(CorrelationData correlationData, boolean ack, String cause) {
        String id = correlationData.getId();
        if (ack) {
            if (log.isDebugEnabled()) {
                log.debug("成功发送消息，id：" + id);
            }
        } else {
            dealMsgNack(id);
        }
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
