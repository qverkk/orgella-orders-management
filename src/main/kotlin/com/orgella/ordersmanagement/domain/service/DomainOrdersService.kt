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

    override fun getOrdersForSellerUsername(sellerUsername: String, page: Int): Page<OrderEntity> {
        return repository.getOrdersForSellerId(sellerUsername, page)
    }

    override fun createOrder(orderEntity: OrderEntity): OrderEntity {
        return repository.createOrder(orderEntity)
    }

}