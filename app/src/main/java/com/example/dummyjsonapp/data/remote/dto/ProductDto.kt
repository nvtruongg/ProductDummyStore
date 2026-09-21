package com.example.dummyjsonapp.data.remote.dto

data class ProductDto (
    val id: Int,
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
    val dimensions: ProductDimensionsDto? = null,
    val warrantyInformation: String? = null,
    val shippingInformation: String? = null,
    val returnPolicy: String? = null,
    val reviews: List<ProductReviewDto>? = null
)