package com.example.dummyjsonapp.domain.repository

import com.example.dummyjsonapp.data.local.entity.CartItem
import com.example.dummyjsonapp.domain.model.ProductModel
import com.example.dummyjsonapp.data.remote.dto.CategoryDto

interface ProductRepository {
    suspend fun getProductsFromLocal(): List<ProductModel>

    suspend fun refreshProducts(): Result<Unit>

    suspend fun getProductById(id: Int): ProductModel?

    suspend fun searchProducts(
        keyword: String
    ): Result<List<ProductModel>>

    suspend fun getCategories(): Result<List<CategoryDto>>

    suspend fun getProductsByCategory(
        categorySlug: String
    ): Result<List<ProductModel>>

    suspend fun toggleFavorite(
        productId: Int,
        isFavorite: Boolean
    )

    suspend fun getAllFavoriteIds(): List<Int>

    suspend fun getFavoriteProducts(): Result<List<ProductModel>>

    suspend fun addToCart(productId: Int): Boolean

    suspend fun getCartItems(): List<CartItem>

    suspend fun updateCartQuantity(
        productId: Int,
        quantity: Int
    ): Boolean

    suspend fun removeFromCart(productId: Int)

    suspend fun clearCart()
}