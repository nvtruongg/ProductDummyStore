package com.example.dummyjsonapp.data.repository

import com.example.dummyjsonapp.data.remote.ApiService
import com.example.dummyjsonapp.data.local.dao.ProductDao
import com.example.dummyjsonapp.data.local.entity.CartEntity
import com.example.dummyjsonapp.data.model.CartItem
import com.example.dummyjsonapp.data.model.Category
import com.example.dummyjsonapp.data.local.entity.FavoriteEntity
import com.example.dummyjsonapp.data.model.Product
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
    suspend fun getProductById(id: Int): Product? {
        return productDao.getProductById(id)
    }
    // Nối với API Tìm kiếm
    suspend fun searchProductsFromApi(keyword: String): Result<List<Product>> {
        return try {
            val response = apiService.searchProducts(keyword)
            if (response.isSuccessful && response.body() != null) {
                Result.success(response.body()!!.products)
            } else {
                val localData = productDao.searchProducts(keyword)
                if (localData.isNotEmpty()) Result.success(localData)
                else Result.failure(Exception("Không tìm thấy kết quả!"))
            }
        } catch (e: Exception) {
            val localData = productDao.searchProducts(keyword)
            if (localData.isNotEmpty()) {
                Result.success(localData)
            } else {
                Result.failure(Exception("Bạn đang offline!"))
            }
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
                val localData = productDao.getProductsByCategory(categorySlug)
                if (localData.isNotEmpty()) Result.success(localData)
                else Result.failure(Exception("Lỗi máy chủ và không có dữ liệu cũ!"))
            }
        } catch (e: Exception) {
            val localData = productDao.getProductsByCategory(categorySlug)
            if (localData.isNotEmpty()) {
                Result.success(localData)
            } else {
                Result.failure(Exception("Bạn đang offline!"))
            }
        }
    }
    suspend fun toggleFavorite(productId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            productDao.insertFavorite(FavoriteEntity(productId))
        } else {
            productDao.removeFavorite(productId)
        }
    }

    suspend fun getAllFavoriteIds(): List<Int> {
        return productDao.getAllFavoriteIds()
    }
    suspend fun getFavoritedProducts(): Result<List<Product>> {
        return try {
            val favorites = productDao.getFavoritedProducts()
            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(Exception("Không thể tải danh sách yêu thích!"))
        }
    }

    suspend fun addToCart(productId: Int) : Boolean {
        val product = productDao.getProductById(productId) ?: return false
        val existingItem = productDao.getCartItemById(productId)

        val currentQuantity = existingItem?.quantity ?: 0
        if(currentQuantity + 1> product.stock){
            return false
        }
        if (existingItem != null) {
            existingItem.quantity += 1
            productDao.insertOrUpdateCart(existingItem)
        } else {
            productDao.insertOrUpdateCart(CartEntity(productId, 1))
        }
        return true
    }

    suspend fun getCartItems(): List<CartItem> {
        return productDao.getCartItems()
    }

    suspend fun updateCartQuantity(productId: Int, quantity: Int): Boolean {
        val product = productDao.getProductById(productId) ?: return false
        if(quantity > product.stock) return false
        if (quantity > 0) {
            val item = productDao.getCartItemById(productId)
            if (item != null) {
                item.quantity = quantity
                productDao.insertOrUpdateCart(item)
            }
        } else {
            // Nếu số lượng tụt xuống 0 thì xóa luôn khỏi giỏ
            productDao.removeFromCart(productId)
        }
        return true
    }

    suspend fun removeFromCart(productId: Int) {
        productDao.removeFromCart(productId)
    }
    suspend fun clearCart() {
        productDao.clearCart()
    }
}