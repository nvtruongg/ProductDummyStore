package com.example.dummyjsonapp.domain.repository

import com.example.dummyjsonapp.data.local.entity.CartItem
import com.example.dummyjsonapp.data.local.entity.ProductEntity
import com.example.dummyjsonapp.data.remote.dto.Category

interface ProductRepository {
    suspend fun getProductsFromLocal(): List<ProductEntity>

    suspend fun refreshProducts(): Result<Unit>

    suspend fun getProductById(id: Int): ProductEntity?

    suspend fun searchProductsFromApi(
        keyword: String
    ): Result<List<ProductEntity>>

    suspend fun getCategoriesFromApi(): Result<List<Category>>

    suspend fun getProductsByCategoryFromApi(
        categorySlug: String
    ): Result<List<ProductEntity>>

    suspend fun toggleFavorite(
        productId: Int,
        isFavorite: Boolean
    )

    suspend fun getAllFavoriteIds(): List<Int>

    suspend fun getFavoriteProducts(): Result<List<ProductEntity>>

    suspend fun addToCart(productId: Int): Boolean

    suspend fun getCartItems(): List<CartItem>

    suspend fun updateCartQuantity(
        productId: Int,
        quantity: Int
    ): Boolean

    suspend fun removeFromCart(productId: Int)

    suspend fun clearCart()
}