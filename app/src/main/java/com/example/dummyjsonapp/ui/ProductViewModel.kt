package com.example.dummyjsonapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.data.model.CartItem
import com.example.dummyjsonapp.data.model.Category
import com.example.dummyjsonapp.data.model.Product
import com.example.dummyjsonapp.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductViewModel @Inject constructor(
    private val repository : ProductRepository) : ViewModel() {

    // LiveData chứa danh sách sản phẩm
    private val _products = MutableLiveData<List<Product>>()
    val products: LiveData<List<Product>> = _products
    private val _categories = MutableLiveData<List<Category>>()
    val categories: LiveData<List<Category>> = _categories

    // LiveData quản lý trạng thái  (Loading)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData lưu thông báo lỗi nếu có
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _favoriteIds = MutableLiveData<Set<Int>>(emptySet())
    val favoriteIds: LiveData<Set<Int>> = _favoriteIds

    private val _favoritedProductsList = MutableLiveData<List<Product>>()
    val favoritedProductsList: LiveData<List<Product>> = _favoritedProductsList

    //livedate cho Cart
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    init {
        loadData()
        loadCategories()
        loadFavorites()
    }
    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            // b1. đọc dữ liệu từ Room
            val localData = repository.getProductsFromLocal()
            _products.postValue(localData)

            // b2. gọi mạng để làm mới dữ liệu
            val refreshResult = repository.refreshProducts()
            refreshResult.onSuccess {
                val updatedData = repository.getProductsFromLocal()
                _products.postValue(updatedData)
                _errorMessage.postValue(null)
            }.onFailure { exception ->
                if (localData.isEmpty()) {
                    _errorMessage.postValue("Không thể tải dữ liệu: ${exception.message}")
                }
            }

            _isLoading.postValue(false)
        }
    }
    //live data dành cho detail
    private val _selectedProduct = MutableLiveData<Product?>()
    val selectedProduct: LiveData<Product?> = _selectedProduct

    fun loadProductDetail(productId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val product = repository.getProductById(productId)
            _selectedProduct.postValue(product)
        }
    }
    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getCategoriesFromApi()
            result.onSuccess { categoryList ->
                val categoryAll = Category(slug = "", name = "Tất cả", url = "")
                val displayList = listOf(categoryAll) + categoryList
                // Nếu thành công, đẩy danh sách livedata
                _categories.postValue(displayList)
            }.onFailure {
                // Nếu lỗi, tạm thời truyền list rỗng hoặc báo lỗi
                _categories.postValue(emptyList())
            }
        }
    }
    fun searchProducts(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true) // Bật loading

            val result = repository.searchProductsFromApi(keyword)

            result.onSuccess { searchedList ->
                // Tìm thành công -> Bơm danh sách mới vào biến _products cũ
                // MainActivity đang lắng nghe _products sẽ tự động cập nhật UI!
                _products.postValue(searchedList)
                _errorMessage.postValue(null)
            }.onFailure { exception ->
                _errorMessage.postValue("Lỗi tìm kiếm: ${exception.message}")
            }

            _isLoading.postValue(false) // Tắt loading
        }
    }
    fun filterByCategory(slug: String) {
        // Kiểm tra slug rỗng không -> Tất cả
        if (slug.isEmpty()) {
            loadData() // Nếu là "Tất cả", tải lại danh sách gốc từ đầu
            return
        }

        // Nếu là danh mục cụ thể, tiến hành gọi API lọc
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            val result = repository.getProductsByCategoryFromApi(slug)
            result.onSuccess { filteredList ->
                _products.postValue(filteredList)
                _errorMessage.postValue(null)
            }.onFailure { exception ->
                _products.postValue(emptyList()) // Trả về list rỗng để hiện thông báo "Không tìm thấy"
                _errorMessage.postValue("Lỗi: ${exception.message}")
            }

            _isLoading.postValue(false)
        }
    }
    // Tải danh sách tim từ Database lên
    fun loadFavorites() {
        viewModelScope.launch(Dispatchers.IO) {
            val ids = repository.getAllFavoriteIds().toSet()
            _favoriteIds.postValue(ids)
        }
    }

    // Xử lý khi người dùng bấm thả/hủy tim
    fun toggleFavorite(productId: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(productId, isFavorite)
            loadFavorites() // Cập nhật lại danh sách ngay lập tức
            loadFavoritedProductsList()
        }
    }

    fun loadFavoritedProductsList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getFavoritedProducts()
            if (result.isSuccess) {
                _favoritedProductsList.postValue(result.getOrDefault(emptyList()))
            }
        }
    }

    fun addToCart(productId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.addToCart(productId)
            if (success) {
                _errorMessage.postValue("Đã thêm vào giỏ hàng!")
            } else {
                _errorMessage.postValue("Số lượng vượt quá tồn kho!")
            }
        }
    }
    fun loadCart() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.getCartItems()
            _cartItems.postValue(items)

            // Tính tổng tiền: Giá * Số lượng
            val total = items.sumOf { it.product.price * it.quantity }
            _totalPrice.postValue(total)
        }
    }

    fun updateCartQuantity(productId: Int, newQuantity: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            val success = repository.updateCartQuantity(productId, newQuantity)
            if (!success) {
                _errorMessage.postValue("Số lượng vượt quá tồn kho!")
            }
            loadCart()
        }
    }

    fun removeCartItem(productId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeFromCart(productId)
            loadCart() // Tải lại giỏ hàng sau khi xóa
        }
    }
    fun placeOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            //TODO: phát triển bảng OrderEntity,insert thông tin đơn hàng vào đây
            repository.clearCart()
            loadCart()
        }
    }
}