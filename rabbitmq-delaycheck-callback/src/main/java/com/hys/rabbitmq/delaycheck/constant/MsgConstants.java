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

    public static final String PRODUCT_TO_CALLBACK_QUEUE_NAME = "product_to_callback_queue";

    public static final String ORDER_TO_PRODUCT_DELAY_QUEUE_NAME = "order-to-product.delay-queue";

    /**
     * 分布式锁
     */
    public static final String LOCK_CALLBACK_KEY = "lock.key.callback";
    /**
     * 最大重试次数
     */
    public static final Integer MAX_RETRY_COUNT = 5;
}
