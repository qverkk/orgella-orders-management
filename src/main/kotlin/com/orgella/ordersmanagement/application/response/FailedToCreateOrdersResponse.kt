package com.orgella.ordersmanagement.application.response

data class FailedToCreateOrdersResponse(
    val auctionPath: String,
    val reason: String
)
