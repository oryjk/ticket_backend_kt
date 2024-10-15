package com.wangrui.ticketsystem.ticket.adaptor.input.rest

import com.wangrui.ticketsystem.extensions.slf4k
import com.wangrui.ticketsystem.ticket.adaptor.input.OrderListener
import com.wangrui.ticketsystem.ticket.adaptor.input.endpoint.AutoTaskEndpoint
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderStatus
import com.wangrui.ticketsystem.ticket.adaptor.output.UserInfoEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.UserInfoRepository
import com.wangrui.ticketsystem.ticket.application.port.input.OrderUseCase
import com.wangrui.ticketsystem.ticket.domain.UserInfo
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/order")
@CrossOrigin
class OrderController(val orderUseCase: OrderUseCase,
                      val userInfoRepository: UserInfoRepository,
                      val autoTaskEndpoint: AutoTaskEndpoint,
                      val orderListener: OrderListener) {

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

    @PostMapping("/createUserInfo")
    fun createSimpleOrder(@RequestBody userInfoRequest: UserInfoRequest): String {
        val userInfoEntityOptional = userInfoRepository.findById(userInfoRequest.userId)
        if (userInfoEntityOptional.isPresent) {
            val userInfoEntity = userInfoEntityOptional.get()
            userInfoRepository.save(
                UserInfoEntity.toUserInfoEntity(
                    userInfoRequest,
                    userInfoEntity.users,
                    userInfoEntity.iv,
                    userInfoEntity.encryptKey,
                    userInfoEntity.version,
                    userInfoEntity.expireTime,
                    userInfoEntity.regions,

                    )
            )
        } else {
            userInfoRepository.save(UserInfoEntity.toUserInfoEntity(userInfoRequest))
        }

        autoTaskEndpoint.createOrder()
        return userInfoRequest.userId
    }

    @GetMapping("/getJobs")
    fun getJobs(): Set<String> {
        return orderListener.getJobs()
    }


    data class ClientOrderRequest(val orderId: String,
                                  val matchId: String,
                                  val orderPayload: String,
                                  val loginCode: String,
                                  val token: String,
                                  val clientTokenId: String)

    data class UserInfoRequest(
        val userId: String,
        val member: List<UserInfo>,
        val loginCode: String,
        val token: String,
    )

}