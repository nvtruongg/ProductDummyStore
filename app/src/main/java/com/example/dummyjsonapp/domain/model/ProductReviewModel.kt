package com.example.dummyjsonapp.domain.model

data class ProductReviewModel (
    val rating: Int? = null,
    val comment: String? = null,
    val date: String? = null,
    val reviewerName: String? = null,
    val reviewerEmail: String? = null
)