package com.wangrui.ticketsystem.ticket.domain.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.wangrui.ticketsystem.extensions.slf4k
import com.wangrui.ticketsystem.ticket.application.port.input.RequestUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.MatchReader
import com.wangrui.ticketsystem.ticket.application.port.output.UserDao
import com.wangrui.ticketsystem.ticket.application.port.output.UserDao.Companion.MiniUserInfo
import com.wangrui.ticketsystem.ticket.application.port.output.UserDao.Companion.autoUserInfoKey
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.http.HttpHeaders
import org.springframework.stereotype.Service

@Service
class RequestParamService(
    val userDao: UserDao,
    val matchReader: MatchReader,
    val redisTemplate: RedisTemplate<String, Any>,
    val objectMapper: ObjectMapper
) : RequestUseCase {

    private var currentIndex = 0
    private val logger = slf4k()
    override fun getByUserId(userId: String): RequestUseCase.Companion.RequestParam {
        val miniUserInfo = userDao.findByURLId(userId)
        return buildRequestParam(miniUserInfo)
    }

    override fun getHeader(token: String, hostName: String): HttpHeaders {
//        val miniUserInfo = userDao.findByURLId(userId)
        return buildHeader(token, hostName)
    }

    private fun buildHeader(token: String, hostName: String): HttpHeaders {
        val httpHeaders = HttpHeaders()
        httpHeaders.add("Content-Type", "application/json;charset:utf-8;")
        httpHeaders.add("Referer", "https://servicewechat.com/wxffa42ecd6c0e693d/70/page-frame.html")
        httpHeaders.add("Authorization", token)
        httpHeaders.add("Host", hostName)
        httpHeaders.add("Accept-Charset", "utf-8")
        httpHeaders.add(
            "User-Agent",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/116.0.0.0 Safari/537.36 MicroMessenger/7.0.20.1781(0x6700143B) NetType/WIFI MiniProgramEnv/Windows WindowsWechat/WMPF WindowsWechat(0x63090b13)XWEB/9133"
        )
        return httpHeaders
    }


    private fun buildRequestParam(miniUserInfo: MiniUserInfo): RequestUseCase.Companion.RequestParam {
        val scheduleDO = matchReader.findByIsCurrentTrue()
        return scheduleDO.let {
            val httpHeaders = buildHeader(miniUserInfo.token, "fccdn1.k4n.cc")
            RequestUseCase.Companion.RequestParam(
                miniUserInfo.userId, it?.matchId.toString(), miniUserInfo.token, httpHeaders
            )
        }
    }

    override fun getRobinUser(): RequestUseCase.Companion.RequestParam {
        val userInfoList = redisTemplate.opsForSet().members(autoUserInfoKey)?.map {
            objectMapper.convertValue(it, MiniUserInfo::class.java)
        }

        if(userInfoList!!.isEmpty()){
            return RequestUseCase.Companion.RequestParam.default()
        }

        if (currentIndex >= userInfoList!!.size) {
            currentIndex = 0
        }

        val miniUserInfo = userInfoList[currentIndex]
        logger.info("当前使用这个用户发起检测请求 ${miniUserInfo.nickName}")
        val requestParam = buildRequestParam(miniUserInfo)
        currentIndex++
        return requestParam
    }


}