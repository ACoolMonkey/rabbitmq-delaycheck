package com.hys.rabbitmq.delaycheck.enumuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 订单状态
 *
 * @author Robert Hou
 * @date 2020年04月23日 05:18
 **/
@Getter
@AllArgsConstructor
public enum OrderStatusEnum {

    /**
     * 订单生成
     */
    SUCCESS(0, "订单生成"),
    /**
     * 订单作废
     */
    ERROR(1, "订单作废");
    /**
     * 码值
     */
    private Integer code;
    /**
     * 含义
     */
    private String message;
}
