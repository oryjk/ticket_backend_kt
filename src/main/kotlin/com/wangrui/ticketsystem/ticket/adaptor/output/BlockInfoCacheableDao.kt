package com.wangrui.ticketsystem.ticket.adaptor.output

import com.fasterxml.jackson.databind.ObjectMapper
import com.wangrui.ticketsystem.ticket.application.port.input.GeneralTicketUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.BlockInfoDao
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import java.time.LocalDateTime

@Service
class BlockInfoCacheableDao(
    val blockInfoRepository: BlockInfoRepository,
    val redisTemplate: RedisTemplate<String, Any>,
    val objectMapper: ObjectMapper
) : BlockInfoDao {
    private val blockInfoKey = "blockInfoKey"
    override fun saveOne(blockInfoEntity: BlockInfoDao.BlockInfoEntity) {
        blockInfoRepository.save(blockInfoEntity)
        redisTemplate.delete(blockInfoKey)
    }

    override fun findByRoundId(round: Long): List<GeneralTicketUseCase.BlockInfoHistory> {
        return if (redisTemplate.hasKey(blockInfoKey)) {
            redisTemplate.opsForList().range(blockInfoKey, 0, -1)!!.map {
                objectMapper.convertValue(it, GeneralTicketUseCase.BlockInfoHistory::class.java)
            }
        } else {
            val blockInfoHistories = blockInfoRepository.findByRoundId(round).sortedBy { it.blockName }
            if (blockInfoHistories.isEmpty()) {
                redisTemplate.opsForList().rightPush(blockInfoKey, GeneralTicketUseCase.BlockInfoHistory(
                    "101",
                    0,
                    LocalDateTime.parse("2020-02-01T00:00:00")
                ))
            } else {
                redisTemplate.opsForList().rightPushAll(blockInfoKey, blockInfoHistories)
            }

            blockInfoHistories
        }
    }

}