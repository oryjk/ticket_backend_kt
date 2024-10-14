package com.wangrui.ticketsystem.ticket.domain.service

import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.MatchReader
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

/**
 * Created on 2024/2/29 12:06.
 * @author wangrui
 * @since 0.0.1
 */

@Service
class MatchService(val matchReader: MatchReader) : MatchUseCase {
    private val logger = LoggerFactory.getLogger(javaClass)
    override fun queryAll(): List<MatchUseCase.Match> {
        val scheduleDOList = matchReader.queryAll()
        logger.info("scheduleDOList size ${scheduleDOList.size}")

        return scheduleDOList.map { scheduleDO ->
            val localDate = scheduleDO.date.toLocalDate()
            val localTime = scheduleDO.date.toLocalTime()
            MatchUseCase.Match(
                localDate,
                localTime.hour,
                localTime.minute,
                scheduleDO.homeName,
                scheduleDO.awayName,
                scheduleDO.round,
                scheduleDO.matchId
            )
        }
    }

    override fun queryLatest(): MatchUseCase.Match {
        return matchReader.findByIsCurrentTrue()?.let {
            val localDate = it.date.toLocalDate()
            val localTime = it.date.toLocalTime()
            MatchUseCase.Match(
                localDate, localTime.hour, localTime.minute, it.homeName, it.awayName, it.round, it.matchId
            )
        } ?: run { throw IllegalArgumentException("无法找到当前正在进行的比赛") }

    }
}