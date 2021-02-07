package com.orgella.ordersmanagement.application.rest

import com.orgella.ordersmanagement.application.feign.AuctionsServiceClient
import com.orgella.ordersmanagement.application.feign.BasketServiceClient
import com.orgella.ordersmanagement.application.request.AddOrderRequest
import com.orgella.ordersmanagement.application.request.SellItemRequest
import com.orgella.ordersmanagement.application.response.*
import com.orgella.ordersmanagement.domain.OrderEntity
import com.orgella.ordersmanagement.domain.OrderStatus
import com.orgella.ordersmanagement.domain.ProductEntity
import com.orgella.ordersmanagement.domain.service.OrdersService
import com.orgella.ordersmanagement.exceptions.ErrorResponseException
import com.orgella.ordersmanagement.exceptions.FailureCause
import com.orgella.ordersmanagement.exceptions.NotEnoughItemsException
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors

@RestController
@RequestMapping("orders")
class OrdersController(
    private val ordersService: OrdersService,
    private val auctionsServiceClient: AuctionsServiceClient,
    private val basketServiceClient: BasketServiceClient
) {

    @PostMapping(
        "/{userId}",
        produces = [MediaType.APPLICATION_JSON_VALUE],
        consumes = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("#userId == authentication.principal.userId OR hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    fun addOrderInformationForUser(
        @PathVariable userId: String,
        @RequestBody addOrderRequest: AddOrderRequest,
        @CookieValue("UserInfo") cookie: String
    ): ResponseEntity<AddOrderResponse> {
        val userCookie = "UserInfo=${cookie}"
        val productPaths = addOrderRequest.products.map { it.productPath }

        val productDetails = auctionsServiceClient.getAuctionsForBasketPaths(productPaths)

        val ordersToBeAdded = productDetails.items.map {
            val orderRequest = addOrderRequest.products.first { order -> order.productPath == it.auctionPath }

            return@map OrderEntity(
                UUID.randomUUID(),
                userId,
                it.sellerUsername,
                ProductEntity(
                    orderRequest.productPath,
                    orderRequest.quantity,
                    it.price
                ),
                OrderStatus.WAITING_FOR_PAYMENT_CONFIRMATION,
                Date(),
                0
            )
        }

        val createdOrders = mutableListOf<CreatedOrderResponse>()
        val failedOrders = mutableListOf<FailedToCreateOrdersResponse>()
        ordersToBeAdded.forEach {
            try {
                val sellItemResponse = auctionsServiceClient.increaseSoldQuantity(SellItemRequest(it.product.productPath, it.product.quantity), userCookie)
                if (sellItemResponse.message.isNotBlank()) {
                    val createdOrder = ordersService.createOrder(it)
                    createdOrders.add(
                        CreatedOrderResponse(
                            createdOrder.date,
                            createdOrder.orderStatus,
                            createdOrder.product,
                            createdOrder.sellerUsername
                        )
                    )
                }
            } catch (exception: ErrorResponseException) {
                val notEnoughItemsException = NotEnoughItemsException(FailureCause.NotEnoughItemsException, exception)
                failedOrders.add(FailedToCreateOrdersResponse(it.product.productPath,
                    notEnoughItemsException.errorResponseException.errorResponse.message
                ))
            }
        }

        createdOrders.forEach {
            basketServiceClient.deleteBasketItemForUserId(userId, it.product.productPath, userCookie)
        }

        return ResponseEntity.ok(
            AddOrderResponse(
                createdOrders,
                failedOrders
            )
        )
    }

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

    @GetMapping("/seller/{sellerUsername}")
    @PreAuthorize("#sellerUsername == authentication.principal.username OR hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    fun getOrdersForSellerId(
        @PathVariable sellerUsername: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<GetSellerOrdersResponse> {
        val orders = ordersService.getOrdersForSellerUsername(sellerUsername, page)

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