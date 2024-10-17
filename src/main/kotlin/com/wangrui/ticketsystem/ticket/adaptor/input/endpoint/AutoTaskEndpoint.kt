package com.wangrui.ticketsystem.ticket.adaptor.input.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wangrui.ticketsystem.extensions.slf4k
import com.wangrui.ticketsystem.ticket.adaptor.input.rest.OrderController
import com.wangrui.ticketsystem.ticket.adaptor.output.UserInfoEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.UserInfoRepository
import com.wangrui.ticketsystem.ticket.application.port.input.EncryptUtils
import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.TicketDao
import com.wangrui.ticketsystem.ticket.domain.*
import com.wangrui.ticketsystem.ticket.domain.service.AutoTaskManager
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class AutoTaskEndpoint(val autoTaskManager: AutoTaskManager,
                       val ticketDao: TicketDao,
                       val userInfoRepository: UserInfoRepository,
                       val objectMapper: ObjectMapper,
                       val matchUseCase: MatchUseCase) {


    private val logger = slf4k()


    @PostConstruct
    fun autoTask() {
        autoTaskManager.loopOrderRequest()
//        autoTaskManager.watchLatestTicketInfo()
    }

    fun getUserCandidateOrders(): Map<String, List<OrderController.OrderInfoResult>> {
        val matchId = matchUseCase.queryLatest().matchId
        val allTicket = ticketDao.queryAllTicket()

        return findValidUserInfo().filter { user -> !StringUtils.isEmpty(user.users) }.map { user ->
            var regions = user.regions.split(",")
            if (regions.size == 0 || StringUtils.isEmpty(user.regions)) {
                regions = allTicket.values.sortedBy { it.price.toDouble() }.map { it.name }
            }
            val userOrders = regions.filter { allTicket.containsKey(it) }.flatMap { regionName ->
                val region = allTicket[regionName]!!
                val orderInfoResults =
                    user.users.split(",").filter { userId -> !StringUtils.isEmpty(userId) }.map { userId ->
                        val userInfos = objectMapper.readValue<List<UserInfo>>(user.members).associateBy { it.id }
                        val userInfo = userInfos[userId.toInt()]!!
                        OrderController.OrderInfoResult(
                            "${userInfo.id}|$matchId|${region.name}", userInfo.id, userInfo.realname
                        )

                    }
                orderInfoResults
            }
            user.userId to userOrders
        }.toMap()
    }


    fun createOrder(orderIds: List<String>): List<String> {
        logger.info("每隔5秒扫描一下订单")
        val matchId = matchUseCase.queryLatest().matchId
        val allTicket = ticketDao.queryAllTicket()
        val userOrderMap = orderIds.map { it.split("|") } // 将每个字符串拆分为列表
            .groupBy({ it.first().toInt() }, { it.last() }) // 根据第一个元素分组，并将最后一个元素作为值的列表

        return findValidUserInfo().flatMap { user ->
            objectMapper.readValue<List<UserInfo>>(user.members).filter {
                userOrderMap.containsKey(it.id)
            }.flatMap { userInfo ->
                val regions = userOrderMap[userInfo.id]!!
                regions.filter { allTicket.containsKey(it) }.map { regionName ->
                    val region = allTicket[regionName]!!
                    val formData = FormData(
                        true, matchId, listOf(
                            OrderRegion(
                                region.region,
                                region.estate,
                                region.num,
                                region.name,
                                region.price,
                                region.usable_count.toInt(),
                            )
                        ), listOf(
                            OrderUser(
                                userInfo.id,
                                userInfo.uid,
                                userInfo.realname,
                                userInfo.real_card_id,
                                userInfo.phone,
                                userInfo.is_self,
                                userInfo.real_card_id2,
                                userInfo.phone2,
                                userInfo.timestamp,
                                userInfo.signature,
                                false,
                                false,
                                "${userInfo.realname} ${userInfo.real_card_id}"
                            )
                        )
                    )

                    val orderPayloadRoot = OrderPayloadRoot.convertFormData2Object(
                        EncryptUtils.EncryptionParams(
                            user.encryptKey, user.iv, user.version, user.expireTime
                        ), formData
                    )
                    autoTaskManager.createOrder(
                        "${user.userId}|$matchId|${region.name}", user.loginCode, user.token, orderPayloadRoot
                    )

                }
            }
        }
    }

    fun findValidUserInfo(): List<UserInfoEntity> {
        return userInfoRepository.findAll().filter { !it.expire }
    }
}