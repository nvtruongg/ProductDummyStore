package com.example.dummyjsonapp.presentation.cart

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.domain.model.CartItemModel
import com.example.dummyjsonapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartUiState(
    val isLoading: Boolean = false,
    val items: List<CartItemModel> = emptyList(),
    val totalPrice: Double = 0.0,
    val errorMessage: String? = null
)
@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    private val _uiState = MutableStateFlow(CartUiState())
    val uiState: StateFlow<CartUiState> = _uiState.asStateFlow()

    init {
        loadCart()
    }
    fun loadCart() {
        viewModelScope.launch() {
            _uiState.update { it.copy(isLoading = true)  }
            val items = repository.getCartItems()
            val totalPrice = items.sumOf { it.product.price * it.quantity  }
            _uiState.update { it.copy(isLoading = false, items = items, totalPrice = totalPrice) }
        }
    }

    fun updateCartQuantity(productId: Int, newQuantity: Int) {
        viewModelScope.launch() {
            val success = repository.updateCartQuantity(productId, newQuantity)
            if(!success){
                _uiState.update { it.copy(errorMessage = "Số lượng đã hết!") }
            }
            loadCart()
        }
    }

    fun removeCartItem(productId: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.removeFromCart(productId)
            loadCart()
        }
    }

    fun clearMessage() {
        _uiState.update { it.copy(errorMessage = null) }
    }
}