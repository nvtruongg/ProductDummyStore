package com.example.dummyjsonapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "favorites")

data class FavoriteEntity(
    @PrimaryKey
    val productId: Int
)