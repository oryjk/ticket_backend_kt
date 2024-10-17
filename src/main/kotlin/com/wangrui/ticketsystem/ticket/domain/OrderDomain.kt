package com.wangrui.ticketsystem.ticket.domain

import org.apache.logging.log4j.util.StringMap


data class CreateOrderParam(
    val queryParam: CreateOrderQueryParam,
    val requestBody: CreateMatchOderRequest
)

data class CreateOrderQueryParam(val userId: String, val timeStamp: Long, val sign: String,val token:String)
data class CreateMatchOderRequest(
    val encryptedData: String,
    val version: Int,//encryptedData变化的时候，version也会变化，+1操作
    val expireTime: Long,//这个应该是encryptedData的过期时间，观测是1个小时过期
    val agree: Boolean,
    val id: String,//这个是比赛的ID
    val regions: List<Region>,
    val users: List<User>


)

data class Region(
    val region: Int,
    val estate: Int,
    val num: Int,
    val name: String,
    val price: String,
    val usable_count: String
)


data class User(
    val id: Int,
    val uid: Int,
    val realname: String,
    val real_card_id: String,
    val phone: String,
    val is_self: Boolean,
    val real_card_id2: String,
    val phone2: String,
    val timestamp: Long,
    val signature: String,
    val disabled: Boolean?,
    val disabled2: Boolean?,
    val showText: String?
) {
    companion object {
        fun defaultUser(
            id: Int,
            uid: Int,
            timeout: Long,
            signature: String,
            realName: String,
            realCardId2: String,
            phone2: String,
            realCardId: String,
            phone: String,
            showText: String
        ): User {
            return User(
                id,
                uid,
                realName,
                realCardId,
                phone,
                id == uid,
                realCardId2,
                phone2,
                timeout,
                signature,
                false,
                false,
                showText
            )
        }
    }

}

data class CreateMatchOrderUrl(val lid2: String, val sTime: String, val sSign: String) {
    //https://fccdn5.k4n.cc/fc/wx_api/v1/MatchCon/createMatchOrder?lid2=432555&s_time=1716643896&s_sign=84736a6c9d451485e6e5e338d938b2e1
    private val baseUrl =
        "https://fccdn5.k4n.cc/fc/wx_api/v1/MatchCon/createMatchOrder?lid2=%lid2%&s_time=%s_time%&s_sign=%s_sign%"

    override fun toString(): String {
        return baseUrl.replace("%lid2%", lid2).replace("%s_time%", sTime).replace("%s_sign%", sSign)
    }

    fun host(): String {
        return "fccdn5.k4n.cc"
    }
}

data class CreateMatchOderRequestContainer(val body: CreateMatchOderRequest, val urlParam: CreateMatchOrderUrl)


data class CreateMatchOrderResponse(
    val code: Int,
    val msg: String,
    val timeout: Long,
    val data: Object?
)


