package com.hys.rabbitmq.delaycheck.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.hys.rabbitmq.delaycheck.model.OrderInfo;

/**
 * 订单
 *
 * @author Robert Hou
 * @date 2020年04月23日 05:03
 **/
public interface OrderService extends IService<OrderInfo> {

    /**
     * 保存订单
     *
     * @param orderInfo 订单
     */
    void saveOrder(OrderInfo orderInfo);
}
