package com.hys.rabbitmq.delaycheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hys.rabbitmq.delaycheck.model.ProductInfo;
import org.apache.ibatis.annotations.Mapper;

/**
 * 商品
 *
 * @author Robert Hou
 * @date 2020年04月23日 17:55
 **/
@Mapper
public interface ProductMapper extends BaseMapper<ProductInfo> {
}
