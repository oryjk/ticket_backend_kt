package com.wangrui.ticketsystem.ticket.domain.service

import com.wangrui.ticketsystem.extensions.slf4k
import com.wangrui.ticketsystem.ticket.application.port.input.OrderTaskUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.TicketDao
import com.wangrui.ticketsystem.ticket.domain.CreateMatchOrderResponse
import com.wangrui.ticketsystem.ticket.domain.Region
import com.wangrui.ticketsystem.utils.EmailSender
import kotlinx.coroutines.*
import org.springframework.context.annotation.Conditional
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import java.util.regex.Pattern

@Service
@Conditional(OrderTaskUseCaseMockCondition::class)
class OrderTaskManagerMock(ticketDao: TicketDao) : OrderTaskUseCase {
    private val logger = slf4k()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val allRegion = ticketDao.queryAllTicket()
    private val regex = "\\d+"
    private val pattern = Pattern.compile(regex)
    private val statusMsgPrefix = mapOf<Int, String>(
        0 to "~~~~~~", 1 to "√√√√√√", 2 to "√√++++", 3 to "⭕⭕⭕⭕", 5 to "××××××"
    )

    override fun createOrderJob(jobId: String, orderRequest: OrderTaskUseCase.OrderRequest): Job {
        return scope.launch {
            var count = 0//用于计数通知的，假如=10，那么就会在控制台通知一下，确保任务是活着的
            while (true) {
                try {
                    count++
                    val result = mockOrder(orderRequest)
                    val delayTime = logInConsole(orderRequest, result, count)
                    if (count == 10) {
                        count = 0
                    }
                    delay(delayTime)
                } catch (e: Exception) {
                    logger.error(e.message)
                    delay(10000)
                }

            }
        }
    }

    private fun mockOrder(orderRequest: OrderTaskUseCase.OrderRequest): CreateMatchOrderResponse {
        return CreateMatchOrderResponse(5, "mock一下下", 5, null)
    }

    private fun logInConsole(orderRequest: OrderTaskUseCase.OrderRequest,
                             result: CreateMatchOrderResponse,
                             count: Int): Long {
        var regions = regions(orderRequest)
        val nameStr = orderRequest.users.map { it.realName }.reduce { acc, i -> "$acc , $i" }
        val regionStr = regions.map { it.name }.reduce { acc, i -> "$acc , $i" }
        val regionPriceStr = regions.map { it.price }.reduce { acc, i -> "$acc , $i" }


        when (result.code) {
            -1 -> {
                logger.error("${orderRequest.users[0].realName}|${orderRequest.regions[0]}|登录信息失效 ${result.msg}")
                EmailSender.sendEmail(
                    listOf(
//                        "331675560@qq.com",
                        "oryjk@qq.com"
                    ),
                    "成都蓉城抢票失败token过期 ${orderRequest.users[0].realName}|${orderRequest.regions[0]}",
                    "名字 $nameStr, 区域 $regionStr，当前时间 ${LocalDateTime.now()}，${orderRequest.users[0].realName}|${orderRequest.regions[0]} 过期"
                )
                return 300
            }

            0 -> {
                if (count == 10) {
                    logger.info("${statusMsgPrefix[0]} ${orderRequest.orderId}: 【$nameStr】当前抢的 【$regionStr】 区，价格 【$regionPriceStr】，状态码是${result.code}，${result.msg}")
                }
                val matcher = pattern.matcher(result.msg)
                if (matcher.find()) {
                    val number = matcher.group()
                    return number.toLong() * 1000L
                } else {
                    throw IllegalArgumentException("状态是5，但是没有超时时间，很奇怪哦")
                }

            }

            1 -> {
                logger.info("${statusMsgPrefix[1]} ${orderRequest.orderId}: 【$nameStr】当前抢的 【$regionStr】 区，价格 【$regionPriceStr】，状态码是${result.code}，${result.msg} √√√√√ √√√√√ √√√√√ √√√√√ √√√√√ √√√√√ √√√√√")
                logger.info("~~~~~~~~~~~~~~~恭喜，恭喜~~~~~发送瞄提醒~~~~~~~~~~")

                try {
                    logger.info("假装发送了一个喵提醒")
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                EmailSender.sendEmail(
                    listOf(
                        "oryjk@qq.com"
                    ), "成都蓉城抢票成功", "名字 $nameStr, 区域 $regionStr，当前时间 ${LocalDateTime.now()}，过期时间 ${
                        LocalDateTime.now().plusMinutes(15)
                    }"
                )
                return 1000L

            }

            2 -> {
                logger.info("${statusMsgPrefix[2]} ${orderRequest.orderId}: 【$nameStr】当前抢的 【$regionStr】 区，价格 【$regionPriceStr】，状态码是${result.code}，${result.msg}√√√√√ √√√√√ √√√√√ √√√√√ 已经抢到，有待支付订单")
                return 1000L
            }

            3 -> {
                logger.info("${statusMsgPrefix[3]} ${orderRequest.orderId}: 【$nameStr】当前抢的 【$regionStr】 区，价格 【$regionPriceStr】，状态码是${result.code}，${result.msg}")
                return 1000L
            }

            5 -> {
                if (count == 10) {
                    logger.info("${statusMsgPrefix[5]} ${orderRequest.orderId}: 【$nameStr】当前抢的 【$regionStr】 区，价格 【$regionPriceStr】，状态码是${result.code}，${result.msg} 休眠 ${result.timeout} 秒")
                }
                return result.timeout * 1000L
            }

            else -> {
                throw IllegalArgumentException("${orderRequest.orderId}: 状态是${result.code}，没有遇到过这个code")
            }
        }
    }

    private fun regions(orderRequest: OrderTaskUseCase.OrderRequest): List<Region> {
        var regions = orderRequest.regions.map { allRegion[it]!! }
        if (regions.size != orderRequest.users.size) {
            val region = regions[0]
            regions = listOf(
                Region(
                    region.region,
                    region.estate,
                    orderRequest.users.size,
                    region.name,
                    region.price,
                    region.usable_count
                )
            )
        }
        return regions
    }

}