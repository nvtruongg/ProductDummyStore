package com.example.dummyjsonapp.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.dummyjsonapp.data.local.entity.CartEntity
import com.example.dummyjsonapp.data.local.entity.CartItem
import com.example.dummyjsonapp.data.local.entity.FavoriteEntity
import com.example.dummyjsonapp.data.local.entity.ProductEntity

@Dao
interface ProductDao {
    @Query("select * from products")
    suspend fun getAllProducts(): List<ProductEntity>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: Int): ProductEntity?

    @Query("SELECT * FROM products WHERE category = :categorySlug")
    suspend fun getProductsByCategory(categorySlug: String): List<ProductEntity>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :keyword || '%' OR description LIKE '%' || :keyword || '%'")
    suspend fun searchProducts(keyword: String): List<ProductEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProducts(products: List<ProductEntity>)

    @Query("DELETE FROM products")
    suspend fun clearAllProducts()

    // --- CÁC HÀM CHO FAVORITES ---

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE productId = :productId")
    suspend fun removeFavorite(productId: Int)

    @Query("SELECT productId FROM favorites")
    suspend fun getAllFavoriteIds(): List<Int>

    @Query("SELECT products.* FROM products INNER JOIN favorites ON products.id = favorites.productId")
    suspend fun getFavoritedProducts(): List<ProductEntity>

    // --- CÁC HÀM CHO GIỎ HÀNG (CART) ---

    // Thêm hoặc cập nhật sản phẩm trong giỏ
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateCart(cartItem: CartEntity)

    // Xóa một sản phẩm khỏi giỏ
    @Query("DELETE FROM cart WHERE productId = :productId")
    suspend fun removeFromCart(productId: Int)

    // Lấy danh sách hiển thị Giỏ hàng (Gộp Product và Số lượng)
    @Query("SELECT products.*, cart.quantity FROM products INNER JOIN cart ON products.id = cart.productId")
    suspend fun getCartItems(): List<CartItem>

    // (Tùy chọn) Xóa toàn bộ giỏ hàng sau khi thanh toán xong
    @Query("DELETE FROM cart")
    suspend fun clearCart()

    @Query("SELECT * FROM cart WHERE productId = :productId")
    suspend fun getCartItemById(productId: Int): CartEntity?
}