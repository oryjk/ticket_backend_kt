package com.wangrui.ticketsystem.ticket.adaptor.input.endpoint

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wangrui.ticketsystem.ticket.adaptor.output.UserInfoRepository
import com.wangrui.ticketsystem.ticket.application.port.input.EncryptUtils
import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.TicketDao
import com.wangrui.ticketsystem.ticket.domain.*
import com.wangrui.ticketsystem.ticket.domain.service.AutoTaskManager
import jakarta.annotation.PostConstruct
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.util.StringUtils

@Service
class AutoTaskEndpoint(val autoTaskManager: AutoTaskManager,
                       val ticketDao: TicketDao,
                       val userInfoRepository: UserInfoRepository,
                       val objectMapper: ObjectMapper,
                       val matchUseCase: MatchUseCase) {

    @Scheduled(cron = "0 0 14 * * ?")
    @PostConstruct
    fun autoTask() {
        autoTaskManager.loopOrderRequest()
//        autoTaskManager.watchLatestTicketInfo()
    }

    @Scheduled(cron = "*/1 * * * * *")
    fun createOrder() {
        val matchId = matchUseCase.queryLatest().matchId
        val allTicket = ticketDao.queryAllTicket()
        userInfoRepository.findAll().filter { user -> !StringUtils.isEmpty(user.users) }.forEach { user ->
            var regions = user.regions.split(",")
            if (regions.size == 0 || StringUtils.isEmpty(user.regions)) {
                val region1 = (503..535).map { it.toString() }
                val region2 = (101..108).map { it.toString() }
                regions = region1 + region2 + listOf("124")
            }
            regions.filter { allTicket.containsKey(it) }.forEach { regionName ->
                val region = allTicket[regionName]!!
                user.users.split(",").filter { userId -> !StringUtils.isEmpty(userId) }.forEach { userId ->
                    val userInfos = objectMapper.readValue<List<UserInfo>>(user.members).associateBy { it.id }
                    val userInfo = userInfos[userId.toInt()]!!
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
                        EncryptUtils.EncryptionParams(user.encryptKey, user.iv, user.version, user.expireTime), formData
                    )
                    autoTaskManager.createOrder(
                        "${user.userId}|$matchId|${region.name}", user.loginCode, user.token, orderPayloadRoot
                    )
                }


            }


        }

    }
}