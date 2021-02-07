package com.orgella.ordersmanagement.exceptions

class ErrorResponseException(
    cause: Throwable,
    val errorResponse: ErrorResponse
) : RuntimeException(cause)
