package com.wangrui.ticketsystem.ticket.adaptor.output

import com.wangrui.ticketsystem.ticket.application.port.output.MatchReader
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created on 2024/2/29 12:24.
 * @author wangrui
 * @since 0.0.1
 */

@Repository
interface MatchRepository : MatchReader, JpaRepository<MatchReader.MatchDO, Long> {

    override fun queryAll(): List<MatchReader.MatchDO> {
        return findAll()
    }

    override fun findByIsCurrentTrue(): MatchReader.MatchDO?
}