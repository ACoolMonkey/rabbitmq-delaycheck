package com.hys.rabbitmq.delaycheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hys.rabbitmq.delaycheck.mapper.ProductMapper;
import com.hys.rabbitmq.delaycheck.model.ProductInfo;
import com.hys.rabbitmq.delaycheck.service.ProductService;
import org.springframework.stereotype.Service;

/**
 * 商品
 *
 * @author Robert Hou
 * @date 2020年04月23日 17:58
 **/
@Service
public class ProductServiceImpl extends ServiceImpl<ProductMapper, ProductInfo> implements ProductService {
}
