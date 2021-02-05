package com.orgella.ordersmanagement.domain.service

import com.orgella.ordersmanagement.domain.repository.OrdersRepository

class DomainOrdersService(
    private val repository: OrdersRepository
): OrdersService {

}