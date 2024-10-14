package com.wangrui.ticketsystem.ticket.application.port.input

import java.time.LocalDate

/**
 * Created on 2024/2/29 12:04.
 * @author wangrui
 * @since 0.0.1
 */

interface MatchUseCase {
    data class Match(
        val date: LocalDate,
        val hour: Int,
        val minute: Int,
        val homeName: String,
        val awayName: String,
        val round: Int,
        val matchId:String
    )

    fun queryAll(): List<Match>

    fun queryLatest(): Match
}