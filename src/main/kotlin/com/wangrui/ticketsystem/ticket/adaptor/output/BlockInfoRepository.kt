package com.wangrui.ticketsystem.ticket.adaptor.output

import com.wangrui.ticketsystem.ticket.application.port.input.GeneralTicketUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.BlockInfoDao
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

/**
 * Created on 2024/3/1 14:35.
 * @author wangrui
 * @since 0.0.1
 */

@Repository
interface BlockInfoRepository : JpaRepository<BlockInfoDao.BlockInfoEntity, Long> {

    fun findByRoundId(scheduleId: Long): List<GeneralTicketUseCase.BlockInfoHistory> {
        return findByScheduleId(scheduleId)
            .groupBy { it.blockName }
            .map {
                val blockName = it.key
                val sumOfInventory = it.value.sumOf { it.inventory }
                val latestTime = it.value.maxOf { it.time }
                GeneralTicketUseCase.BlockInfoHistory(blockName, sumOfInventory, latestTime)
            }
    }

    fun findByScheduleId(scheduleId: Long): List<BlockInfoDao.BlockInfoEntity>


}