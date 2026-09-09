package com.example.dummyjsonapp.repository

import com.example.dummyjsonapp.api.ApiService
import com.example.dummyjsonapp.db.ProductDao
import com.example.dummyjsonapp.model.Product

class ProductRepository(
    private val apiService: ApiService,
    private val productDao : ProductDao
) {
    //lấy dữu liệu từ room
    suspend fun getProductsFromLocal(): List<Product>{
        return productDao.getAllProducts()
    }
    //lấy dữ lieeuj từ api và lưu vào room
    suspend fun refreshProducts(): Result<Unit> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!.products
                productDao.insertProducts(products)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}