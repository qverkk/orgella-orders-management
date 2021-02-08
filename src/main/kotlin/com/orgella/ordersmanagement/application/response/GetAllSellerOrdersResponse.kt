package com.orgella.ordersmanagement.application.response

import java.math.BigDecimal
import java.util.*

data class GetAllSellerOrdersResponse(
    val maxPages: Int,
    val page: Int,
    val orders: List<SellerOrdersResponse>
)

data class SellerOrdersResponse(
    val id: UUID,
    val productPath: String,
    val price: BigDecimal,
    val quantity: Int,
    val status: String,
    val date: Date,
    val buyerId: String
)
