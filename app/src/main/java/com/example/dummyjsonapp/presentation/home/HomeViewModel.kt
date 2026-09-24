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
        productJob?.cancel()
        productJob = viewModelScope.launch {
            repository.getProducts().collect { resource ->
                when (resource) {

                    Resource.Loading -> {
                        _uiState.update {
                            it.copy(isLoading = true)
                        }
                    }

                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = null) }
                        rawProducts = resource.data
                        updateProductsFavorite()
                    }

                    is Resource.Error -> {
                        _uiState.update {
                            it.copy(
                                isLoading = false,
                                errorMessage = resource.message
                            )
                        }
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
    fun searchProducts(keyword: String) {
        productJob?.cancel()
        productJob = viewModelScope.launch {
            repository.searchProducts(keyword).collect { resource ->
                when(resource){
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false)}
                        rawProducts = resource.data
                        updateProductsFavorite()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
                }
            }
        }
    }
    fun filterByCategory(slug: String) {
        productJob?.cancel()
        if (slug.isEmpty()) {
            loadData()
            return
        }
        productJob = viewModelScope.launch {
            repository.getProductsByCategory(slug).collect { resource ->
                when(resource){
                    is Resource.Success -> {
                        _uiState.update { it.copy(isLoading = false) }
                        rawProducts = resource.data
                        updateProductsFavorite()
                    }
                    is Resource.Error -> {
                        _uiState.update { it.copy(isLoading = false, errorMessage = resource.message) }
                    }
                    is Resource.Loading -> {
                        _uiState.update { it.copy(isLoading = true) }
                    }
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