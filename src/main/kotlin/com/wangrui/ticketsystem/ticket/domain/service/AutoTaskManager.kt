package com.wangrui.ticketsystem.ticket.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wangrui.ticketsystem.ticket.adaptor.input.OrderListener
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderStatus
import com.wangrui.ticketsystem.ticket.application.port.input.EncryptUtils
import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import com.wangrui.ticketsystem.ticket.application.port.input.OrderUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.UserDao.Companion.autoUserInfoKey
import com.wangrui.ticketsystem.ticket.config.BaseUrlConfig
import com.wangrui.ticketsystem.ticket.config.GlobalConfig
import com.wangrui.ticketsystem.ticket.domain.FormData
import com.wangrui.ticketsystem.ticket.domain.OrderPayloadRoot
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class AutoTaskManager(
    val requestParamService: RequestParamService,
    val matchUseCase: MatchUseCase,
    val redisTemplate: RedisTemplate<String, Any>,
    @Value("\${autoCreateOrderWhenStartUp:false}") val autoCreateOrderWhenStartUp: Boolean,
    val generalTicketService: GeneralTicketService,
    val orderListener: OrderListener,
    val orderUseCase: OrderUseCase,
    val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val timeDelay = 5000L
    private val clientToken = "MUX9cBF8"


    fun loop() {
        if (!autoCreateOrderWhenStartUp) {
            return
        }
        val matchId = matchUseCase.queryLatest().matchId

        redisTemplate.delete(autoUserInfoKey)

        scope.launch {
            while (true) {
                val requestParam = requestParamService.getRobinUser()
                if (StringUtils.isEmpty(requestParam.useUrlLid2)) {
                    logger.info("当前没有用户，等待一下")
                    delay(timeDelay)
                    continue
                }
                generalTicketService.getLatestTicketInfo(
                    GlobalConfig.getUrl(
                        GlobalConfig.Api.GetBillRegion, BaseUrlConfig.getBaseUrl(), requestParam.useUrlLid2
                    ),
                    matchId.toLong(), requestParam,
                )
                delay(timeDelay)
            }

        }

    }

    fun ssss() {
        loopOrderRequest()

        val encryptKey = "RWhayTeZw9Z15wBKJW7GDA=="
        val expireTime = 1727539721988
        val iv = "164e0d966aab617a"
        val version = 88

        val dataString =
            "{\"agree\":true,\"id\":\"33\",\"regions\":[{\"region\":6,\"estate\":3,\"num\":1,\"name\":\"503\",\"price\":\"100.00\",\"usable_count\":\"1\"}],\"users\":[{\"id\":116692,\"uid\":116692,\"realname\":\"王睿\",\"real_card_id\":\"510***********6011\",\"phone\":\"186****2970\",\"is_self\":true,\"real_card_id2\":\"510112198905246011\",\"phone2\":\"18602812970\",\"timestamp\":1727536109,\"signature\":\"a9848cd469193f766b90861981356e44\",\"disabled\":false,\"disabled2\":false,\"showText\":\"王睿 510***********6011\"}]}"
        val formData = objectMapper.readValue<FormData>(dataString)
        val orderPayloadRoot = OrderPayloadRoot.convertFormData2Object(
            EncryptUtils.EncryptionParams(encryptKey, iv, version, expireTime),
            formData
        )
        val payLoad = OrderPayloadRoot.convertObject2Json(orderPayloadRoot)
        orderUseCase.save(
            OrderRequestEntity(
                "testestete====",
                "33",
                payLoad,
                "0c1OBvFa1UXofI0Z6VFa1fZ3ip3OBvF5",
                "Bearer eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjExNjY5Miwib2lkIjoiMWYxMGI5OWJkODNiZTAxN2Q3NDE5MGM0MGYyNmJhMDgiLCJsaWQiOjAsInNpZGUiOiJ3eF9hcGkiLCJhdWQiOiIiLCJleHAiOjE3Mjc2MDg4ODYsImlhdCI6MTcyNzUzNjg4NiwiaXNzIjoiIiwianRpIjoiYjExOTg0Njg1MDMxNGY4NjVkYzhkYWI1Y2I3NmQ5MjQiLCJuYmYiOjE3Mjc1MzY4ODYsInN1YiI6IiJ9.5k4fID0J_lfc5peqBfV41L5fWkGG1FSkjbmnLdMoD00",
                LocalDateTime.now(),
                clientToken,
                OrderStatus.ONGOING.status
            )
        )
    }

    fun loopOrderRequest() {
        scope.launch {
            while (true) {
                orderListener.createOrders()
                TimeUnit.SECONDS.sleep(1)
            }
        }

    }

}