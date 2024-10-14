package com.wangrui.ticketsystem.ticket.domain

/**
 * Created on 2024/4/8 13:07.
 * @author wangrui
 * @since 0.0.1
 */

data class TicketInfo(
    val level: TicketLevel, val sites: List<String>, val tower: Tower
)

data class TicketLevel(val level:String,val price:Double)

data class Tower(val key:String,val value: String)