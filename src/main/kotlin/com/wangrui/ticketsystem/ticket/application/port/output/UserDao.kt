package com.wangrui.ticketsystem.ticket.application.port.output

interface UserDao {

    fun findByURLId(userId: MiniUserId): MiniUserInfo
    fun findAllInfo(): List<MiniUserInfo>

    companion object {
        data class MiniUserInfo(
            val userId: MiniUserId,
            val token: String,
            val version:Int,
            val nickName: String,
            val referer: String
        )

        val autoUserInfoKey="autoUserInfo"

    }
}

typealias MiniUserId = String