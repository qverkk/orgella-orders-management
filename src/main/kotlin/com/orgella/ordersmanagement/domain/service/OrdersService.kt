package com.orgella.ordersmanagement.domain.service

import com.orgella.ordersmanagement.domain.OrderEntity
import org.springframework.data.domain.Page
import java.util.*

interface OrdersService {
    fun getOrdersForUserId(userId: String, page: Int): Page<OrderEntity>
    fun getOrdersForSellerUsername(sellerUsername: String, page: Int): Page<OrderEntity>
    fun save(orderEntity: OrderEntity): OrderEntity
    fun updateStatusForOrderIdAndSellerUsername(orderStatus: String, orderId: UUID, sellerUsername: String): Optional<OrderEntity>
    fun getOrdersForUserIdAndNonReviewed(userId: String, page: Int): Page<OrderEntity>
    fun getOrderByIdAndUserId(orderId: String, userId: String): Optional<OrderEntity>
}