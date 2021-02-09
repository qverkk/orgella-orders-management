package com.orgella.ordersmanagement.application.request

data class CreateAuctionReviewRequest(
    val orderId: String,
    val auctionPath: String,
    val username: String,
    val rating: Int,
    val description: String
)
