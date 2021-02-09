package com.orgella.ordersmanagement.application.rest

import com.orgella.ordersmanagement.application.feign.AuctionsServiceClient
import com.orgella.ordersmanagement.application.feign.BasketServiceClient
import com.orgella.ordersmanagement.application.request.*
import com.orgella.ordersmanagement.application.response.*
import com.orgella.ordersmanagement.domain.OrderEntity
import com.orgella.ordersmanagement.domain.OrderStatus
import com.orgella.ordersmanagement.domain.ProductEntity
import com.orgella.ordersmanagement.domain.service.OrdersService
import com.orgella.ordersmanagement.exceptions.ErrorResponseException
import com.orgella.ordersmanagement.exceptions.FailureCause
import com.orgella.ordersmanagement.exceptions.NoOrderFoundException
import com.orgella.ordersmanagement.exceptions.NotEnoughItemsException
import com.orgella.ordersmanagement.infrastructure.configuration.security.UserInfo
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.stream.Collectors
import javax.validation.Valid

@RestController
@RequestMapping("orders")
class OrdersController(
    private val ordersService: OrdersService,
    private val auctionsServiceClient: AuctionsServiceClient,
    private val basketServiceClient: BasketServiceClient
) {

    @PostMapping(
        "/create/review",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun createReviewForOrder(
        @Valid @RequestBody createReviewRequest: CreateReviewRequest,
        @CookieValue("UserInfo") cookie: String,
        authentication: Authentication
    ): ResponseEntity<CreateReviewResponse> {
        val userCookie = "UserInfo=${cookie}"
        val user = (authentication.principal as UserInfo)

        val order = ordersService.getOrderByIdAndUserId(createReviewRequest.orderId, user.userId).orElseThrow {
            throw NoOrderFoundException("No order found for ${createReviewRequest.orderId}")
        }

        val response = auctionsServiceClient.createReview(
            CreateAuctionReviewRequest(
                order.id.toString(),
                order.product.productPath,
                user.username,
                createReviewRequest.rating,
                createReviewRequest.description
            ),
            userCookie
        )

        return if (response.id.isNotEmpty()) {
            order.reviewed = true
            ordersService.save(order)
            ResponseEntity.ok(
                CreateReviewResponse(
                    "Success"
                )
            )
        } else {
            ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(CreateReviewResponse("Failure"))
        }
    }

    @GetMapping(
        "/{userId}/nonReviewed",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getNonReviewedOrders(
        @PathVariable userId: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<GetNonReviewedOrdersResponse> {
        val orders = ordersService.getOrdersForUserIdAndNonReviewed(userId, page)

        return ResponseEntity.ok(
            GetNonReviewedOrdersResponse(
                orders.totalPages,
                orders.number,
                orders.content.map {
                    NonReviewedOrderResponse(
                        it.id,
                        it.product.productPath,
                        it.product.price,
                        it.product.quantity,
                        it.date
                    )
                }
            )
        )
    }

    @GetMapping(
        "/orderStatus/all",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    fun getAllOrderStatuses(): ResponseEntity<GetAllOrderStatusesResponse> {
        return ResponseEntity.ok(
            GetAllOrderStatusesResponse(
                OrderStatus.values().map { it.name }
            )
        )
    }

    @PostMapping(
        "/update",
        consumes = [MediaType.APPLICATION_JSON_VALUE],
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("#updateOrderRequest.sellerUsername == authentication.principal.username OR hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    fun updateOrderStatus(@RequestBody updateOrderRequest: UpdateOrderRequest): ResponseEntity<UpdateOrderResponse> {

        val order = ordersService.updateStatusForOrderIdAndSellerUsername(
            updateOrderRequest.orderStatus,
            UUID.fromString(updateOrderRequest.orderId),
            updateOrderRequest.sellerUsername
        )

        if (!order.isPresent) {
            throw NoOrderFoundException("Order wasn't found, can't update")
        }

        val orderEntity = order.get()

        return ResponseEntity.ok(
            UpdateOrderResponse(
                orderEntity.id,
                orderEntity.product.productPath,
                orderEntity.product.price,
                orderEntity.product.quantity,
                orderEntity.orderStatus.name,
                orderEntity.date,
                orderEntity.userId
            )
        )
    }

    @GetMapping(
        "/{sellerUsername}/all",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
    @PreAuthorize("(hasRole('ROLE_SELLER') AND #sellerUsername == authentication.principal.username) OR hasRole('ROLE_ADMIN') or hasRole('ROLE_MODERATOR')")
    fun getAllOrdersForSellerUsername(
        @PathVariable sellerUsername: String,
        @RequestParam(defaultValue = "0") page: Int
    ): ResponseEntity<GetAllSellerOrdersResponse> {

        val orders = ordersService.getOrdersForSellerUsername(sellerUsername, page)

        return ResponseEntity.ok(
            GetAllSellerOrdersResponse(
                orders.totalPages,
                orders.number,
                orders.content.map {
                    SellerOrdersResponse(
                        it.id,
                        it.product.productPath,
                        it.product.price,
                        it.product.quantity,
                        it.orderStatus.name,
                        it.date,
                        it.userId
                    )
                }
            )
        )
    }

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
                0,
                false
            )
        }

        val createdOrders = mutableListOf<CreatedOrderResponse>()
        val failedOrders = mutableListOf<FailedToCreateOrdersResponse>()
        ordersToBeAdded.forEach {
            try {
                val sellItemResponse = auctionsServiceClient.increaseSoldQuantity(
                    SellItemRequest(
                        it.product.productPath,
                        it.product.quantity
                    ), userCookie
                )
                if (sellItemResponse.message.isNotBlank()) {
                    val createdOrder = ordersService.save(it)
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
                failedOrders.add(
                    FailedToCreateOrdersResponse(
                        it.product.productPath,
                        notEnoughItemsException.errorResponseException.errorResponse.message
                    )
                )
            }
        }

        if (!addOrderRequest.buynow) {
            createdOrders.forEach {
                basketServiceClient.deleteBasketItemForUserId(userId, it.product.productPath, userCookie)
            }
        }

        return ResponseEntity.ok(
            AddOrderResponse(
                createdOrders,
                failedOrders
            )
        )
    }

    @GetMapping(
        "/{userId}",
        produces = [MediaType.APPLICATION_JSON_VALUE]
    )
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
                        it.sellerUsername,
                        it.product.productPath,
                        it.product.quantity,
                        it.product.price
                    )
                }.collect(Collectors.toList())
            )
        )
    }
}