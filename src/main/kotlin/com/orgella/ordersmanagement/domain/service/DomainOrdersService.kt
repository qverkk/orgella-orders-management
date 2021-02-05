package com.orgella.ordersmanagement.domain.service

import com.orgella.ordersmanagement.domain.OrderEntity
import com.orgella.ordersmanagement.domain.repository.OrdersRepository
import org.springframework.data.domain.Page

class DomainOrdersService(
    private val repository: OrdersRepository
): OrdersService {
    override fun getOrdersForUserId(userId: String, page: Int): Page<OrderEntity> {
        return repository.getOrdersForUserId(userId, page)
    }

    override fun getOrdersForSellerId(sellerId: String, page: Int): Page<OrderEntity> {
        return repository.getOrdersForSellerId(sellerId, page)
    }

}