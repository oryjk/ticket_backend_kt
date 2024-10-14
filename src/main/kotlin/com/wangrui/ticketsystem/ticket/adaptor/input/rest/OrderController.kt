package com.wangrui.ticketsystem.ticket.adaptor.input.rest

import com.wangrui.ticketsystem.extensions.slf4k
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderStatus
import com.wangrui.ticketsystem.ticket.application.port.input.OrderUseCase
import com.wangrui.ticketsystem.ticket.domain.service.OrderTaskManager
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/order")
@CrossOrigin
class OrderController(
   val orderTaskManager: OrderTaskManager, val orderUseCase: OrderUseCase
) {

    private val logger = slf4k()


    /**
     * tauri前端提交在使用
     */
    @PostMapping("/createSimpleOrder")
    fun createSimpleOrder(@RequestBody clientOrderRequest: ClientOrderRequest): String {
        val orderRequestEntity = OrderRequestEntity(
            clientOrderRequest.orderId,
            clientOrderRequest.matchId,
            clientOrderRequest.orderPayload,
            clientOrderRequest.loginCode,
            clientOrderRequest.token,
            LocalDateTime.now(),
            clientOrderRequest.clientTokenId,
            OrderStatus.ONGOING.status
        )
        orderUseCase.save(orderRequestEntity)
        logger.info("${clientOrderRequest.orderId} 接收到新的订单，已经保存到DB, 客户端推荐码为：${clientOrderRequest.clientTokenId}")
        val result = clientOrderRequest.clientTokenId + clientOrderRequest.orderId
        return result
    }



    data class ClientOrderRequest(
        val orderId: String,
        val matchId: String,
        val orderPayload: String,
        val loginCode: String,
        val token: String,
        val clientTokenId: String
    )

}