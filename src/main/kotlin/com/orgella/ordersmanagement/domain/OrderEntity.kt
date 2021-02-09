package com.orgella.ordersmanagement.domain

import org.springframework.data.annotation.Id
import org.springframework.data.annotation.Version
import java.math.BigDecimal
import java.util.*

data class OrderEntity(
    @field:Id
    val id: UUID,
    val userId: String,
    val sellerUsername: String,
    val product: ProductEntity,
    var orderStatus: OrderStatus,
    val date: Date,
    @field:Version
    val version: Int,
    var reviewed: Boolean
)

data class ProductEntity(
    val productPath: String,
    val quantity: Int,
    val price: BigDecimal
)
