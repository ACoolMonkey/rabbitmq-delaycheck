package com.hys.rabbitmq.delaycheck.constant;

/**
 * 消息常量
 *
 * @author Robert Hou
 * @date 2020年04月23日 15:58
 **/
public class MsgConstant {

    private MsgConstant() {
    }

    /**
     * 延时发送时间（单位：秒）
     */
    public static final Integer DELAY_TIME = 30000;

    public static final String ORDER_TO_PRODUCT_EXCHANGE_NAME = "order-to-product.exchange";

    public static final String ORDER_TO_PRODUCT_QUEUE_NAME = "order-to-product.queue";

    public static final String ORDER_TO_PRODUCT_ROUTING_KEY = "order-to-product.key";

    public static final String PRODUCT_TO_CALLBACK_QUEUE_NAME = "product_to_callback_queue";

    public static final String PRODUCT_TO_CALLBACK_ROUTING_KEY = "product_to_callback_key";

    public static final String ORDER_TO_PRODUCT_DELAY_EXCHANGE_NAME = "order-to-product.delay-exchange";

    public static final String ORDER_TO_PRODUCT_DELAY_QUEUE_NAME = "order-to-product.delay-queue";

    public static final String ORDER_TO_PRODUCT_DELAY_ROUTING_KEY = "order-to-product.delay-key";
}
