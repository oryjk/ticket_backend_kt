package com.wangrui.ticketsystem.ticket.config

import com.wangrui.ticketsystem.extensions.slf4k


class TokenConfig {

    companion object {
        private val logger = slf4k()
        private var tokenIndex = 0
        val tokens = listOf(
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjE1OTQ1Miwib2lkIjoiYmRiZDliZGViNWJkOTIyYWVlODAxYWNhMzU4ZTBmOWIiLCJsaWQiOjAsInNpZGUiOiJ3eF9hcGkiLCJhdWQiOiIiLCJleHAiOjE3MTQxMzk1MzMsImlhdCI6MTcxNDA2NzUzMywiaXNzIjoiIiwianRpIjoiMjAzNDViMmIwMDNmYTZiYWVhNGE4MDY1MjU1MGE2ZGMiLCJuYmYiOjE3MTQwNjc1MzMsInN1YiI6IiJ9.sGyaEEapRftlhD2O6-dGpi9kK8fiyILBZSCWc-HHdGw",
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjE1OTQ1Miwib2lkIjoiYmRiZDliZGViNWJkOTIyYWVlODAxYWNhMzU4ZTBmOWIiLCJsaWQiOjAsInNpZGUiOiJ3eF9hcGkiLCJhdWQiOiIiLCJleHAiOjE3MTQxMzk1MzMsImlhdCI6MTcxNDA2NzUzMywiaXNzIjoiIiwianRpIjoiMjAzNDViMmIwMDNmYTZiYWVhNGE4MDY1MjU1MGE2ZGMiLCJuYmYiOjE3MTQwNjc1MzMsInN1YiI6IiJ9.sGyaEEapRftlhD2O6-dGpi9kK8fiyILBZSCWc-HHdGw",
            "eyJ0eXAiOiJKV1QiLCJhbGciOiJIUzI1NiJ9.eyJ1aWQiOjE1OTQ1Miwib2lkIjoiYmRiZDliZGViNWJkOTIyYWVlODAxYWNhMzU4ZTBmOWIiLCJsaWQiOjAsInNpZGUiOiJ3eF9hcGkiLCJhdWQiOiIiLCJleHAiOjE3MTQxMzk1MzMsImlhdCI6MTcxNDA2NzUzMywiaXNzIjoiIiwianRpIjoiMjAzNDViMmIwMDNmYTZiYWVhNGE4MDY1MjU1MGE2ZGMiLCJuYmYiOjE3MTQwNjc1MzMsInN1YiI6IiJ9.sGyaEEapRftlhD2O6-dGpi9kK8fiyILBZSCWc-HHdGw"
        )

        fun getToken(): String {
            logger.info("tokenIndex $tokenIndex")
            val tokenIdx: Int = when (tokenIndex) {
                0 -> {
                    tokenIndex += 1
                    tokenIndex
                }

                1 -> {
                    tokenIndex += 1
                    tokenIndex
                }

                2 -> {
                    tokenIndex = 0
                    tokenIndex
                }

                else ->
                    throw IllegalArgumentException()
            }
            return tokens[tokenIdx]
        }
    }
}