package com.orgella.ordersmanagement.application.response

import java.math.BigDecimal
import java.util.*

data class OrderResponse(
    val status: String,
    val date: Date,
    val productPath: String,
    val quantity: Int,
    val price: BigDecimal
)
