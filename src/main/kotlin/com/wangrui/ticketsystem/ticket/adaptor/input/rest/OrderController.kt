package com.wangrui.ticketsystem.ticket.adaptor.input.rest

import com.wangrui.ticketsystem.extensions.slf4k
import com.wangrui.ticketsystem.ticket.adaptor.input.OrderListener
import com.wangrui.ticketsystem.ticket.adaptor.input.endpoint.AutoTaskEndpoint
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderStatus
import com.wangrui.ticketsystem.ticket.adaptor.output.UserInfoEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.UserInfoRepository
import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import com.wangrui.ticketsystem.ticket.application.port.input.OrderUseCase
import com.wangrui.ticketsystem.ticket.domain.UserInfo
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.springframework.web.bind.annotation.*
import java.time.LocalDateTime

@RestController
@RequestMapping("/api/order")
@CrossOrigin
class OrderController(val orderUseCase: OrderUseCase,
                      val userInfoRepository: UserInfoRepository,
                      val autoTaskEndpoint: AutoTaskEndpoint,
                      val orderListener: OrderListener,
                      val matchUseCase: MatchUseCase) {

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

        return userInfoRequest.userId
    }

    @PostMapping("/bindUserInfo")
    fun bindUserInfo(@RequestBody userBindInfoRequest: UserBindInfoRequest): String {
        val userInfoEntityOptional = userInfoRepository.findById(userBindInfoRequest.userId)
        if (userInfoEntityOptional.isPresent) {
            val userInfoEntity = userInfoEntityOptional.get()
            userInfoRepository.save(
                UserInfoEntity(
                    userInfoEntity.userId,
                    userInfoEntity.loginCode,
                    userInfoEntity.token,
                    userInfoEntity.members,
                    userBindInfoRequest.users,
                    userBindInfoRequest.iv,
                    userBindInfoRequest.encryptKey,
                    userBindInfoRequest.version,
                    userBindInfoRequest.expireTime,
                    userBindInfoRequest.regions,

                    )
            )
        } else {
            throw IllegalArgumentException("没有找到用户id为 ${userBindInfoRequest.userId} 的用户，请先录入用户基础信息")
        }

        return userBindInfoRequest.userId
    }

    @PostMapping("/createOrders")
    fun createOrders(@RequestBody orderIds: List<String>): List<String> {
        return autoTaskEndpoint.createOrder(orderIds)
    }

    @PostMapping("/deleteOrders")
    fun deleteOrders(@RequestBody orderIds: List<String>):List<String> {
        runBlocking {
            orderIds.forEach {
                launch {
                    orderUseCase.deleteOrderById(it)
                    orderListener.cancelOrderJob(it)
                }
            }
        }
        return getJobs()
    }

    @GetMapping("/getUserCandidateOrders")
    fun getUserCandidateOrders(): Map<String, List<OrderInfoResult>> {
        return autoTaskEndpoint.getUserCandidateOrders()
    }

    @GetMapping("/getUserOrders")
    fun getUserOrders(): Map<String, List<OrderInfoResult>> {
        val matchId = matchUseCase.queryLatest().matchId
        val orderRequests = orderUseCase.getAutoBuyInfo()
        return orderRequests.filter { it.matchId == matchId }
            .map { OrderInfoResult(it.orderId, it.orderId.split("|").first().toInt(), "") }
            .groupBy { it.orderId.split("|").first() }
    }

    @GetMapping("/getJobs")
    fun getJobs(): List<String> {
        return orderListener.getJobs().keys.toList()
//        return listOf("xxx1|aaa|aaa","xxx2|aaa|aaa","xxx3|aaa|aaa")
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

    data class UserBindInfoRequest(
        val userId: String,
        val users: String = "",
        val iv: String = "",
        val encryptKey: String = "",
        val version: Int = 0,
        val expireTime: Long = 0,
        val regions: String = "",
    )

    data class OrderInfoResult(
        val orderId: String,
        val userId: Int,
        val realName: String
    )


}