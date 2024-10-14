package com.wangrui.ticketsystem.ticket.domain

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.wangrui.ticketsystem.ticket.application.port.input.EncryptUtils

/**
 * Created on 2024/9/11 16:29.
 * @author wangrui
 * @since 0.0.1
 */

data class OrderPayloadRoot(
    val encryptedData: String,//不参与加密
    val version: Int,//不参与加密
    val expireTime: Long,//你参与加密
    val agree: Boolean,
    val id: String,
    val regions: List<OrderRegion>,
    val users: List<OrderUser>
) {
    companion object {
        private val mapper = initMapper()

        private fun initMapper(): ObjectMapper {
            val mapper = ObjectMapper()
            mapper.registerKotlinModule()
            return mapper
        }

        fun convertJson2Object(json: String): OrderPayloadRoot {
            val root = mapper.readValue<OrderPayloadRoot>(json)
            return root
        }

        fun convertObject2Json(orderPayloadRoot: OrderPayloadRoot): String {
            val root = mapper.writeValueAsString(orderPayloadRoot)
            return root
        }

        fun convertFormData2Object(
            encryptionParams: EncryptUtils.EncryptionParams,
            formData: FormData
        ): OrderPayloadRoot {
            val encrypt = EncryptUtils.encrypt(encryptionParams, mapper.writeValueAsString(formData))
            return OrderPayloadRoot(
                encrypt,
                encryptionParams.version,
                encryptionParams.expireTime,
                formData.agree,
                formData.id,
                formData.regions,
                formData.users
            )
        }
    }
}

data class FormData(
    val agree: Boolean,
    val id: String,
    val regions: List<OrderRegion>,
    val users: List<OrderUser>
)

data class OrderRegion(
    val region: Int, val estate: Int, val num: Int, val name: String, val price: String, val usable_count: Int
)

data class OrderUser(
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
    val disabled: Boolean,
    val disabled2: Boolean,
    val showText: String
)


