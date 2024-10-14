package com.wangrui.ticketsystem.ticket.application.port.output

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Created on 2024/2/29 12:11.
 * @author wangrui
 * @since 0.0.1
 */

interface MatchReader {

    @Entity
    @Table(name = "rs_match")
    data class MatchDO(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        val id: Long? = null,
        val date: LocalDateTime,
        val homeName: String,
        val awayName: String,
        val round: Int,
        val matchId: String,
        val isCurrent: Boolean
    )

    fun queryAll(): List<MatchDO>

    fun findByIsCurrentTrue():MatchDO?
}