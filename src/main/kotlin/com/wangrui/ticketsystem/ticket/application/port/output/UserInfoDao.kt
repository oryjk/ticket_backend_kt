package com.wangrui.ticketsystem.ticket.application.port.output

import com.wangrui.ticketsystem.ticket.domain.User

interface UserInfoDao {


    fun queryById(userId: String): User


}