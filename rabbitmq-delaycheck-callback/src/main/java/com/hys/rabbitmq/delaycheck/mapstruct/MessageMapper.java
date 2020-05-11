package com.hys.rabbitmq.delaycheck.mapstruct;

import com.hys.rabbitmq.delaycheck.constant.MsgConstants;
import com.hys.rabbitmq.delaycheck.enumration.MsgStatusEnum;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Mappings;
import org.springframework.amqp.core.Message;

import java.util.Date;

/**
 * 消息实体映射
 *
 * @author Robert Hou
 * @date 2020年04月24日 02:12
 **/
@Mapper(componentModel = "spring", imports = {MsgStatusEnum.class, MsgConstants.class, Date.class})
public interface MessageMapper {

    /**
     * 转换成功消息
     *
     * @param mc      消息内容
     * @param message 消息
     * @return 消息
     */
    @Mappings({
            @Mapping(source = "mc.msgId", target = "msgId"),
            @Mapping(source = "mc.orderNo", target = "orderNo"),
            @Mapping(source = "mc.productNo", target = "productNo"),
            @Mapping(target = "msgStatus", expression = "java(MsgStatusEnum.CONSUMER_SUCCESS.getCode())"),
            @Mapping(target = "exchange", expression = "java(message.getMessageProperties().getReceivedExchange())"),
            @Mapping(target = "routingKey", expression = "java(message.getMessageProperties().getReceivedRoutingKey())"),
            @Mapping(target = "errCause", constant = ""),
            @Mapping(target = "maxRetry", expression = "java(MsgConstants.MAX_RETRY_COUNT)"),
            @Mapping(target = "currentRetry", expression = "java(mc.getCurrentRetry() == null ? 0 : mc.getCurrentRetry())"),
            @Mapping(target = "createTime", expression = "java(new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())"),
            @Mapping(target = "retry", ignore = true)
    })
    MessageContent messageMapper(MessageContent mc, Message message);

    /**
     * 转换失败消息
     *
     * @param mc      消息内容
     * @param message 消息
     * @param e       异常
     * @return 消息
     */
    @Mappings({
            @Mapping(source = "mc.msgId", target = "msgId"),
            @Mapping(source = "mc.orderNo", target = "orderNo"),
            @Mapping(source = "mc.productNo", target = "productNo"),
            @Mapping(target = "msgStatus", expression = "java(MsgStatusEnum.CONSUMER_FAIL.getCode())"),
            @Mapping(target = "exchange", expression = "java(message.getMessageProperties().getReceivedExchange())"),
            @Mapping(target = "routingKey", expression = "java(message.getMessageProperties().getReceivedRoutingKey())"),
            @Mapping(target = "errCause", expression = "java(e.getMessage())"),
            @Mapping(target = "maxRetry", expression = "java(MsgConstants.MAX_RETRY_COUNT)"),
            @Mapping(target = "currentRetry", expression = "java(mc.getCurrentRetry() == null ? 0 : mc.getCurrentRetry())"),
            @Mapping(target = "createTime", expression = "java(mc.getCreateTime() != null ? mc.getCreateTime() :new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())"),
            @Mapping(target = "retry", ignore = true)
    })
    MessageContent failMessageMapper(MessageContent mc, Message message, Exception e);

    /**
     * 转换重试消息
     *
     * @param mc        消息内容
     * @param message   消息
     * @param maxRetry  最大重试次数
     * @param nextRetry 当前重试次数
     * @return 消息
     */
    @Mappings({
            @Mapping(target = "msgStatus", expression = "java(MsgStatusEnum.CONSUMER_RETRYING.getCode())"),
            @Mapping(target = "exchange", expression = "java(message.getMessageProperties().getReceivedExchange())"),
            @Mapping(target = "routingKey", expression = "java(message.getMessageProperties().getReceivedRoutingKey())"),
            @Mapping(target = "errCause", expression = "java(mc.getErrCause() == null ? \"\" : mc.getErrCause())"),
            @Mapping(source = "maxRetry", target = "maxRetry"),
            @Mapping(source = "nextRetry", target = "currentRetry"),
            @Mapping(target = "createTime", expression = "java(mc.getCreateTime() != null ? mc.getCreateTime() : new Date())"),
            @Mapping(target = "updateTime", expression = "java(new Date())"),
            @Mapping(target = "retry", constant = "true"),
            @Mapping(target = "msgId", ignore = true),
            @Mapping(target = "orderNo", ignore = true),
            @Mapping(target = "productNo", ignore = true)
    })
    MessageContent retryMessageMapper(MessageContent mc, Message message, Integer maxRetry, int nextRetry);
}
