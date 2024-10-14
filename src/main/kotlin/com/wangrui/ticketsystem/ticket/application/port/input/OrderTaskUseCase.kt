package com.wangrui.ticketsystem.ticket.application.port.input

import kotlinx.coroutines.Job

interface OrderTaskUseCase {


    //    fun cancelOrderJob(jobId: String)
    fun createOrderJob(jobId: String, orderRequest: OrderRequest): Job

    data class OrderRequest(
        val orderId: String,
        val encryptedData: String,//和区域有关，很时间有关等等
        val version: Int = 37,//encryptedData的版本
        val users: List<UserOrderInfo>,
        val loginCode: String,//用户微信的登录code，参与生成encryptedData？
        val regions: List<String>,// 抢那个区，参与生成encryptedData
        val expireTime: Long,//encryptedData的过期的时间，可以随便改
        val token: String,//最后一次登录的时间戳，参与生成encryptedData
        val matchId: String,//比赛的id
        val clientTokenId: String,
        val orderStatus: Int
    )

    data class UserOrderInfo(
        val id: Int,
        val uid: Int,
        val signature: String, //参与生成encryptedData？与区域无关
        val timestamp: Long,//最后一次登录的时间戳，参与生成encryptedData
        val realName: String,//真实的名字
        val realCardId2: String,//身份证号码
        val phone2: String,//手机号码


    )

}