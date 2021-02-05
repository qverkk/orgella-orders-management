package com.orgella.ordersmanagement.domain

enum class OrderStatus(val status: String) {
    WAITING_FOR_PAYMENT_CONFIRMATION("Oczekiwanie na potwierdzenie platnosci"),
    ORDER_SHIPPED("Produkt zostal wyslany"),
    ORDER_AWAITING_PICKUP("Produkt oczekuje na odebranie z placowki"),
    COMPLETED("Produkt zostal dostarczony")
}
