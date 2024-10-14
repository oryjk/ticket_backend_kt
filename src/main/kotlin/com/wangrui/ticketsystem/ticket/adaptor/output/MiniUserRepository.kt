package com.wangrui.ticketsystem.ticket.adaptor.output

import com.wangrui.ticketsystem.ticket.application.port.output.MiniUserId
import com.wangrui.ticketsystem.ticket.application.port.output.UserDao
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MiniUserRepository : UserDao, JpaRepository<MiniUserRepository.Companion.MiniUserEntity, MiniUserId> {

    companion object {

        @Entity
        @Table(name = "rs_mini_user")
        data class MiniUserEntity(
            @Id val id: MiniUserId, @Column(columnDefinition = "TEXT")  val token: String, val version: Int, val nickName: String, val referer: String
        )
    }

    override fun findByURLId(userId: MiniUserId): UserDao.Companion.MiniUserInfo {
        return findById(userId).map {
            UserDao.Companion.MiniUserInfo(
                it.id, it.token, it.version, it.nickName, it.referer
            )
        }.orElseThrow { IllegalArgumentException("没有找到id为 $userId 的用户信息") }
    }

    override fun findAllInfo(): List<UserDao.Companion.MiniUserInfo> {
        return findAll().map { UserDao.Companion.MiniUserInfo(it.id, it.token, it.version, it.nickName, it.referer) }
    }


}