package com.wangrui.ticketsystem

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.wangrui.ticketsystem.ticket.application.port.input.EncryptUtils
import com.wangrui.ticketsystem.ticket.domain.FormData
import com.wangrui.ticketsystem.ticket.domain.OrderPayloadRoot
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import kotlin.test.BeforeTest

class ObjectMapperTest {
    private val objectMapper = ObjectMapper()
    val dataString2 = """
            {
    "agree": true,   
    "id": "33",    
    "regions": [    
        {     
            "region": 6,
            "estate": 3,
            "num": 1,
            "name": "503",
            "price": "100.00",
            "usable_count": "1"
        }
    ],
    "users": [
        {
            "id": 116692,
            "uid": 116692,
            "realname": "王睿",
            "real_card_id": "510***********6011",
            "phone": "186****2970",
            "is_self": true,
            "real_card_id2": "510112198905246011",
            "phone2": "18602812970",
            "timestamp": 1727536109,
            "signature": "a9848cd469193f766b90861981356e44",
            "disabled": false,
            "disabled2": false,
            "showText": "王睿 510***********6011"
        }
    ]
}
"""

    val dataString1 =
        "{\"agree\":true,\"id\":\"33\",\"regions\":[{\"region\":6,\"estate\":3,\"num\":1,\"name\":\"503\",\"price\":\"100.00\",\"usable_count\":\"1\"}],\"users\":[{\"id\":116692,\"uid\":116692,\"realname\":\"王睿\",\"real_card_id\":\"510***********6011\",\"phone\":\"186****2970\",\"is_self\":true,\"real_card_id2\":\"510112198905246011\",\"phone2\":\"18602812970\",\"timestamp\":1727536109,\"signature\":\"a9848cd469193f766b90861981356e44\",\"disabled\":false,\"disabled2\":false,\"showText\":\"王睿 510***********6011\"}]}"


    val dataString3 = """
        {
            "agree": true,
            "id": "33",
            "regions": [
                {
                    "region": 6,
                    "estate": 3,
                    "num": 1,
                    "name": "503",
                    "price": "100.00",
                    "usable_count": 608
                }
            ],
            "users": [
                {
                    "id": 159452,
                    "uid": 159452,
                    "realname": "许大海",
                    "real_card_id": "510***********9017",
                    "phone": "181****1106",
                    "is_self": true,
                    "real_card_id2": "510211197206289017",
                    "phone2": "18121871106",
                    "timestamp": 1728996996,
                    "signature": "c3fb89ec410b07fa24cb1bb141649fd6",
                    "disabled": false,
                    "disabled2": false,
                    "showText": "许大海 510***********9017"
                }
            ]
        }
    """.trimIndent()

    val dataString4 = """
        {
            "agree": true,
            "id": "33",
            "regions": [
                {
                    "region": 6,
                    "estate": 3,
                    "num": 1,
                    "name": "503",
                    "price": "100.00",
                    "usable_count": 608
                }
            ],
            "users": [
                {
                    "id": 159452,
                    "uid": 159452,
                    "realname": "许大海",
                    "real_card_id": "cccccc",
                    "phone": "ddddd",
                    "is_self": true,
                    "real_card_id2": "510211197206289017",
                    "phone2": "18121871106",
                    "timestamp": 1728996996,
                    "signature": "c3fb89ec410b07fa24cb1bb141649fd6",
                    "disabled": false,
                    "disabled2": false,
                    "showText": "ffff"
                }
            ]
        }
    """.trimIndent()

    val encryptKey = "eTUDbSJD9gnYtlR8WeJPnw=="
    val expireTime = 1729001404913
    val iv = "86651cb246c57d37"
    val version = 74


    @BeforeTest
    fun init() {
        objectMapper.registerModule(kotlinModule())
    }

    @Test
    fun jsonParseTest() {
        val object1 = objectMapper.readValue<FormData>(dataString1)
        val object2 = objectMapper.readValue<FormData>(dataString2)

        Assertions.assertEquals(object1, object2)
    }


    @Test
    fun objectParseToJson() {
        val object2 = objectMapper.readValue<FormData>(dataString2)
        val json = objectMapper.writeValueAsString(object2)

        val encryptionParams = EncryptUtils.EncryptionParams(encryptKey, iv, version, expireTime)
        val encrypt1 = EncryptUtils.encrypt(encryptionParams, json)
        val encrypt2 = EncryptUtils.encrypt(encryptionParams, dataString2)

        Assertions.assertNotEquals(encrypt1, encrypt2)
    }

