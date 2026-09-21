package com.example.dummyjsonapp.data.remote.dto

data class ProductReviewDto(
    val rating: Int? = null,
    val comment: String? = null,
    val date: String? = null,
    val reviewerName: String? = null,
    val reviewerEmail: String? = null
)