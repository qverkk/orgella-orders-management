package com.orgella.ordersmanagement.application.response

import java.math.BigDecimal
import java.util.*

data class GetNonReviewedOrdersResponse(
    val totalPages: Int,
    val page: Int,
    val orders: List<NonReviewedOrderResponse>
)

data class NonReviewedOrderResponse(
    val id: UUID,
    val productPath: String,
    val price: BigDecimal,
    val quantity: Int,
    val date: Date
)