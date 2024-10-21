package com.wangrui.ticketsystem.ticket.application.port.input

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

class EncryptUtilsTest {

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

    @Test
    fun test_vi() {
        val result = EncryptUtils.encodeIVtoBytes("a00f46201a17357e")
        assertEquals("a00f46201a17357e", String(result, Charsets.UTF_8))

        val result1 = EncryptUtils.toByteArray(result)
        assertEquals("a00f46201a17357e", String(result1, Charsets.UTF_8))
    }

    @Test
    fun encodeToByteArray_test() {
        val result = EncryptUtils.stringToUtf8ByteArray(dataString4)
        val expect = dataString4.encodeToByteArray()
        val result1 = dataString4.toByteArray()
        assertEquals(String(expect, Charsets.UTF_8), String(result, Charsets.UTF_8))
        assertEquals(String(expect, Charsets.UTF_8), String(result1, Charsets.UTF_8))
    }

}