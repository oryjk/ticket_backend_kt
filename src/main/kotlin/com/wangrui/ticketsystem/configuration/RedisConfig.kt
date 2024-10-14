package com.wangrui.ticketsystem.configuration

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer

@Configuration
class RedisConfig {

    @Bean
    fun objectMapper(javaTimeModule: Module): ObjectMapper {
        val objectMapper = ObjectMapper()
        objectMapper.registerModule(javaTimeModule)
        objectMapper.registerKotlinModule()
        return objectMapper
    }


    @Bean
    fun redisTemplate(
        objectMapper: ObjectMapper,
        redisConnectionFactory: RedisConnectionFactory
    ): RedisTemplate<String, Any> {
        val redisTemplate = RedisTemplate<String, Any>()
        redisTemplate.connectionFactory = redisConnectionFactory
        redisTemplate.keySerializer = StringRedisSerializer()
        redisTemplate.hashKeySerializer = StringRedisSerializer()

        val serializer = GenericJackson2JsonRedisSerializer(objectMapper)
        redisTemplate.valueSerializer = serializer
        redisTemplate.hashValueSerializer = serializer
        redisTemplate.afterPropertiesSet()
        return redisTemplate
    }
}