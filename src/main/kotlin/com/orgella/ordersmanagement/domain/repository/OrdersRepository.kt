package com.orgella.ordersmanagement.domain.repository

import com.orgella.ordersmanagement.domain.OrderEntity
import org.springframework.data.domain.Page
import java.util.*

interface OrdersRepository {
    fun getOrdersForUserId(userId: String, page: Int): Page<OrderEntity>
    fun getOrdersForSellerId(sellerId: String, page: Int): Page<OrderEntity>
    fun save(orderEntity: OrderEntity): OrderEntity
    fun getOrderByIdAndSellerUsername(orderId: UUID, sellerUsername: String): Optional<OrderEntity>

}
