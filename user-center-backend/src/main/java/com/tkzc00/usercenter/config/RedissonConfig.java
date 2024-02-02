package com.tkzc00.usercenter.config;

import lombok.Data;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Redisson配置
 * 用于分布式锁
 *
 * @author tkzc00
 */
@Configuration
@ConfigurationProperties(prefix = "spring.redis")
@Data
public class RedissonConfig {
    private String port;
    private String host;
    @Bean
    public RedissonClient redissonClient() {
        // 创建配置
        Config config = new Config();
        config.useSingleServer().setAddress(String.format("redis://%s:%s", host, port)).setDatabase(3);
        // 创建示例并返回
        return Redisson.create(config);
    }
}
