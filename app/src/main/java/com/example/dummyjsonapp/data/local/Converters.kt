package com.example.dummyjsonapp.data.local

import androidx.room.TypeConverter
import com.example.dummyjsonapp.data.local.entity.ProductDimensions
import com.example.dummyjsonapp.data.local.entity.ProductReview
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value) // List -> Json
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) // Json -> List
    }

    @TypeConverter
    fun fromDimensions(value: ProductDimensions?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toDimensions(value: String?): ProductDimensions? {
        if (value.isNullOrEmpty()) return null
        return Gson().fromJson(value, ProductDimensions::class.java)
    }

    @TypeConverter
    fun fromReviewList(value: List<ProductReview>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toReviewList(value: String?): List<ProductReview>? {
        if (value.isNullOrEmpty()) return null
        val listType = object : TypeToken<List<ProductReview>>() {}.type
        return Gson().fromJson(value, listType)
    }
}