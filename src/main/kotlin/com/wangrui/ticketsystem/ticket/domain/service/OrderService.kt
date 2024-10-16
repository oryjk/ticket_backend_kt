package com.wangrui.ticketsystem.ticket.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.wangrui.ticketsystem.extensions.slf4k
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import com.wangrui.ticketsystem.ticket.application.port.input.OrderTaskUseCase
import com.wangrui.ticketsystem.ticket.application.port.input.OrderUseCase
import com.wangrui.ticketsystem.ticket.application.port.input.RequestUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.OrderRequestDao
import com.wangrui.ticketsystem.ticket.domain.CreateMatchOrderResponse
import com.wangrui.ticketsystem.ticket.domain.CreateMatchOrderUrl
import com.wangrui.ticketsystem.ticket.domain.CreateOrderParam
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate

@Service
class OrderService(val requestUseCase: RequestUseCase,
                   val orderRequestDao: OrderRequestDao,
                   val redisTemplate: RedisTemplate<String, Any>,
                   val objectMapper: ObjectMapper,
                   val matchUseCase: MatchUseCase) : OrderUseCase {

    private val restTemplate = RestTemplate()
    private val orderRequestKey = "orderRequestKey"
    private val logger = slf4k()

    override fun sendOrder(requestParam: CreateOrderParam): CreateMatchOrderResponse {

        val queryParam = requestParam.queryParam
        val createMatchOrderUrl = CreateMatchOrderUrl(
            queryParam.userId, queryParam.timeStamp.toString(), queryParam.sign
        )
        val host = createMatchOrderUrl.host()
        val requestEntity = HttpEntity<Any>(
            jacksonObjectMapper().writeValueAsString(requestParam.requestBody),
            requestUseCase.getHeader(queryParam.token, host)
        )

        val url = createMatchOrderUrl.toString()
        logger.trace("url {}, body {}", url, requestEntity)
        val responseEntity =
            restTemplate.exchange(url, HttpMethod.POST, requestEntity, CreateMatchOrderResponse::class.java)
        return responseEntity.body!!
    }

    override fun getAutoBuyInfo(): List<OrderTaskUseCase.OrderRequest> {
        val orderRequests = redisTemplate.opsForSet().members(orderRequestKey)?.map {
            objectMapper.convertValue(it, OrderTaskUseCase.OrderRequest::class.java)
        }
        if (orderRequests!!.isEmpty()) {
            val matchId = matchUseCase.queryLatest().matchId

            return orderRequestDao.getAutoBuyInfo().filter { it.matchId == matchId }.also {
                    it.filter { orderRequest -> orderRequest.matchId == matchId }.forEach {
                        redisTemplate.opsForSet().add(orderRequestKey, it)
                    }
                }
        }
        return orderRequests
    }

    override fun save(orderRequestEntity: OrderRequestEntity) {
        redisTemplate.delete(orderRequestKey)
        orderRequestDao.save(orderRequestEntity)
    }

    override fun findByToken(token: String): List<OrderRequestEntity> {
        return orderRequestDao.findByToken(token)
    }

}