package com.orgella.ordersmanagement.infrastructure.repository.mongo

import com.orgella.ordersmanagement.domain.OrderEntity
import com.orgella.ordersmanagement.domain.repository.OrdersRepository
import org.springframework.context.annotation.Primary
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Component
import java.util.*

@Component
@Primary
class MongoOrdersRepository(
    val repository: SpringDataMongoOrdersRepository
) : OrdersRepository {
    override fun getOrdersForUserId(userId: String, page: Int): Page<OrderEntity> {
        val pageable = PageRequest.of(page, 20)
        return repository.findAllByUserId(userId, pageable)
    }

    override fun getOrdersForSellerId(sellerUsername: String, page: Int): Page<OrderEntity> {
        val pageable = PageRequest.of(page, 20)
        return repository.findAllBySellerUsername(sellerUsername, pageable)
    }

    override fun save(orderEntity: OrderEntity): OrderEntity {
        return repository.save(orderEntity)
    }

    override fun getOrderByIdAndSellerUsername(orderId: UUID, sellerUsername: String): Optional<OrderEntity> {
        return repository.findByIdAndSellerUsername(orderId, sellerUsername)
    }
}