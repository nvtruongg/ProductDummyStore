package com.example.dummyjsonapp.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.model.Product
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

    // LiveData quản lý trạng thái  (Loading)
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    // LiveData lưu thông báo lỗi nếu có
    private val _errorMessage = MutableLiveData<String?>()
    val errorMessage: LiveData<String?> = _errorMessage

    init {
        loadData()
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
}