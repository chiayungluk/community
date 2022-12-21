package io.chiayungluk.community;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.ReactiveRedisConnectionFactory;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.ReactiveRedisOperations;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class PostConfiguration {

    @Bean
    ReactiveRedisOperations<String, PostDto> redisOperations(ReactiveRedisConnectionFactory factory) {
        RedisSerializationContext.RedisSerializationContextBuilder<String, PostDto> builder =
                RedisSerializationContext.newSerializationContext(new StringRedisSerializer());

        RedisSerializationContext<String, PostDto> context =
                builder.value(new Jackson2JsonRedisSerializer<>(PostDto.class)).build();

        return new ReactiveRedisTemplate<>(factory, context);
    }
}
