package com.wangrui.ticketsystem.ticket.config

import com.wangrui.ticketsystem.extensions.slf4k
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType

/**
 * Created on 2024/4/8 13:35.
 * @author wangrui
 * @since 0.0.1
 */

object GlobalConfig {
    private val logger = slf4k()
    private const val useUrlLid2 = "159452"
    const val open = "Open/"
    const val miniApp = "MiniApp/"
    const val matchCon = "MatchCon/"
    const val matchOrder = "MatchOrder/"

    val filterBlock = listOf("VIP1", "VIP2", "VIP3")


    fun buildHeader(token:String): HttpHeaders {
        val authorization = "Bearer $token"
        val httpHeaders = HttpHeaders()
        httpHeaders.contentType = MediaType.APPLICATION_JSON
        httpHeaders["User-Agent"] =
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.3"
        httpHeaders.add("Authorization", authorization)
        httpHeaders.add("Referer", "https://servicewechat.com/wxffa42ecd6c0e693d/65/page-frame.html")
        httpHeaders.add("Host", "fccdn5.k4n.cc")
        httpHeaders.add("Accept-Charset", "utf-8")
        return httpHeaders
    }

    fun getUrl(api: Api, baseUrl: String, userId: String): String {

        return urlMapping[api]!!.replace("%baseUrl%", baseUrl).replace("%userId%", userId)
    }

    val urlMapping = mapOf(
        Api.GetBillRegion to "%baseUrl%" + matchCon + "getBillRegion?lid2=%userId%",
        Api.GetMatchInfo to "%baseUrl%" + miniApp + "getMatchInfo?lid2=%userId%",
        Api.MatchList to "%baseUrl%" + open + "matchList?lid2=%userId%",
        Api.CreateMatchOrder to "%baseUrl%" + matchOrder + "createMatchOrder?lid2=%userId%",

        )

    enum class Api {
        GetBillRegion, GetMatchInfo, MatchList, CreateMatchOrder
    }
}