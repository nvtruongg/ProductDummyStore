package com.example.dummyjsonapp.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
class CategoryEntity (
    @PrimaryKey val slug : String,
    val name : String,
    val url : String
)