package com.wangrui.ticketsystem.ticket.domain.service

import com.wangrui.ticketsystem.ticket.adaptor.input.OrderListener
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderStatus
import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import com.wangrui.ticketsystem.ticket.application.port.input.OrderUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.UserDao.Companion.autoUserInfoKey
import com.wangrui.ticketsystem.ticket.config.BaseUrlConfig
import com.wangrui.ticketsystem.ticket.config.GlobalConfig
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

@Service
class AutoTaskManager(val requestParamService: RequestParamService,
                      val matchUseCase: MatchUseCase,
                      val redisTemplate: RedisTemplate<String, Any>,
                      @Value("\${autoCreateOrderWhenStartUp:false}") val autoCreateOrderWhenStartUp: Boolean,
                      val generalTicketService: GeneralTicketService,
                      val orderListener: OrderListener,
                      val orderUseCase: OrderUseCase) {
    private val logger = LoggerFactory.getLogger(javaClass)
    private val scope = CoroutineScope(Dispatchers.IO)
    private val timeDelay = 5000L
    private val clientToken = "MUX9cBF8"

    /**
     * 监控余票信息
     */
    fun watchLatestTicketInfo() {

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

    /**
     * 自动创建订单
     */
    fun createOrder(orderId: String, loginCode: String, token: String, orderPayloadRoot: OrderPayloadRoot) {
        val payLoad = OrderPayloadRoot.convertObject2Json(orderPayloadRoot)
        orderUseCase.save(
            OrderRequestEntity(
                orderId,
                orderPayloadRoot.id,
                payLoad,
                loginCode,
                token,
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
                delay(1000L)
            }
        }

    }

}