package com.wangrui.ticketsystem.ticket.adaptor.output

import com.wangrui.ticketsystem.ticket.application.port.input.OrderTaskUseCase
import com.wangrui.ticketsystem.ticket.application.port.output.OrderRequestDao
import com.wangrui.ticketsystem.ticket.domain.OrderPayloadRoot
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Created on 2024/5/7 20:06.
 * @author wangrui
 * @since 0.0.1
 */

@Repository
interface OrderRequestRepository : JpaRepository<OrderRequestEntity, String>, OrderRequestDao {

    override fun getAutoBuyInfo(): List<OrderTaskUseCase.OrderRequest> {
        return findAll()
            .map {
                val root = OrderPayloadRoot.convertJson2Object(it.orderPayload)

                OrderTaskUseCase.OrderRequest(
                    it.id,
                    root.encryptedData,
                    root.version,
                    root.users.map { user ->
                        OrderTaskUseCase.UserOrderInfo(
                            user.id,
                            user.uid,
                            user.signature,
                            user.timestamp,
                            user.realname,
                            user.real_card_id2,
                            user.phone2
                        )
                    },
                    it.loginCode,
                    root.regions.map { it.name },
                    root.expireTime,
                    it.token,
                    root.id,
                    it.tokenId,
                    it.orderStatus
                )
            }
    }
}

@Table(name = "rs_order_request")
@Entity
class OrderRequestEntity(
    @Id val id: String,
    val matchId: String,
    @Column(columnDefinition = "TEXT") val orderPayload: String,
    val loginCode: String,
    @Column(columnDefinition = "TEXT") val token: String,
    val time: LocalDateTime,
    val tokenId: String,
    var orderStatus: Int
) {

    fun toOrderStatus(): OrderStatus {

        return when (this.orderStatus) {
            0 -> OrderStatus.ONGOING
            1 -> OrderStatus.GOT_IT
            else -> OrderStatus.FAILURE
        }
    }
}

enum class OrderStatus(val status: Int) {
    ONGOING(0),
    GOT_IT(1),
    FAILURE(2),
}


