package com.wangrui.ticketsystem.ticket.application.port.input

import com.wangrui.ticketsystem.extensions.slf4k
import java.math.BigInteger
import java.security.MessageDigest
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

object EncryptUtils {
    val logger = slf4k()

    /**
     * @param seKey 盐，起到盐的作用，现在是配置在小程序前端代码的，Y6xkC7b5
     * @param timeStamp 提交订单时候的时间戳
     * @param loginCode 用户登录小程序的官方code，这个code虽然5分钟有效，但是一旦兑换成功，就可以使用直到token过期
     * @param userId 用户的id，table rs_mini_user 的 id 字段
     */
    fun getSign(seKey: String, timeStamp: Long, loginCode: String, userId: String): String {
        val f = seKey + timeStamp + loginCode + userId
        return hexMd532(f).also { logger.trace("timeStamp is $timeStamp, loginCode is $loginCode, userId is $userId, md532 result is $it") }
    }

    /**
     *{
     *     "agree": true,
     *     "id": "33",
     *     "regions": [
     *         {
     *             "region": 6,
     *             "estate": 3,
     *             "num": 1,
     *             "name": "503",
     *             "price": "100.00",
     *             "usable_count": 608
     *         }
     *     ],
     *     "users": [
     *         {
     *             "id": 116692,
     *             "uid": 116692,
     *             "realname": "王睿",
     *             "real_card_id": "510***********6011",
     *             "phone": "186****2970",
     *             "is_self": true,
     *             "real_card_id2": "510112198905246011",
     *             "phone2": "18602812970",
     *             "timestamp": 1727507393,
     *             "signature": "b8eebf69d8b88a3427e6b57d6eb13ad4",
     *             "disabled": false,
     *             "disabled2": false,
     *             "showText": "王睿 510***********6011"
     *         }
     *     ]
     * }
     *
     *
     */
    fun encrypt(params: EncryptionParams, data: String): String {
        val a = base64ToByteArray(params.encryptKey)
        val u = toByteArray(encodeIVtoBytes(params.iv))
        val l = stringToUtf8ByteArray(data)
        val secretKey = SecretKeySpec(a, "AES")
        val ivSpec = IvParameterSpec(u)
        val cipher = Cipher.getInstance("AES/CBC/PKCS5Padding")
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec)
        val encryptedBytes = cipher.doFinal(l)
        val result = encryptedBytes.toHex()
        return result
    }

    fun base64ToByteArray(base64: String): ByteArray {
        // 过滤掉非Base64字符
        val cleanedBase64 = base64.replace(Regex("[^A-Za-z0-9+/]"), "")
        val length = cleanedBase64.length

        // 计算目标字节数组的长度
        val requiredLength = (3 * length + 1) / 4

        // Base64解码
        val base64Decoder = Base64.getDecoder()
        val decodedBytes = base64Decoder.decode(cleanedBase64)

        // 准备输出数组并复制解码后的字节
        val result = ByteArray(requiredLength)
        decodedBytes.copyInto(result, endIndex = Math.min(result.size, decodedBytes.size))

        return result
    }

    fun encodeIVtoBytes(iv: String): ByteArray {
        val encodedURI = java.net.URLEncoder.encode(iv, "UTF-8")
        val byteArrayOutput = mutableListOf<Byte>()

        var i = 0
        while (i < encodedURI.length) {
            val char = encodedURI[i]
            if (char == '%') {
                val hexValue = encodedURI.substring(i + 1, i + 3).toInt(16).toByte()
                byteArrayOutput.add(hexValue)
                i += 3
            } else {
                byteArrayOutput.add(char.code.toByte())
                i++
            }
        }

        return byteArrayOutput.toByteArray()
    }

    fun toByteArray(input: Any, isCopy: Boolean = false): ByteArray {
        return when (input) {
            is ByteArray -> if (isCopy) input.copyOf() else input
            is Array<*> -> {
                if (input.all { it is Byte }) {
                    ByteArray(input.size) { (input[it] as Byte) }
                } else {
                    throw IllegalArgumentException("Array contains invalid value: $input")
                }
            }

            is List<*> -> {
                if (input.all { it is Byte }) {
                    ByteArray(input.size) { (input[it] as Byte) }
                } else {
                    throw IllegalArgumentException("List contains invalid value: $input")
                }
            }

            else -> throw IllegalArgumentException("Unsupported array-like object")
        }
    }

    fun stringToUtf8ByteArray(input: String): ByteArray {
        val byteArray = mutableListOf<Byte>()

        for (char in input) {
            val codePoint = char.code
            when {
                codePoint < 0x80 -> {
                    byteArray.add(codePoint.toByte())
                }

                codePoint < 0x800 -> {
                    byteArray.add((0xC0 or (codePoint shr 6)).toByte())
                    byteArray.add((0x80 or (codePoint and 0x3F)).toByte())
                }

                codePoint < 0xD800 || codePoint >= 0xE000 -> {
                    byteArray.add((0xE0 or (codePoint shr 12)).toByte())
                    byteArray.add((0x80 or ((codePoint shr 6) and 0x3F)).toByte())
                    byteArray.add((0x80 or (codePoint and 0x3F)).toByte())
                }

                else -> {
                    // Handle surrogate pairs
                    val highSurrogate = codePoint
                    val lowSurrogate = input[input.indexOf(char) + 1].code
                    val combinedCodePoint = 0x10000 + ((highSurrogate and 0x3FF) shl 10) or (lowSurrogate and 0x3FF)

                    byteArray.add((0xF0 or (combinedCodePoint shr 18)).toByte())
                    byteArray.add((0x80 or ((combinedCodePoint shr 12) and 0x3F)).toByte())
                    byteArray.add((0x80 or ((combinedCodePoint shr 6) and 0x3F)).toByte())
                    byteArray.add((0x80 or (combinedCodePoint and 0x3F)).toByte())
                }
            }
        }

        return byteArray.toByteArray()
    }

    fun padPkcs7(input: ByteArray, blockSize: Int = 16): ByteArray {
        val padding = blockSize - (input.size % blockSize)
        val paddedArray = ByteArray(input.size + padding)

        // Copy original data
        System.arraycopy(input, 0, paddedArray, 0, input.size)

        // Add padding
        for (i in input.size until paddedArray.size) {
            paddedArray[i] = padding.toByte()
        }

        return paddedArray
    }

    fun fromBytes(input: ByteArray): String {
        // 定义一个十六进制字符数组
        val hexChars = "0123456789ABCDEF".toCharArray()
        val result = StringBuilder()

        // 遍历输入字节数组
        for (byte in input) {
            val intVal = byte.toInt() and 0xFF
            result.append(hexChars[intVal ushr 4])
            result.append(hexChars[intVal and 0x0F])
        }

        return result.toString()
    }


    private fun ByteArray.toHex(): String {
        return this.joinToString("") { "%02x".format(it) }
    }


    private fun hexMd532(input: String): String {
        val md = MessageDigest.getInstance("MD5")
        val byteArray = md.digest(input.toByteArray())
        val bigInt = BigInteger(1, byteArray)
        var hashText = bigInt.toString(16)
        // Pad with leading zeros to ensure the length is 32
        while (hashText.length < 32) {
            hashText = "0$hashText"
        }
        return hashText
    }

    data class EncryptionParams(
        val encryptKey: String, val iv: String, val version: Int, val expireTime: Long
    )
}