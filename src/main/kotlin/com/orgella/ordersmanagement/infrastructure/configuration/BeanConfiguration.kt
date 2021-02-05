package com.orgella.ordersmanagement.infrastructure.configuration

import com.orgella.ordersmanagement.domain.repository.OrdersRepository
import com.orgella.ordersmanagement.domain.service.DomainOrdersService
import com.orgella.ordersmanagement.domain.service.OrdersService
import feign.Logger
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class BeanConfiguration {

    @Bean
    fun auctionsService(repository: OrdersRepository): OrdersService {
        return DomainOrdersService(repository)
    }

    @Bean
    @Profile("production")
    fun feignProductionLoggingLevel(): Logger.Level = Logger.Level.NONE

    @Bean
    @Profile("!production")
    fun feignDefaultFeignLoggingLevel(): Logger.Level = Logger.Level.FULL
}