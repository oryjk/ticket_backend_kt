package com.wangrui.ticketsystem.ticket.application.port.output

import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.application.port.input.OrderTaskUseCase

/**
 * 自动下单的信息，返回的是需要自动下单的用户和他的选区
 */
interface OrderRequestDao {
    fun getAutoBuyInfo(): List<OrderTaskUseCase.OrderRequest>
    fun save(orderRequestEntity: OrderRequestEntity)

    fun findByToken(token: String): List<OrderRequestEntity>
}