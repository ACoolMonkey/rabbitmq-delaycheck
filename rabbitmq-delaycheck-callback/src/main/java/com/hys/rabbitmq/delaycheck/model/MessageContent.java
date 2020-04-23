package com.hys.rabbitmq.delaycheck.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息载体
 *
 * @author Robert Hou
 * @date 2020年04月23日 15:39
 **/
@Data
@NoArgsConstructor
@TableName("message_content")
public class MessageContent implements Serializable {

    private static final long serialVersionUID = -1187828689567541014L;
    /**
     * 消息id
     */
    @TableId
    private String msgId;
    /**
     * 订单编号
     */
    private Long orderNo;
    /**
     * 产品编号
     */
    private Long productNo;
    /**
     * 消息状态
     */
    private Integer msgStatus;
    /**
     * 交换机
     */
    private String exchange;
    /**
     * 路由键
     */
    private String routingKey;
    /**
     * 错误原因
     */
    private String errCause;
    /**
     * 最大重试次数
     */
    private Integer maxRetry;
    /**
     * 当前重试次数
     */
    private Integer currentRetry;
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
}
