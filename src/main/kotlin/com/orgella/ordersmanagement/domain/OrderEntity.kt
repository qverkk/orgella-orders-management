package com.orgella.ordersmanagement.domain

import org.springframework.data.annotation.Id
import java.util.*

data class OrderEntity(
    @field:Id
    val id: UUID,
    val userId: String,
    val sellerUserId: String,
    val product: ProductEntity,
    val orderStatus: OrderStatus,
    val date: Date
)

data class ProductEntity(
    val productPath: String,
    val quantity: Int,
    val price: Double
)
