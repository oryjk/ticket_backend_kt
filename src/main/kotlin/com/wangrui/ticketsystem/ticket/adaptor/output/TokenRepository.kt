package com.wangrui.ticketsystem.ticket.adaptor.output

import com.wangrui.ticketsystem.ticket.application.port.input.TokenInfo
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface TokenRepository : JpaRepository<ClientTokenEntity, String> {}

@Entity
@Table(name = "rs_client_token")
data class ClientTokenEntity(
    @Id val token: String, val description: String, val valid: Boolean = true
) {

    fun toTokenInfo(): TokenInfo {
        return TokenInfo(this.token, this.description, this.valid)
    }

    companion object {
        fun toTokenEntity(tokenInfo: TokenInfo): ClientTokenEntity {
            return ClientTokenEntity(tokenInfo.token, tokenInfo.desc, tokenInfo.valid)
        }
    }

}