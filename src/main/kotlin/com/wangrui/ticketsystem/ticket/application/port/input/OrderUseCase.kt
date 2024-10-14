package com.wangrui.ticketsystem.ticket.application.port.input

import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.domain.CreateMatchOrderResponse
import com.wangrui.ticketsystem.ticket.domain.CreateOrderParam
import java.time.LocalDateTime

interface OrderUseCase {

    fun sendOrder(requestParam: CreateOrderParam): CreateMatchOrderResponse
    fun getAutoBuyInfo(): List<OrderTaskUseCase.OrderRequest>
    fun save(orderRequestEntity: OrderRequestEntity)
    fun findByToken(token: String): List<OrderRequestEntity>

    data class MatchInfo(
        val id: Int,
        val team1Name: String,
        val team1Logo: String,
        val team1Color: String,
        val team2Name: String,
        val team2Logo: String,
        val team2Color: String,
        val timeS: Long,
        val addressName: String,
        val title: String,
        val address: String,
        val addressLat: Double,
        val addressLng: Double,
        val maxNum: Int,
        val imgs: Map<String, String>,
        val refundETime: Long,
        val refund: Boolean,
        val refundRatio: Int,
        val bgimg: String,
        val selectType: Int,
        val inSTime: Long,
        val shareImg: Any?,
        val sDate: String
    )

    data class Order(
        val id: Int,
        val matchId: Int,
        val countBill: Int,
        val orderId: String,
        val payable: String,
        val status: Int,
        val createTime: LocalDateTime,
        val matchInfo: MatchInfo,
        val showInfo: Boolean
    )

    data class Page(
        val current: Int,
        val currentPage: Int,
        val pageSize: Int,
        val status: String
    )

    /**
     * 我的订单里面代付款订单
     */
    data class OrderListResponse(
        val code: Int,
        val data: List<Order>,
        val page: Page,
        val msg: String
    )

}