package com.orgella.ordersmanagement.infrastructure.configuration

import com.google.gson.Gson
import com.orgella.ordersmanagement.exceptions.ErrorResponse
import com.orgella.ordersmanagement.exceptions.ErrorResponseException
import feign.FeignException
import feign.Response
import feign.RetryableException
import feign.codec.ErrorDecoder
import org.springframework.stereotype.Component
import java.util.*

@Component
class FeignErrorDecoder : ErrorDecoder {

    private val errorDecoder = ErrorDecoder.Default()
    private val gson = Gson()

    override fun decode(methodKey: String, response: Response): Exception {
        val exception = errorDecoder.decode(methodKey, response)

        return if (exception is RetryableException) {
            exception
        } else {
            mapToErrorResponse(exception)
                .orElse(exception)
        }
    }

    fun mapToErrorResponse(exception: Exception): Optional<Exception> {
        return Optional.ofNullable(exception)
            .filter(FeignException::class.java::isInstance)
            .map(FeignException::class.java::cast)
            .map(FeignException::contentUTF8)
            .filter { it.isNotEmpty() }
            .map { gson.fromJson(it, ErrorResponse::class.java) }
            .map { ErrorResponseException(exception, it) }
    }
}