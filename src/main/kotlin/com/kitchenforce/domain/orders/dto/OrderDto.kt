package com.kitchenforce.domain.orders.dto

import com.kitchenforce.domain.enum.OrderStatus
import com.kitchenforce.domain.enum.OrderType
import javax.validation.Valid

import com.kitchenforce.domain.delivery.DeliveryAddress

data class OrderDto(
    val orderType: OrderType,
    val orderStatus: OrderStatus?,
    val paymentMethod: String,
    val requirement: String,
    val deliveryAddress: DeliveryAddress,
    @Valid
    val orderTableDto: OrderTableDto?,
    @Valid
    val orderMenuDtoList: List<OrderMenuDto>
)
