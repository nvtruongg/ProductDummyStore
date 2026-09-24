package com.example.dummyjsonapp.presentation

import com.example.dummyjsonapp.data.remote.dto.CategoryDto
import com.example.dummyjsonapp.domain.model.CategoryModel
import com.example.dummyjsonapp.domain.model.ProductModel

data class UiState (
    val products : List<ProductModel> = emptyList(),
    val product : ProductModel? = null,
    val categories : List<CategoryModel> = emptyList(),
    val favoriteIds : Set<Int> = emptySet(),
    val isLoading : Boolean = false,
    val errorMessage : String? = null
    )