package com.kitchenforce.domain.orders.dto

import javax.validation.constraints.Min

data class OrderTableDto(
    val tableName: String,
    val emptyness: Boolean,
    @Min(value = 0)
    val numberOfGuests: Int
)
