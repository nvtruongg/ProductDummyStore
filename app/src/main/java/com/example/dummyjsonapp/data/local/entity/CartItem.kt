package com.example.dummyjsonapp.data.local.entity

import androidx.room.ColumnInfo
import androidx.room.Embedded

class CartItem (
    @Embedded val product: Product,
    @ColumnInfo(name = "quantity") var quantity: Int
)