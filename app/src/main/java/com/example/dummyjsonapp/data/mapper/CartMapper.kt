package com.example.dummyjsonapp.data.mapper

import com.example.dummyjsonapp.data.local.entity.CartItem
import com.example.dummyjsonapp.domain.model.CartItemModel

fun CartItem.toDomain(): CartItemModel {
    return CartItemModel(
        product = product.toDomain(),
        quantity = quantity
    )
}