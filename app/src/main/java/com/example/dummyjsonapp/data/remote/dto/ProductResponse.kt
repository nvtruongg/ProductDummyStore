package com.example.dummyjsonapp.data.remote.dto

import com.example.dummyjsonapp.data.local.entity.ProductEntity

data class ProductResponse(
    val products: List<ProductEntity>,
    val total: Int,
    val skip: Int,
    val limit: Int
)