package com.example.dummyjsonapp.data.repository

import com.example.dummyjsonapp.data.remote.ApiService
import com.example.dummyjsonapp.data.local.dao.ProductDao
import com.example.dummyjsonapp.data.local.entity.CartEntity
import com.example.dummyjsonapp.data.local.entity.CartItem
import com.example.dummyjsonapp.data.remote.dto.CategoryDto
import com.example.dummyjsonapp.data.local.entity.FavoriteEntity
import com.example.dummyjsonapp.data.mapper.toEntity
import com.example.dummyjsonapp.data.mapper.toDomain
import com.example.dummyjsonapp.domain.repository.ProductRepository
import com.example.dummyjsonapp.domain.model.ProductModel
import javax.inject.Inject

class ProductRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val productDao : ProductDao
): ProductRepository {

    override suspend fun getProductsFromLocal(): List<ProductModel>{
        return productDao.getAllProducts().map{it.toDomain()}
    }

    override suspend fun refreshProducts(): Result<Unit> {
        return try {
            val response = apiService.getProducts()
            if (response.isSuccessful && response.body() != null) {
                val products = response.body()!!.products.map {it.toEntity()}
                productDao.insertProducts(products)
                Result.success(Unit)
            } else {
                Result.failure(Exception("Lỗi API: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getProductById(id: Int): ProductModel? {
        return productDao.getProductById(id)?.toDomain()
    }

    override suspend fun searchProducts(keyword: String): Result<List<ProductModel>> {
        return try {
            val response = apiService.searchProducts(keyword)
            if (response.isSuccessful && response.body() != null) {
                val entities = response.body()!!.products.map { it.toEntity() }
                productDao.insertProducts(entities)
                Result.success(entities.map { it.toDomain() })
            } else {
                val localData = productDao.searchProducts(keyword)
                if (localData.isNotEmpty()) Result.success(localData.map { it.toDomain() })
                else Result.failure(Exception("Không tìm thấy kết quả!"))
            }
        } catch (e: Exception) {
            val localData = productDao.searchProducts(keyword)
            if (localData.isNotEmpty()) {
                Result.success(localData.map { it.toDomain() })
            } else {
                Result.failure(Exception("Bạn đang offline!"))
            }
        }
    }

    override suspend fun getCategories(): Result<List<CategoryDto>> {
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

    override suspend fun getProductsByCategory(categorySlug: String): Result<List<ProductModel>> {
        return try {
            val response = apiService.getProductsByCategory(categorySlug)

            if (response.isSuccessful && response.body() != null) {
                val entities = response.body()!!.products.map { it.toEntity() }
                productDao.insertProducts(entities)
                Result.success(entities.map { it.toDomain() })
            } else {
                val localData = productDao.getProductsByCategory(categorySlug)
                if (localData.isNotEmpty()) Result.success(localData.map { it.toDomain() })
                else Result.failure(Exception("Lỗi máy chủ và không có dữ liệu cũ!"))
            }
        } catch (e: Exception) {
            val localData = productDao.getProductsByCategory(categorySlug)
            if (localData.isNotEmpty()) {
                Result.success(localData.map { it.toDomain() })
            } else {
                Result.failure(Exception("Bạn đang offline!"))
            }
        }
    }
    override suspend fun toggleFavorite(productId: Int, isFavorite: Boolean) {
        if (isFavorite) {
            productDao.insertFavorite(FavoriteEntity(productId))
        } else {
            productDao.removeFavorite(productId)
        }
    }

    override suspend fun getAllFavoriteIds(): List<Int> {
        return productDao.getAllFavoriteIds()
    }
    override suspend fun getFavoriteProducts(): Result<List<ProductModel>> {
        return try {
            val favorites = productDao.getFavoriteProducts().map { it.toDomain() }
            Result.success(favorites)
        } catch (e: Exception) {
            Result.failure(Exception("Không thể tải danh sách yêu thích!"))
        }
    }

    override suspend fun addToCart(productId: Int) : Boolean {
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

    override suspend fun getCartItems(): List<CartItem> {
        return productDao.getCartItems()
    }

    override suspend fun updateCartQuantity(productId: Int, quantity: Int): Boolean {
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

    override suspend fun removeFromCart(productId: Int) {
        productDao.removeFromCart(productId)
    }
    override suspend fun clearCart() {
        productDao.clearCart()
    }
}