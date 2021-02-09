package com.orgella.ordersmanagement.domain.service

import com.orgella.ordersmanagement.domain.OrderEntity
import com.orgella.ordersmanagement.domain.OrderStatus
import com.orgella.ordersmanagement.domain.repository.OrdersRepository
import org.springframework.data.domain.Page
import java.util.*

class DomainOrdersService(
    private val repository: OrdersRepository
): OrdersService {
    override fun getOrdersForUserId(userId: String, page: Int): Page<OrderEntity> {
        return repository.getOrdersForUserId(userId, page)
    }

    override fun getOrdersForSellerUsername(sellerUsername: String, page: Int): Page<OrderEntity> {
        return repository.getOrdersForSellerUsername(sellerUsername, page)
    }

    override fun save(orderEntity: OrderEntity): OrderEntity {
        return repository.save(orderEntity)
    }

    override fun updateStatusForOrderIdAndSellerUsername(
        orderStatus: String,
        orderId: UUID,
        sellerUsername: String
    ): Optional<OrderEntity> {
        val order = repository.getOrderByIdAndSellerUsername(orderId, sellerUsername)
        order.ifPresent {
            it.orderStatus = OrderStatus.valueOf(orderStatus)
            repository.save(it)
        }
        return order
    }

    override fun getOrdersForUserIdAndNonReviewed(userId: String, page: Int): Page<OrderEntity> {
        return repository.getOrdersForUserIdAndNonReviewed(userId, page)
    }

    override fun getOrderByIdAndUserId(orderId: String, userId: String): Optional<OrderEntity> {
        return repository.getOrderByIdAndUserId(UUID.fromString(orderId), userId)
    }

}