    @Test
    fun encryptTest() {
        val encryptionParams = EncryptUtils.EncryptionParams(encryptKey, iv, version, expireTime)
        val object3 = objectMapper.readValue<FormData>(dataString3)
        val object4 = objectMapper.readValue<FormData>(dataString4)
        val json3 = objectMapper.writeValueAsString(object3)
        val json4 = objectMapper.writeValueAsString(object4)
        val encrypt3 = EncryptUtils.encrypt(encryptionParams, json3)
        val encrypt4 = EncryptUtils.encrypt(encryptionParams, json4)

        Assertions.assertEquals(
            "cf27758ab9850d9bdb23809fbb584d774d3abd8ad4c4b42df5d127d4b937f00ad8265c323eff80363d537cf1360e937de5b3625c4373c402dd0286dee5c85031b79dd578e38f06d8e7617abe665c6396f89ae3fc2f6ccb02323132c3224ec4d347e5db2334fa7ccf49fdbbf96c0c945cf2554c7b962efe96f84a5ab03f2e9c5d76e7769e2c95c824e71c7a8c2f66f05ba67646a4160dd7537063873b1bddd5de5ea422de4415b351754d8b65665a800faff51d2c6e9522217aed4877cf551eaffb8e5873b61afbad63d13f2b38fe7561a471f51467c35c8c0e035fed7df71f9a15a22c3808749de92c7c0bd81f2a4b5eff235ff99f4313f4bc51fab1dff83b9298268ece604886ff0cc4e159c19fb5f975e1fd277bc6016b8fc8276e804b62a5df22e4922d179212b065341642bb3844361f8718c0a6fe1a003cf351a743f268c504d283f5e1d0e6501c2df9d2735d0bcf554f6b550fa5b9e3c8f56b92308e2b2d566caaf7343383f64b98e8a1b78aa874bdbe0d1bbcd91ce931129bd67959ea4e42b9b3dcda8fc51c589e522740af700d9f4903c8d49fb7a6b1e9a15617eab509e0e8035a9ec7a89bb15c3e24fdf859096b89d6f81317972ec7b040c6181b0108d32ce7ae4d7d8996eafbdbf7427e8a",
            encrypt3
        )
        Assertions.assertNotEquals(
            "cf27758ab9850d9bdb23809fbb584d774d3abd8ad4c4b42df5d127d4b937f00ad8265c323eff80363d537cf1360e937de5b3625c4373c402dd0286dee5c85031b79dd578e38f06d8e7617abe665c6396f89ae3fc2f6ccb02323132c3224ec4d347e5db2334fa7ccf49fdbbf96c0c945cf2554c7b962efe96f84a5ab03f2e9c5d76e7769e2c95c824e71c7a8c2f66f05ba67646a4160dd7537063873b1bddd5de5ea422de4415b351754d8b65665a800faff51d2c6e9522217aed4877cf551eaffb8e5873b61afbad63d13f2b38fe7561a471f51467c35c8c0e035fed7df71f9a15a22c3808749de92c7c0bd81f2a4b5eff235ff99f4313f4bc51fab1dff83b9298268ece604886ff0cc4e159c19fb5f975e1fd277bc6016b8fc8276e804b62a5df22e4922d179212b065341642bb3844361f8718c0a6fe1a003cf351a743f268c504d283f5e1d0e6501c2df9d2735d0bcf554f6b550fa5b9e3c8f56b92308e2b2d566caaf7343383f64b98e8a1b78aa874bdbe0d1bbcd91ce931129bd67959ea4e42b9b3dcda8fc51c589e522740af700d9f4903c8d49fb7a6b1e9a15617eab509e0e8035a9ec7a89bb15c3e24fdf859096b89d6f81317972ec7b040c6181b0108d32ce7ae4d7d8996eafbdbf7427e8a",
            encrypt4
        )
    }

    @Test
    fun orderPayloadRootTest() {
        val formData = objectMapper.readValue<FormData>(dataString3)
        val orderPayloadRoot = OrderPayloadRoot.convertFormData2Object(
            EncryptUtils.EncryptionParams(encryptKey, iv, version, expireTime), formData
        )
        Assertions.assertEquals(
            "cf27758ab9850d9bdb23809fbb584d774d3abd8ad4c4b42df5d127d4b937f00ad8265c323eff80363d537cf1360e937de5b3625c4373c402dd0286dee5c85031b79dd578e38f06d8e7617abe665c6396f89ae3fc2f6ccb02323132c3224ec4d347e5db2334fa7ccf49fdbbf96c0c945cf2554c7b962efe96f84a5ab03f2e9c5d76e7769e2c95c824e71c7a8c2f66f05ba67646a4160dd7537063873b1bddd5de5ea422de4415b351754d8b65665a800faff51d2c6e9522217aed4877cf551eaffb8e5873b61afbad63d13f2b38fe7561a471f51467c35c8c0e035fed7df71f9a15a22c3808749de92c7c0bd81f2a4b5eff235ff99f4313f4bc51fab1dff83b9298268ece604886ff0cc4e159c19fb5f975e1fd277bc6016b8fc8276e804b62a5df22e4922d179212b065341642bb3844361f8718c0a6fe1a003cf351a743f268c504d283f5e1d0e6501c2df9d2735d0bcf554f6b550fa5b9e3c8f56b92308e2b2d566caaf7343383f64b98e8a1b78aa874bdbe0d1bbcd91ce931129bd67959ea4e42b9b3dcda8fc51c589e522740af700d9f4903c8d49fb7a6b1e9a15617eab509e0e8035a9ec7a89bb15c3e24fdf859096b89d6f81317972ec7b040c6181b0108d32ce7ae4d7d8996eafbdbf7427e8a",
            orderPayloadRoot.encryptedData
        )
    }
}