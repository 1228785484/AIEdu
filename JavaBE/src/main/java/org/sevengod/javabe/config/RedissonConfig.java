package org.sevengod.javabe.config;

import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;

@Configuration
public class RedissonConfig {
    
    @Bean
    public RedissonClient redissonClient() throws IOException {
        Config config = Config.fromYAML(getClass().getClassLoader().getResource("redisson.yaml"));
        // 设置JSON序列化
        config.setCodec(new JsonJacksonCodec());
        return Redisson.create(config);
    }
}
