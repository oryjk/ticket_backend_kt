package com.wangrui.ticketsystem.ticket.adaptor.output

import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.wangrui.ticketsystem.ticket.domain.User
import com.wangrui.ticketsystem.ticket.application.port.output.UserInfoDao
import org.springframework.stereotype.Service

/**
 * Created on 2024/5/7 18:56.
 * @author wangrui
 * @since 0.0.1
 */

@Service
class UserInfoInMemory : UserInfoDao {
    override fun queryById(userId: String): User {
        val user = userMapping[userId]
        if (user != null) return user
        throw IllegalArgumentException("无法找到用户id为 $userId 的用户信息")
    }

    companion object {
        private val users = mapOf(
            Pair(
                "116692", """ {
            "id": 116692,
            "uid": 116692,
            "realname": "王睿",
            "real_card_id": "510***********6011",
            "phone": "186****2970",
            "is_self": true,
            "real_card_id2": "510112198905246011",
            "phone2": "18602812970",
            "timestamp": 1714466998,
            "signature": "3dbb5e89ba006237385e4913df8cab24",
            "disabled": false,
            "disabled2": false,
            "showText": "王睿 510***********6011"
        }"""
            ), Pair(
                "719294", """ {
		"id": 719294,
		"uid": 432555,
		"realname": "王睿",
		"real_card_id": "510***********6011",
		"phone": "186****2970",
		"is_self": false,
		"real_card_id2": "510112198905246011",
		"phone2": "18602812970",
		"timestamp": 1715080165,
		"signature": "10f85baf6a0030dcedc27f140e411d76"
	}"""
            )
        )
        val userMapping = users.map { it.key to jacksonObjectMapper().readValue<User>(it.value) }.toMap()

    }
}