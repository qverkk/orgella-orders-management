package com.orgella.ordersmanagement.infrastructure.repository.mongo

import com.orgella.ordersmanagement.domain.OrderEntity
import org.springframework.data.mongodb.repository.MongoRepository
import java.util.*

interface SpringDataMongoOrdersRepository: MongoRepository<OrderEntity, UUID> {
}