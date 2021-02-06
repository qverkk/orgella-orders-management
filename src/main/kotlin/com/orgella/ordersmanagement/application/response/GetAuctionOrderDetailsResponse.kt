package com.orgella.ordersmanagement.application.response

import java.math.BigDecimal

data class GetAuctionOrderDetailsResponse(
    val items: List<OrderItemInfo>
)

data class OrderItemInfo(
    val auctionPath: String,
    val quantity: Int,
    val boughtQuantity: Int,
    val price: BigDecimal,
    var sellerUsername: String
)