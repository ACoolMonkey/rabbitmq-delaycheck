package com.hys.rabbitmq.delaycheck.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
import org.apache.ibatis.annotations.Mapper;

/**
 * 消息
 *
 * @author Robert Hou
 * @date 2020年04月23日 19:00
 **/
@Mapper
public interface MsgContentMapper extends BaseMapper<MessageContent> {
}
