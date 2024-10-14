package com.wangrui.ticketsystem.ticket.adaptor.input.rest

import com.wangrui.ticketsystem.ticket.domain.service.RequestParamService
import com.wangrui.ticketsystem.ticket.domain.TicketInfo
import com.wangrui.ticketsystem.ticket.application.port.input.GeneralTicketUseCase
import com.wangrui.ticketsystem.ticket.config.BaseUrlConfig
import com.wangrui.ticketsystem.ticket.config.GlobalConfig
import com.wangrui.ticketsystem.ticket.config.TicketConfig
import org.springframework.web.bind.annotation.*

/**
 * Created on 2024/2/28 23:27.
 * @author wangrui
 * @since 0.0.1
 *
 * 日常比赛获取比赛信息接口
 */

@RestController
@RequestMapping("/api/general-ticket")
@CrossOrigin
class GeneralTicketController(
    val generalTicketUseCase: GeneralTicketUseCase, val requestParamService: RequestParamService
) {

    /**
     * 获取当前比赛的座位信息
     */
    @GetMapping("/latestTicketInfo/{scheduleId}")
    fun  latestTicketInfo(
        @PathVariable("scheduleId") scheduleId: Long
    ): HttpResp {
        val requestParam = requestParamService.getRobinUser()

        return HttpResp(
            generalTicketUseCase.getLatestTicketInfo(
                GlobalConfig.getUrl(
                    GlobalConfig.Api.GetBillRegion, BaseUrlConfig.getBaseUrl(), requestParam.useUrlLid2
                ),
                scheduleId, requestParam,
            )
        )
    }

    /**
     * 获取所有的座位信息，包括价格，区位
     */
    @GetMapping("/ticketInfo")
    fun ticketInfo(): List<TicketInfo> {
        return TicketConfig.ticketInfos.toList()
    }


    /**
     * 获取当前比赛的退票信息
     */
    @GetMapping("/blockInfo/{scheduleId}")
    fun blockInfo(@PathVariable("scheduleId") scheduleId: Long): List<GeneralTicketUseCase.BlockInfoHistory> {
        return generalTicketUseCase.getBlockInfo(scheduleId).toList()
    }

    data class HttpResp(val blockInfos: List<GeneralTicketUseCase.BlockInfo>)


}