package com.orgella.ordersmanagement.application.request

data class SellItemRequest(
    val auctionPath: String,
    val quantity: Int
)
