package com.wangrui.ticketsystem.ticket.adaptor.output

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.wangrui.ticketsystem.ticket.adaptor.input.rest.OrderController
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserInfoRepository : JpaRepository<UserInfoEntity, String> {}

@Entity
@Table(name = "ticket_user_info")
data class UserInfoEntity(
    @Id val userId: String,
    val loginCode: String,
    @Column(columnDefinition = "TEXT") val token: String,
    @Column(columnDefinition = "TEXT") val members: String,
    val users: String = "",
    val iv: String = "",
    val encryptKey: String = "",
    val version: Int = 0,
    val expireTime: Long = 0,
    val regions: String = "",
    val expire:Boolean = false,
) {
    companion object {
        private val mapper = initMapper()

        private fun initMapper(): ObjectMapper {
            val mapper = ObjectMapper()
            mapper.registerKotlinModule()
            return mapper
        }

        fun toUserInfoEntity(userInfoRequest: OrderController.UserInfoRequest): UserInfoEntity = UserInfoEntity(
            userInfoRequest.userId,
            userInfoRequest.loginCode,
            userInfoRequest.token,
            mapper.writeValueAsString(userInfoRequest.member)
        )

        fun toUserInfoEntity(userInfoRequest: OrderController.UserInfoRequest,
                             users: String,
                             iv: String,
                             encryptKey: String,
                             version: Int,
                             expireTime: Long,
                             regions: String): UserInfoEntity = UserInfoEntity(
            userInfoRequest.userId,
            userInfoRequest.loginCode,
            userInfoRequest.token,
            mapper.writeValueAsString(userInfoRequest.member),
            users,
            iv,
            encryptKey,
            version,
            expireTime,
            regions
        )
    }
}