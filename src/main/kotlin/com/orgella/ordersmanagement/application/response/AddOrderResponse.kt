package com.orgella.ordersmanagement.application.response

import com.orgella.ordersmanagement.domain.OrderStatus
import com.orgella.ordersmanagement.domain.ProductEntity
import java.util.*

data class AddOrderResponse(
    val createdItems: List<CreatedOrderResponse>
)

data class CreatedOrderResponse(
    val date: Date,
    val orderStatus: OrderStatus,
    val product: ProductEntity,
    val sellerUsername: String
)
