package com.orgella.ordersmanagement.application.response

data class GetUserOrdersResponse(
    val page: Int,
    val maxPage: Int,
    val orders: List<OrderResponse>
)
