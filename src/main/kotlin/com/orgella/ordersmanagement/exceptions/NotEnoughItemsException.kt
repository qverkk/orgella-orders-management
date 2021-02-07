package com.orgella.ordersmanagement.exceptions

import java.lang.RuntimeException

class NotEnoughItemsException(
    val failureCause: FailureCause,
    val errorResponseException: ErrorResponseException
): RuntimeException() {
}