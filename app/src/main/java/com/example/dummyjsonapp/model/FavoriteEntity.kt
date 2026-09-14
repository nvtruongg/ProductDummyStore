package com.example.dummyjsonapp.model

import androidx.room.Entity
import androidx.room.PrimaryKey
@Entity(tableName = "favorites")

data class FavoriteEntity(
    @PrimaryKey
    val productId: Int
)