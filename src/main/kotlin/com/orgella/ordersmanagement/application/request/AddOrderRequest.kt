package com.orgella.ordersmanagement.application.request

data class AddOrderRequest(
    val userId: String,
    val products: List<AddProductRequest>
)

data class AddProductRequest(
    val productPath: String,
    val quantity: Int
)
