package com.hys.rabbitmq.delaycheck.constant;

/**
 * 消息常量
 *
 * @author Robert Hou
 * @date 2020年04月23日 15:58
 **/
public class MsgConstants {

    private MsgConstants() {
    }

    public static final String ORDER_TO_PRODUCT_EXCHANGE_NAME = "order-to-product.exchange";

    public static final String ORDER_TO_PRODUCT_QUEUE_NAME = "order-to-product.queue";

    public static final String PRODUCT_TO_CALLBACK_ROUTING_KEY = "product_to_callback_key";

    /**
     * 分布式锁
     */
    public static final String LOCK_KEY = "lock.key";
}
