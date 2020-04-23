package com.hys.rabbitmq.delaycheck.model;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 产品
 *
 * @author Robert Hou
 * @date 2020年04月23日 17:51
 **/
@Data
@NoArgsConstructor
@TableName("product_info")
public class ProductInfo implements Serializable {

    private static final long serialVersionUID = -1520853275462923923L;
    /**
     * 商品编号
     */
    @TableId
    private Long productNo;
    /**
     * 商品名称
     */
    private String productName;
    /**
     * 商品数量
     */
    private Integer productNum;
}
