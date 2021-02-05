package com.orgella.ordersmanagement.application.rest

import com.orgella.ordersmanagement.domain.service.OrdersService
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("orders")
class OrdersController(
    private val ordersService: OrdersService
) {

}