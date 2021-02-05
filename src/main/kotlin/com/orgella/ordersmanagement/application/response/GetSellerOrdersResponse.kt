package com.orgella.ordersmanagement.application.response

data class GetSellerOrdersResponse(
    val page: Int,
    val maxPages: Int,
    val orders: List<OrderResponse>
)
