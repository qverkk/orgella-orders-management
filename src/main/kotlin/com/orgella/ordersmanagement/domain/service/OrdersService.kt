package com.orgella.ordersmanagement.domain.service

import com.orgella.ordersmanagement.domain.OrderEntity
import org.springframework.data.domain.Page

interface OrdersService {
    fun getOrdersForUserId(userId: String, page: Int): Page<OrderEntity>
    fun getOrdersForSellerId(sellerId: String, page: Int): Page<OrderEntity>
}