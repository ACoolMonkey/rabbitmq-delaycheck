package com.hys.rabbitmq.delaycheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hys.rabbitmq.delaycheck.model.OrderInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 订单
 *
 * @author Robert Hou
 * @date 2020年04月23日 15:12
 **/
@Mapper
public interface OrderMapper extends BaseMapper<OrderInfo> {
}
