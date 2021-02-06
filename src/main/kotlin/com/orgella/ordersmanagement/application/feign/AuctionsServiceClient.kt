package com.orgella.ordersmanagement.application.feign

import com.orgella.ordersmanagement.application.request.SellItemRequest
import com.orgella.ordersmanagement.application.response.GetAuctionOrderDetailsResponse
import com.orgella.ordersmanagement.application.response.SellItemResponse
import feign.FeignException
import feign.hystrix.FallbackFactory
import org.slf4j.LoggerFactory
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.stereotype.Component
import org.springframework.web.bind.annotation.*

@FeignClient(name = "auctions-ws", fallbackFactory = AuctionsFallbackFactory::class)
interface AuctionsServiceClient {

    @GetMapping("/auctions/details/orders")
    fun getAuctionsForBasketPaths(@RequestParam auctionPaths: List<String>): GetAuctionOrderDetailsResponse

    @PostMapping("/auctions/sell")
    fun increaseSoldQuantity(
        @RequestBody sellItem: SellItemRequest,
        @RequestHeader("Cookie") cookie: String
    ): SellItemResponse
}

@Component
internal class AuctionsFallbackFactory : FallbackFactory<AuctionsServiceClient> {
    override fun create(cause: Throwable): AuctionsServiceClient {
        return AuctionsServiceFallback(cause)
    }
}

internal class AuctionsServiceFallback(
    private val cause: Throwable? = null
) : AuctionsServiceClient {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override fun getAuctionsForBasketPaths(@RequestParam auctionPaths: List<String>): GetAuctionOrderDetailsResponse {
        if (cause is FeignException
            && cause.status() == 404
        ) {
            logger.error(
                "404 error took place when getAuctionsForBasketPaths was called with paths: "
                        + auctionPaths + ". Error message: "
                        + cause.getLocalizedMessage()
            )
        } else {
            logger.error(
                "Other error took place: " + cause!!.localizedMessage
            )
        }
        return GetAuctionOrderDetailsResponse(emptyList())
    }

    override fun increaseSoldQuantity(sellItem: SellItemRequest, cookie: String): SellItemResponse {
        if (cause is FeignException
            && cause.status() == 404
        ) {
            logger.error(
                "404 error took place when increaseSoldQuantity was called: "
                        + ". Error message: "
                        + cause.getLocalizedMessage()
            )
        } else {
            logger.error(
                "Other error took place: " + cause!!.localizedMessage
            )
        }
        return SellItemResponse("")
    }
}