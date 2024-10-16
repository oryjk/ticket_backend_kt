package com.wangrui.ticketsystem

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.scheduling.annotation.EnableScheduling

@SpringBootApplication
@EnableScheduling
class TicketBackendKtApplication

fun main(args: Array<String>) {
    val runApplication = runApplication<TicketBackendKtApplication>(*args)

    val redisTemplate: RedisTemplate<String, Any> = runApplication.getBean("redisTemplate") as RedisTemplate<String, Any>

    Runtime.getRuntime().addShutdownHook(Thread {
        redisTemplate.delete("orderRequestKey")
    })
}
