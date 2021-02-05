package com.orgella.ordersmanagement.infrastructure.repository.mongo

import com.orgella.ordersmanagement.domain.repository.OrdersRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Component

@Component
@Primary
class MongoOrdersRepository(
    val repository: SpringDataMongoOrdersRepository
) : OrdersRepository {

}