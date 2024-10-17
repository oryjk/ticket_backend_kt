package com.wangrui.ticketsystem.ticket.adaptor.input

import com.wangrui.ticketsystem.extensions.slf4k
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderRequestEntity
import com.wangrui.ticketsystem.ticket.adaptor.output.OrderStatus
import com.wangrui.ticketsystem.ticket.application.port.input.MatchUseCase
import com.wangrui.ticketsystem.ticket.application.port.input.OrderTaskUseCase
import com.wangrui.ticketsystem.ticket.application.port.input.OrderUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.UserDao
import com.wangrui.ticketsystem.ticket.application.port.output.UserDao.Companion.autoUserInfoKey
import kotlinx.coroutines.*
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonPrimitive
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import org.springframework.util.ObjectUtils
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.Paths
import java.nio.file.StandardCopyOption
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

@Service
class OrderListener(val orderUseCase: OrderUseCase,
                    val orderTaskUseCase: OrderTaskUseCase,
                    val redisTemplate: RedisTemplate<String, Any>,
                    val matchUseCase: MatchUseCase,
                    @Value("\${autoCreateOrderWhenStartUp:false}") val autoCreateOrderWhenStartUp: Boolean,
                    @Value("\${jsonFilePath:E:\\抢票\\spider_img}") val jsonFilePath: String) {
    private val logger = slf4k()
    private val scope = CoroutineScope(Dispatchers.Default)
    private val clientToken = "MUX9cBF8"

    private val jobMapping = mutableMapOf<String, Job>()
    private val matchId = matchUseCase.queryLatest().matchId
    suspend fun cancelOrderJob(jobId: String) {
        logger.warn("注意，马上取消任务 $jobId!!!")
        jobMapping[jobId]?.cancelAndJoin()
        jobMapping.remove(jobId)
    }

    fun loopOrderRequest() {
        scope.launch {
            while (true) {
                sendOrders()
                TimeUnit.SECONDS.sleep(1)
            }
        }

    }

    private fun processFilesIfExists(files: List<Path>): String {
        var orderId = ""
        val matchId: String

        if (files.all { Files.exists(it) }) {
            logger.info("All files exist, processing starts.")

            val codeFile = files[0]
            val orderPayloadFile = files[1]
            val tokenFile = files[2]

            val loginCode = extractCodeFromJson(codeFile)
            val orderPayload = fetchContentFromFile(orderPayloadFile)

            val jsonElement = Json.parseToJsonElement(orderPayload)
            if (jsonElement !is JsonObject) return ""
            matchId = extractMatchIdFromOrderPayload(jsonElement)
            orderId = extractIdentifierFromOrderPayload(jsonElement)

            val token = fetchContentFromFile(tokenFile)
            val orderRequestEntity = OrderRequestEntity(
                orderId,
                matchId,
                orderPayload,
                loginCode,
                token,
                LocalDateTime.now(),
                clientToken,
                OrderStatus.ONGOING.status
            )
            orderUseCase.save(orderRequestEntity)
            moveFileToProcessingDir(orderPayloadFile, orderId)
        } else {
            logger.info("Some files are missing, waiting for them.")
        }

        return orderId
    }

    fun getJobs(): Map<String, Job> {
        return jobMapping
    }

    fun sendOrders() {
        val orderRequests = orderUseCase.getAutoBuyInfo()
        if (orderRequests.isEmpty()) {
            logger.warn("现在还没有订单~~~~~~~~~~~~~~~~~~~~~~~~~")
            return
        }

        orderRequests.filter { !ObjectUtils.isEmpty(it.matchId) && !jobMapping.contains(it.orderId) && it.matchId == matchId }
            .forEach { orderRequest ->
                if (OrderStatus.ONGOING.status == orderRequest.orderStatus) {
                    logger.info("创建任务，任务id ${orderRequest.orderId}，当前时间 ${LocalDate.now()}")
                    val createOrderJob = orderTaskUseCase.createOrderJob(orderRequest.orderId, orderRequest)
                    jobMapping[orderRequest.orderId] = createOrderJob

                }

                if (OrderStatus.FAILURE.status != orderRequest.orderStatus) {
                    redisTemplate.boundSetOps(autoUserInfoKey)
                    val miniUserInfo = UserDao.Companion.MiniUserInfo(
                        orderRequest.users[0].uid.toString(),
                        orderRequest.token,
                        orderRequest.version,
                        orderRequest.users[0].realName,
                        "https://servicewechat.com/wxffa42ecd6c0e693d/70/page-frame.html"
                    )
                    redisTemplate.opsForSet().add(autoUserInfoKey, miniUserInfo)
                }
            }

    }

    private fun extractMatchIdFromOrderPayload(jsonElement: JsonObject): String =
        jsonElement["id"]?.jsonPrimitive?.content!!


    private fun extractCodeFromJson(codeFile: Path): String {
        val content = fetchContentFromFile(codeFile)
        val jsonElement = Json.parseToJsonElement(content)
        if (jsonElement !is JsonObject || jsonElement["code"]?.jsonPrimitive?.content == null) {
            throw IllegalArgumentException("index.json content cannot be parsed into code.")
        }

        return jsonElement["code"]?.jsonPrimitive?.content!!
    }

    private fun fetchContentFromFile(file: Path): String {
        val content = Files.readString(file)
        logger.info(content)

        return content
    }

    private fun extractIdentifierFromOrderPayload(jsonElement: JsonObject): String {
        val region = jsonElement["regions"]?.jsonArray?.get(0)
        val user = jsonElement["users"]?.jsonArray?.get(0)
        if (region !is JsonObject || user !is JsonObject) return ""

        val regionName = region["name"]?.jsonPrimitive?.content
        val userName = user["realname"]?.jsonPrimitive?.content
        logger.info("$regionName $userName")

        return "$userName$regionName"
    }

    private fun moveFileToProcessingDir(orderPayloadFile: Path, id: String) {
        val destPath = Paths.get("E:\\抢票\\spider_img\\processed\\$id") // replace this with constant
        if (Files.notExists(destPath)) {
            Files.createDirectories(destPath)
        }

        val destinationFile = destPath.resolve(orderPayloadFile.fileName.toString())
        Files.move(orderPayloadFile, destinationFile, StandardCopyOption.REPLACE_EXISTING)
        logger.info("All files processed.")
    }
}
