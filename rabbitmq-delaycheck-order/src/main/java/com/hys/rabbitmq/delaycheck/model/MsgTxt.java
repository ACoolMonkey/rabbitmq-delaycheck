package com.hys.rabbitmq.delaycheck.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 消息文本
 *
 * @author Robert Hou
 * @date 2020年04月23日 16:14
 **/
@Data
@NoArgsConstructor
public class MsgTxt implements Serializable {

    private static final long serialVersionUID = -1538310447654657518L;
    /**
     * 消息id
     */
    private String msgId;
    /**
     * 订单编号
     */
    private Long orderNo;
    /**
     * 产品编号
     */
    private Long productNo;
}
