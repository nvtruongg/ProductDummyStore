package com.example.dummyjsonapp.core.network

import com.example.dummyjsonapp.data.model.Category
import com.example.dummyjsonapp.data.model.ProductResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("products?limit=0")
    suspend fun getProducts(): Response<ProductResponse>
    @GET("products/search")
    suspend fun searchProducts(@Query("q") query: String): Response<ProductResponse>
    @GET("products/categories")
    suspend fun getCategories() : Response<List<Category>>
    @GET("products/category/{category_slug}")
    suspend fun getProductsByCategory(@Path("category_slug") categorySlug: String): Response<ProductResponse>
}