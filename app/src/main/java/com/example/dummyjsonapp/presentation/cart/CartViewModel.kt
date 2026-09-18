package com.example.dummyjsonapp.presentation.cart

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.data.local.entity.CartItem
import com.example.dummyjsonapp.data.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val repository: ProductRepository
) : ViewModel() {
    private val _cartItems = MutableLiveData<List<CartItem>>()
    val cartItems: LiveData<List<CartItem>> = _cartItems

    private val _totalPrice = MutableLiveData<Double>()
    val totalPrice: LiveData<Double> = _totalPrice

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadCart()
    }
    fun loadCart() {
        viewModelScope.launch(Dispatchers.IO) {
            val items = repository.getCartItems()
            _cartItems.postValue(items)

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
            loadCart()
        }
    }
}