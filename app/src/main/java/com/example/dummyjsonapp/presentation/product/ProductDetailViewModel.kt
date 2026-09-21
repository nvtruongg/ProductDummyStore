package com.example.dummyjsonapp.presentation.product

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.domain.model.ProductModel
import com.example.dummyjsonapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductDetailViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel() {
    private val _selectedProduct = MutableLiveData<ProductModel?>()
    val selectedProduct: LiveData<ProductModel?> = _selectedProduct

    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage


    fun loadProductDetail(productId: Int)  {
        viewModelScope.launch(Dispatchers.IO) {
            val product = repository.getProductById(productId)
            _selectedProduct.postValue(product)
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
}