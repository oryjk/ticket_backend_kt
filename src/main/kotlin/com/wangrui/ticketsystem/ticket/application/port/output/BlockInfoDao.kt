package com.wangrui.ticketsystem.ticket.application.port.output

import com.wangrui.ticketsystem.ticket.application.port.input.GeneralTicketUseCase
import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Created on 2024/3/1 14:33.
 * @author wangrui
 * @since 0.0.1
 */

interface BlockInfoDao {

    @Entity
    @Table(name = "rs_block_info")
    data class BlockInfoEntity(
        @Id @GeneratedValue(strategy = GenerationType.IDENTITY) val id: Long? = null,
        val scheduleId: Long,
        val blockName: String,
        val inventory: Int,
        val price: Float,
        val time: LocalDateTime
    ) {
        companion object {
            fun toBlockInfoEntity(it: GeneralTicketUseCase.BlockInfo): BlockInfoEntity {
                return BlockInfoEntity(
                    scheduleId = it.scheduleId,
                    blockName = it.blockName,
                    inventory = it.inventory,
                    price = it.price,
                    time = LocalDateTime.now()
                )
            }
        }
    }

    fun saveOne(blockInfoEntity: BlockInfoEntity)
    fun findByRoundId(round: Long): List<GeneralTicketUseCase.BlockInfoHistory>
}