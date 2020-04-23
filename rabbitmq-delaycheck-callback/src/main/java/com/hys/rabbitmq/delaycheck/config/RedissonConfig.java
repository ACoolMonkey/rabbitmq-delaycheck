package com.hys.rabbitmq.delaycheck.config;

import org.redisson.Redisson;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置类
 *
 * @author Robert Hou
 * @date 2020年04月23日 17:41
 **/
@Configuration
public class RedissonConfig {

    @Bean
    public Redisson redisson() {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.253.129:6379").setDatabase(0);
        return (Redisson) Redisson.create(config);
    }
}
