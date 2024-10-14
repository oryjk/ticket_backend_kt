package com.wangrui.ticketsystem.ticket.adaptor.input.rest

import com.wangrui.ticketsystem.ticket.application.port.input.TokenInfo
import com.wangrui.ticketsystem.ticket.application.port.input.TokenUseCase
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/token")
@CrossOrigin
class TokenController(val tokenUseCase: TokenUseCase) {
    @PostMapping("/createClientToken")
    fun createTokens(@RequestBody tokenInfo: TokenInfo): TokenInfo {
        tokenUseCase.createToken(tokenInfo)
        return tokenInfo
    }

    @GetMapping("/check/{token}")
    fun checkToken(@PathVariable("token") token: String): TokenInfo {
        val tokenInfo = tokenUseCase.findByToken(token)
        return tokenInfo
    }
}