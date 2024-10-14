package com.wangrui.ticketsystem.ticket.application.port.input

interface TokenUseCase {

    fun findByToken(token: String): TokenInfo

    fun createToken(tokenInfo: TokenInfo)

    fun invalidateToken(token: String)


}

data class TokenInfo(val token: String, val desc: String, val valid: Boolean)