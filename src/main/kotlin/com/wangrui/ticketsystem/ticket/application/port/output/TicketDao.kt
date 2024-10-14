package com.wangrui.ticketsystem.ticket.application.port.output

import com.wangrui.ticketsystem.ticket.domain.Region

interface TicketDao {
    fun queryAllTicket(): Map<String, Region>

}