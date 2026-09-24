package com.example.dummyjsonapp.presentation.checkout

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

data class CheckoutUiState(
    val cartItems: List<CartItemModel> = emptyList(),
    val subTotal: Double = 0.0,
    val shippingFee: Double = 2.00,
    val finalTotal: Double = 0.0
)
@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {
    private val _uiState = MutableStateFlow(CheckoutUiState())
    val uiState : StateFlow<CheckoutUiState> = _uiState.asStateFlow()

    fun placeOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            //TODO: phát triển bảng OrderEntity,insert thông tin đơn hàng vào đây
            repository.clearCart()
            loadCart()
        }
    }
    fun loadCart() {
        viewModelScope.launch() {
            val items = repository.getCartItems()
            val total = items.sumOf { it.product.price * it.quantity }
            val shipfee = _uiState.value.shippingFee
            val finalTotal = if(items.isNotEmpty()) total + shipfee else 0.0
            _uiState.update { it.copy(cartItems = items, subTotal = total, finalTotal = finalTotal) }
        }
    }
}