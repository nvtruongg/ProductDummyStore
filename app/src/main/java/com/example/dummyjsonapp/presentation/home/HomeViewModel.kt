package com.example.dummyjsonapp.presentation.home

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.data.remote.dto.CategoryDto
import com.example.dummyjsonapp.domain.model.ProductModel
import com.example.dummyjsonapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val repository: ProductRepository
) : ViewModel() {
    private val _products = MutableLiveData<List<ProductModel>>()
    val products: LiveData<List<ProductModel>> = _products
    private val _categories = MutableLiveData<List<CategoryDto>>()
    val categories: LiveData<List<CategoryDto>> = _categories

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    private val _favoriteIds = MutableLiveData<Set<Int>>(emptySet())
    val favoriteIds: LiveData<Set<Int>> = _favoriteIds

    init {
        loadData()
        loadCategories()
        loadFavoriteIds()
    }
    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            val localData = repository.getProductsFromLocal()
            _products.postValue(localData)

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
    fun loadCategories() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getCategories()
            result.onSuccess { categoryList ->
                val categoryAll = CategoryDto(slug = "", name = "Tất cả", url = "")
                val displayList = listOf(categoryAll) + categoryList
                _categories.postValue(displayList)
            }.onFailure {
                _categories.postValue(emptyList())
            }
        }
    }
    fun searchProducts(keyword: String) {
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true) // Bật loading

            val result = repository.searchProducts(keyword)

            result.onSuccess { searchedList ->
                _products.postValue(searchedList)
                _errorMessage.postValue(null)
            }.onFailure { exception ->
                _errorMessage.postValue("Lỗi tìm kiếm: ${exception.message}")
            }

            _isLoading.postValue(false) // Tắt loading
        }
    }
    fun filterByCategory(slug: String) {
        if (slug.isEmpty()) {
            loadData()
            return
        }
        viewModelScope.launch(Dispatchers.IO) {
            _isLoading.postValue(true)

            val result = repository.getProductsByCategory(slug)
            result.onSuccess { filteredList ->
                _products.postValue(filteredList)
                _errorMessage.postValue(null)
            }.onFailure { exception ->
                _products.postValue(emptyList())
                _errorMessage.postValue("Lỗi: ${exception.message}")
            }

            _isLoading.postValue(false)
        }
    }
    fun toggleFavorite(productId: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(productId, isFavorite)
            loadFavoriteIds()
        }
    }
    fun loadFavoriteIds() {
        viewModelScope.launch(Dispatchers.IO) {
            val ids = repository.getAllFavoriteIds().toSet()
            _favoriteIds.postValue(ids)
        }
    }
}