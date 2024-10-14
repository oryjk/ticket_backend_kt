package com.wangrui.ticketsystem.ticket.application.port.input

import java.time.LocalDateTime

/**
 * Created on 2024/2/28 23:35.
 * @author wangrui
 * @since 0.0.1
 */

interface GeneralTicketUseCase {
    data class BlockInfo(val scheduleId: Long, val blockName: String, val inventory: Int, val price: Float)
    data class BlockInfoHistory(val blockName: String, val occurrences: Int, val latestTime: LocalDateTime)

    fun getLatestTicketInfo(url: String, scheduleId: Long, requestParam: RequestUseCase.Companion.RequestParam): List<BlockInfo>
    fun getBlockInfo(scheduleId: Long): List<BlockInfoHistory>
}