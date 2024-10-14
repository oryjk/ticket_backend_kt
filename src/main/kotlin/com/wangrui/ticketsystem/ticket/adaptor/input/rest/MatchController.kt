package com.wangrui.ticketsystem.ticket.adaptor.input.rest

import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import org.springframework.web.bind.annotation.CrossOrigin
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * Created on 2024/2/29 11:59.
 * @author wangrui
 * @since 0.0.1
 */

@RestController
@RequestMapping("/api/schedule")
@CrossOrigin
class MatchController(val matchUseCase: MatchUseCase) {

    /**
     * 获取所有比赛的清单，数据来自于爬虫或者手动录入
     */
    @GetMapping("/list")
    fun getAllMatch(): List<MatchUseCase.Match> {
        return matchUseCase.queryAll()
    }

    @GetMapping("/current")
    fun getCurrentMatch(): MatchUseCase.Match {
        return matchUseCase.queryLatest()
    }
}