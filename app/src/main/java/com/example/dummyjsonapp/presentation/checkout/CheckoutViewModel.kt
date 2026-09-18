package com.example.dummyjsonapp.presentation.checkout

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.data.local.entity.CartItem
import com.example.dummyjsonapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CheckoutViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    fun placeOrder() {
        viewModelScope.launch(Dispatchers.IO) {
            //TODO: phát triển bảng OrderEntity,insert thông tin đơn hàng vào đây
            repository.clearCart()
            loadCart()
        }
    }
    fun loadCart() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.getCartItems()
            _cartItems.postValue(items)
            val total = items.sumOf { it.product.price * it.quantity }
            _totalPrice.postValue(total)
        }
    }
}