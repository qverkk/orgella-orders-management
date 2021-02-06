package com.orgella.ordersmanagement.domain.repository

import com.orgella.ordersmanagement.domain.OrderEntity
import org.springframework.data.domain.Page

interface OrdersRepository {
    fun getOrdersForUserId(userId: String, page: Int): Page<OrderEntity>
    fun getOrdersForSellerId(sellerId: String, page: Int): Page<OrderEntity>
    fun createOrder(orderEntity: OrderEntity): OrderEntity

}
