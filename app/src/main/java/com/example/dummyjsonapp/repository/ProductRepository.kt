package com.example.dummyjsonapp.repository

import com.example.dummyjsonapp.api.ApiService
import com.example.dummyjsonapp.db.ProductDao
import com.example.dummyjsonapp.model.Category
import com.example.dummyjsonapp.model.Product
import javax.inject.Inject

class ProductRepository @Inject constructor(
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
    // Nối với API Tìm kiếm
    suspend fun searchProductsFromApi(keyword: String): Result<List<Product>> {
        return try {
            val response = apiService.searchProducts(keyword)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.products)
            } else {
                Result.failure(Exception("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    // Nối với API Danh mục
    suspend fun getCategoriesFromApi(): Result<List<Category>> {
        return try {
            val response = apiService.getCategories()
            if(response.isSuccessful && response.body() != null){
                Result.success(response.body()!!)
            } else {
                Result.failure(Exception("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getProductsByCategoryFromApi(categorySlug: String): Result<List<Product>> {
        return try {
            val response = apiService.getProductsByCategory(categorySlug)

            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.products)
            } else {
                Result.failure(Exception("Lỗi lọc danh mục: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}