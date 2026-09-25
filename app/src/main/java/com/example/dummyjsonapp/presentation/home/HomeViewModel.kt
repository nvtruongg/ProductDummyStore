package com.example.dummyjsonapp.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.domain.model.CategoryModel
import com.example.dummyjsonapp.domain.model.ProductModel
import com.example.dummyjsonapp.domain.repository.ProductRepository
import com.example.dummyjsonapp.domain.result.Resource
import com.example.dummyjsonapp.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor (
    private val repository: ProductRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    private var rawProducts : List<ProductModel> = emptyList()
    private var productJob: Job? = null

    // Theo dõi ngữ cảnh danh mục/tìm kiếm hiện tại
    private var currentContextSlug: String = "ALL"

    init {
        loadData()
        loadCategories()
        loadFavoriteIds()
    }

    private fun updateProductsFavorite() {
        val currentFavorites = _uiState.value.favoriteIds
        val newProductsFav = rawProducts.map { product ->
            product.copy(isFavorite = currentFavorites.contains(product.id))
        }
        _uiState.update { it.copy(products = newProductsFav) }
    }

    fun loadData() {
        if (currentContextSlug == "ALL" && rawProducts.isNotEmpty()) return
        currentContextSlug = "ALL"
        executeProductFlow { repository.getProducts() }
    }

    fun searchProducts(keyword: String) {
        val slug = "SEARCH_$keyword"
        if (currentContextSlug == slug) return
        currentContextSlug = slug
        executeProductFlow { repository.searchProducts(keyword) }
    }

    fun filterByCategory(slug: String) {
        if (slug.isEmpty()) {
            loadData()
            return
        }
        val targetSlug = "CATEGORY_$slug"
        if (currentContextSlug == targetSlug) return
        currentContextSlug = targetSlug
        executeProductFlow { repository.getProductsByCategory(slug) }
    }

    // Hàm dùng chung cho các tác vụ lấy danh sách sản phẩm
    private fun executeProductFlow(flowProvider: suspend () -> Flow<Resource<List<ProductModel>>>) {
        productJob?.cancel()

        // Xóa danh sách cũ NGAY LẬP TỨC để tránh UI bị rối hoặc List tự cuộn lung tung
        rawProducts = emptyList()
        _uiState.update { it.copy(products = emptyList(), isLoading = true) }

        productJob = viewModelScope.launch {
            flowProvider().collect { resource ->
                when (resource) {
                    Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                        rawProducts = resource.data
                        updateProductsFavorite()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    }
                }
            }
        }
    }

    fun loadCategories() {
        viewModelScope.launch {
            repository.getCategories().collect { resource->
                when(resource){
                    is Resource.Success -> {
                        val categoryAll = CategoryModel(slug = "", name = "Tất cả", url = "")
                        val displayList = listOf(categoryAll) + resource.data
                        _uiState.update { it.copy(categories = displayList) }
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(errorMessage = resource.message) }
                    }
                    is Resource.Loading -> {}
                }
            }
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
            _uiState.update { it.copy(favoriteIds = ids) }
            updateProductsFavorite()
        }
    }

    fun clearError() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}