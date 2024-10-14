package com.wangrui.ticketsystem.ticket.config

class BaseUrlConfig {

    companion object {
        private val baseUrls = listOf(
            "https://fccdn1.k4n.cc/fc/wx_api/v1/",  "https://fccdn5.k4n.cc/fc/wx_api/v1/"
        )
        private var index = 0

        @Deprecated("存在局限性，换用urlHelper")
        fun getBaseUrl(): String {
            val tokenIdx: Int = when (index) {
                0 -> {
                    index += 1
                    index
                }

                1 -> {
                    index = 0
                    index
                }

                else -> throw IllegalArgumentException()
            }
//            return baseUrls[tokenIdx]
            return "https://fccdn1.k4n.cc/fc/wx_api/v1/"
        }
    }
}