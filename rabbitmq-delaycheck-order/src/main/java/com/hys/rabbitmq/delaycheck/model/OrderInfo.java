package com.hys.rabbitmq.delaycheck.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.hys.rabbitmq.delaycheck.enumuration.OrderStatusEnum;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Date;

/**
 * 订单
 *
 * @author Robert Hou
 * @date 2020年04月23日 05:13
 **/
@Data
@NoArgsConstructor
@TableName("order_info")
public class OrderInfo implements Serializable {

    private static final long serialVersionUID = 553016818679743095L;
    /**
     * 订单编号
     */
    @TableId
    private Long orderNo;
    /**
     * 客户名称
     */
    private String userName;
    /**
     * 金钱
     */
    private Long money;
    /**
     * 产品编号
     */
    private Long productNo;
    /**
     * 订单状态（0：订单生成 1：订单作废）
     */
    private Integer orderStatus = OrderStatusEnum.SUCCESS.getCode();
    /**
     * 创建时间
     */
    private Date createTime;
    /**
     * 修改时间
     */
    private Date updateTime;
}
