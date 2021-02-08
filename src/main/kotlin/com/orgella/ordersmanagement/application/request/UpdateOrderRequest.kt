package com.orgella.ordersmanagement.application.request

data class UpdateOrderRequest(
    val orderId: String,
    val sellerUsername: String,
    val orderStatus: String
)
