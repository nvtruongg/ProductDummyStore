package com.example.dummyjsonapp.domain.repository

import com.example.dummyjsonapp.domain.model.CartItemModel
import com.example.dummyjsonapp.domain.model.ProductModel
import com.example.dummyjsonapp.domain.model.CategoryModel
import com.example.dummyjsonapp.domain.result.Resource
import kotlinx.coroutines.flow.Flow

interface ProductRepository {
    fun getProducts(): Flow<Resource<List<ProductModel>>>

    suspend fun getProductById(id: Int): ProductModel?

    fun searchProducts(
        keyword: String
    ): Flow<Resource<List<ProductModel>>>

    fun getCategories(): Flow<Resource<List<CategoryModel>>>

    fun getProductsByCategory(
        categorySlug: String
    ): Flow<Resource<List<ProductModel>>>

    suspend fun toggleFavorite(
        productId: Int,
        isFavorite: Boolean
    )

    suspend fun getAllFavoriteIds(): List<Int>

    fun getFavoriteProducts(): Flow<Resource<List<ProductModel>>>

    suspend fun addToCart(productId: Int): Boolean

    suspend fun getCartItems(): List<CartItemModel>

    suspend fun updateCartQuantity(
        productId: Int,
        quantity: Int
    ): Boolean

    suspend fun removeFromCart(productId: Int)

    suspend fun clearCart()
}