package com.example.dummyjsonapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

data class ProductResponse(
    val products: List<Product>,
    val total: Int,
    val skip: Int,
    val limit: Int
)

data class Dimensions(
    val width: Double? = null,
    val height: Double? = null,
    val depth: Double? = null
)

data class Review(
    val rating: Int? = null,
    val comment: String? = null,
    val date: String? = null,
    val reviewerName: String? = null,
    val reviewerEmail: String? = null
)

@Entity(tableName = "products")
data class Product(
    @PrimaryKey val id: Int,
    val title: String,
    val description: String,
    val price: Double,
    val rating: Double,
    val thumbnail: String,
    val discountPercentage: Double,
    val category: String,
    val stock: Int,
    val images: List<String>,
    val brand: String? = null,
    val weight: Double? = null,
    val dimensions: Dimensions? = null,
    val warrantyInformation: String? = null,
    val shippingInformation: String? = null,
    val returnPolicy: String? = null,
    val reviews: List<Review>? = null
)
