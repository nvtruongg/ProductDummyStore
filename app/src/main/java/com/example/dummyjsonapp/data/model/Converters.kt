package com.example.dummyjsonapp.data.model

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromStringList(value: List<String>): String {
        return Gson().toJson(value) // List -> Json
    }

    @TypeConverter
    fun toStringList(value: String): List<String> {
        val listType = object : com.google.gson.reflect.TypeToken<List<String>>() {}.type
        return Gson().fromJson(value, listType) // Json -> List
    }

    @TypeConverter
    fun fromDimensions(value: Dimensions?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toDimensions(value: String?): Dimensions? {
        if (value.isNullOrEmpty()) return null
        return Gson().fromJson(value, Dimensions::class.java)
    }

    @TypeConverter
    fun fromReviewList(value: List<Review>?): String? {
        return Gson().toJson(value)
    }

    @TypeConverter
    fun toReviewList(value: String?): List<Review>? {
        if (value.isNullOrEmpty()) return null
        val listType = object : com.google.gson.reflect.TypeToken<List<Review>>() {}.type
        return Gson().fromJson(value, listType)
    }
}
