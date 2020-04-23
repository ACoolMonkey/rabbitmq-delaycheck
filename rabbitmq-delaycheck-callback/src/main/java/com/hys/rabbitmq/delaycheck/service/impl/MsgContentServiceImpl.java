package com.hys.rabbitmq.delaycheck.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.hys.rabbitmq.delaycheck.mapper.MsgContentMapper;
import com.hys.rabbitmq.delaycheck.model.MessageContent;
import com.hys.rabbitmq.delaycheck.service.MsgContentService;
import org.springframework.stereotype.Service;

/**
 * 消息
 *
 * @author Robert Hou
 * @date 2020年04月23日 19:02
 **/
@Service
public class MsgContentServiceImpl extends ServiceImpl<MsgContentMapper, MessageContent> implements MsgContentService {
}
