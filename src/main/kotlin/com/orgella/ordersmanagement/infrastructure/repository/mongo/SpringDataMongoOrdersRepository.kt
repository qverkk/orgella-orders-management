package com.orgella.ordersmanagement.infrastructure.repository.mongo

import com.orgella.ordersmanagement.domain.OrderEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface SpringDataMongoOrdersRepository: MongoRepository<OrderEntity, UUID> {

    fun findAllByUserId(userId: String, pageable: Pageable): Page<OrderEntity>

    fun findAllBySellerUsername(sellerUsername: String, pageable: Pageable): Page<OrderEntity>

    fun findByIdAndSellerUsername(orderId: UUID, sellerUsername: String): Optional<OrderEntity>
}