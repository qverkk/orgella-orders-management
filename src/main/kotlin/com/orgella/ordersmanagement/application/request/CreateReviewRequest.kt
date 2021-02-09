package com.orgella.ordersmanagement.application.request

import javax.validation.constraints.*

data class CreateReviewRequest(
    @field:NotNull(message = "Order id cannot be null")
    @field:NotEmpty(message = "Order id must be filled")
    @field:Pattern(
        regexp = "\\b[0-9a-f]{8}\\b-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-\\b[0-9a-f]{12}\\b",
        message = "Must be a valid order id"
    )
    val orderId: String,
    @field:NotNull(message = "Rating cannot be null")
    @field:Min(value = 1, message = "Rating must be between 1 and 5")
    @field:Max(value = 5, message = "Rating must be between 1 and 5")
    val rating: Int,
    @field:NotNull(message = "Description cannot be null")
    @field:Size(min = 3, message = "Description must be longer than 3 characters")
    val description: String
)
