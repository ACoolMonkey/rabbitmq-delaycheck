package com.hys.rabbitmq.delaycheck.enumuration;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 消息状态
 *
 * @author Robert Hou
 * @date 2020年04月23日 15:50
 **/
@Getter
@AllArgsConstructor
public enum MsgStatusEnum {

    /**
     * 发送中
     */
    SENDING(0, "发送中"),
    /**
     * 消费成功
     */
    CONSUMER_SUCCESS(1, "消费成功"),
    /**
     * 消费重试中
     */
    CONSUMER_RETRYING(2, "消费重试中"),
    /**
     * 消费失败
     */
    CONSUMER_FAIL(3, "消费失败");
    /**
     * 码值
     */
    private Integer code;
    /**
     * 含义
     */
    private String msgStatus;
}
