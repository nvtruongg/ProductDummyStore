package com.example.dummyjsonapp.presentation.product

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.domain.repository.ProductRepository
import com.example.dummyjsonapp.presentation.UiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(UiState())
    val uiState : StateFlow<UiState> = _uiState.asStateFlow()

    fun loadProductDetail(productId: Int)  {
        viewModelScope.launch() {
            _uiState.update { it.copy(isLoading = true) }
            val product = repository.getProductById(productId)
            _uiState.update { it.copy(isLoading = false, product = product) }
        }
    }
    fun addToCart(productId: Int) {
        viewModelScope.launch() {
            val success = repository.addToCart(productId)
            val massage = if(success) "Đã thêm vào giỏ hàng!" else "Số lượng đã hết!"
            _uiState.update { it.copy(errorMessage = massage) }
        }
    }
    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}