package com.orgella.ordersmanagement.exceptions

import java.util.stream.Stream

enum class FailureCause {
    NotEnoughItemsException,
    Unknown;

    init {
        fun findByCode(code: String): FailureCause {
            return Stream.of(*values())
                .filter { it.name == code }
                .findAny()
                .orElse(Unknown)
        }
    }
}
