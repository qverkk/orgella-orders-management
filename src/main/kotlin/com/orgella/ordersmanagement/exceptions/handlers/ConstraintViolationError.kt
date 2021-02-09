package com.orgella.ordersmanagement.exceptions.handlers

data class ConstraintViolationError(
    val cause: String,
    val field: String
)
