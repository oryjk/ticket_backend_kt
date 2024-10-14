package com.wangrui.ticketsystem.ticket.domain.service

import com.wangrui.ticketsystem.ticket.adaptor.output.ClientTokenEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.TokenRepository
import com.wangrui.ticketsystem.ticket.application.port.input.TokenInfo
import com.wangrui.ticketsystem.ticket.application.port.input.TokenUseCase
import org.springframework.stereotype.Service

@Service
class TokenService(val tokenRepository: TokenRepository) : TokenUseCase {

    private val tokenInfo = TokenInfo("xxx","xxx",false)
    override fun findByToken(token: String): TokenInfo {
        return tokenRepository.findById(token).map { it.toTokenInfo() }.orElseGet { tokenInfo }
    }

    override fun createToken(tokenInfo: TokenInfo) {
        tokenRepository.save(ClientTokenEntity.toTokenEntity(tokenInfo))
    }

    override fun invalidateToken(token: String) {
        tokenRepository.findById(token).let {
            it.ifPresent {
                tokenRepository.save(ClientTokenEntity(it.token, it.description, false))
            }
        }
    }
}