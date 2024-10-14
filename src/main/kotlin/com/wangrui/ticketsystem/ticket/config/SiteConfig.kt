package com.wangrui.ticketsystem.ticket.config

import com.wangrui.ticketsystem.ticket.domain.TicketInfo
import com.wangrui.ticketsystem.ticket.domain.TicketLevel
import com.wangrui.ticketsystem.ticket.domain.Tower

/**
 * Created on 2024/4/8 13:04.
 * @author wangrui
 * @since 0.0.1
 */
//    FIRST("首层看台"),
//    SECOND("顶层看台")
object TicketConfig {
    val ticketInfos = listOf(
        TicketInfo(TicketLevel("VIP", 1288.00), listOf("VIP1", "VIP2", "VIP3"), Tower("FIRST", "首层看台")),
        TicketInfo(TicketLevel("S", 400.00), (127..129).map { it.toString() }.toList(), Tower("FIRST", "首层看台")),
        TicketInfo(
            TicketLevel("A", 220.00),
            listOf("109", "110", "111", "112", "113", "125", "126", "130", "131"),
            Tower("FIRST", "首层看台")
        ),
        TicketInfo(
            TicketLevel("B", 180.00),
            (101..108).map { it.toString() }.toList() + listOf("513", "514", "515", "531", "532","124"),
            Tower("FIRST", "首层看台")
        ),
        TicketInfo(
            TicketLevel("C", 150.00),
            (516..517).map { it.toString() }.toList()
                    + (511..512).map { it.toString() }.toList()
                    + (534..535).map { it.toString() }.toList()
                    + (529..530).map { it.toString() }.toList(),
            Tower("FIRST", "顶层看台")
        ),
        TicketInfo(
            TicketLevel("D", 120.00),
            listOf("508", "510", "518", "520", "526", "528", "118"),
            Tower("SECOND", "顶层看台")
        ),
        TicketInfo(
            TicketLevel("E", 100.00), (503..507).map { it.toString() }.toList()
                    + (521..525).map { it.toString() }.toList(),
            Tower(
                "SECOND", "顶层看台" +
                        ""
            )
        ),
    )
}



