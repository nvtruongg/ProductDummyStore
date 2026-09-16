package com.example.dummyjsonapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart")
class CartEntity (
    @PrimaryKey val productId: Int,
    var quantity: Int
)