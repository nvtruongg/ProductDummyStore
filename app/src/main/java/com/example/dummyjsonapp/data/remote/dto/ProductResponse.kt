package com.example.dummyjsonapp.data.remote.dto

data class ProductResponse(
    val products: List<ProductDto>,
    val total: Int,
    val skip: Int,
    val limit: Int
)