package com.orgella.ordersmanagement.application.feign

import feign.FeignException
import feign.Headers
import feign.hystrix.FallbackFactory
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestHeader

@FeignClient(name = "users-basket-ws", fallbackFactory = BasketFallbackFactory::class)
interface BasketServiceClient {

    @DeleteMapping("/basket/{userId}/{productPath}")
    fun deleteBasketItemForUserId(@PathVariable userId: String, @PathVariable productPath: String, @RequestHeader("Cookie") cookie: String)
}

@Component
internal class BasketFallbackFactory : FallbackFactory<BasketServiceClient> {
    override fun create(cause: Throwable): BasketServiceClient {
        return BasketServiceFallback(cause)
    }
}

internal class BasketServiceFallback(
    private val cause: Throwable? = null
) : BasketServiceClient {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun deleteBasketItemForUserId(@PathVariable userId: String, @PathVariable productPath: String, cookie: String) {
        if (cause is FeignException
            && cause.status() == 404
        ) {
            logger.error(
                "404 error took place when deleteBasketItemForUserId was called: "
                        + ". Error message: "
                        + cause.getLocalizedMessage()
            )
        } else {
            logger.error(
                "Other error took place: " + cause!!.localizedMessage
            )
        }
    }
}