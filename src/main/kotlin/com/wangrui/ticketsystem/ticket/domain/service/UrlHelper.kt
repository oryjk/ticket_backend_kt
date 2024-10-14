package com.wangrui.ticketsystem.ticket.domain.service

import org.springframework.stereotype.Service

@Service
class UrlHelper {
    fun getBaseUrl(index: Int): BaseUrl {
        return BaseUrl("https://fccdn1.k4n.cc/fc/wx_api/v1/", "https://fccdn1.k4n.cc")
    }


    fun getOrderBaseUrl(index: Int): BaseUrl {
        return BaseUrl("https://fccdn5.k4n.cc/fc/wx_api/v1/", "https://fccdn5.k4n.cc")
    }

    companion object {
        data class BaseUrl(val url: String, val host: String)
    }
}