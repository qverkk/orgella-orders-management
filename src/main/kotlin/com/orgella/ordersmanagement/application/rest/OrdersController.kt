package com.orgella.ordersmanagement.application.rest

import com.orgella.ordersmanagement.application.response.GetSellerOrdersResponse
import com.orgella.ordersmanagement.application.response.GetUserOrdersResponse
import com.orgella.ordersmanagement.application.response.OrderResponse
import com.orgella.ordersmanagement.domain.service.OrdersService
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.stream.Collectors

@RestController
@RequestMapping("orders")
class OrdersController(
    private val ordersService: OrdersService
) {

    @GetMapping("/{userId}")
    @PreAuthorize("#userId == authentication.principal.userId OR hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    fun getOrdersForUserId(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<GetUserOrdersResponse> {
        val orders = ordersService.getOrdersForUserId(userId, page)

        return ResponseEntity.ok(
            GetUserOrdersResponse(
                orders.number,
                orders.totalPages,
                orders.content.stream().map {
                    OrderResponse(
                        it.orderStatus.toString(),
                        it.date,
                        it.product.productPath,
                        it.product.quantity,
                        it.product.price
                    )
                }.collect(Collectors.toList())
            )
        )
    }

    @GetMapping("/seller/{sellerId}")
    @PreAuthorize("#sellerId == authentication.principal.userId OR hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    fun getOrdersForSellerId(
        @PathVariable sellerId: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<GetSellerOrdersResponse> {
        val orders = ordersService.getOrdersForSellerId(sellerId, page)

        return ResponseEntity.ok(
            GetSellerOrdersResponse(
                orders.number,
                orders.totalPages,
                orders.content.stream().map {
                    OrderResponse(
                        it.orderStatus.toString(),
                        it.date,
                        it.product.productPath,
                        it.product.quantity,
                        it.product.price
                    )
                }.collect(Collectors.toList())
            )
        )
    }
}