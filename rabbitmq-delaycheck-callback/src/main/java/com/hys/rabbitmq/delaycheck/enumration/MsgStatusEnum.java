package com.hys.rabbitmq.delaycheck.enumration;

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
     * 消息发送成功
     */
    SENDING_SUCCESS(1, "消息发送成功"),
    /**
     * 消息发送失败
     */
    SENDING_FAIL(2, "消息发送失败"),
    /**
     * 消费成功
     */
    CONSUMER_SUCCESS(3, "消费成功"),
    /**
     * 消费失败
     */
    CONSUMER_FAIL(4, "消费失败");
    /**
     * 码值
     */
    private Integer code;
    /**
     * 含义
     */
    private String msgStatus;
}
