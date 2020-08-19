package com.atguigu.gulimall.seckill.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

/**
 * @author Guo
 * @Create 2020/7/26 10:59
 */
@Configuration
public class MyRedissonConfig {

    /**
     * 所有对Redisson的搜使用通过RedissonClient对象
     * @return
     * @throws IOException
     */
    @Bean(destroyMethod="shutdown")
    RedissonClient redisson() throws IOException {
        Config config = new Config();
        config.useSingleServer().setAddress("redis://192.168.127.129:6379");

        //根据config创建出RedissonClient的实例
        RedissonClient redissonClient = Redisson.create(config);
        return redissonClient;
    }
}