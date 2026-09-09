package com.example.dummyjsonapp.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.api.RetrofitClient
import com.example.dummyjsonapp.db.AppDatabase
import com.example.dummyjsonapp.model.Product
import com.example.dummyjsonapp.repository.ProductRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ProductViewModel(application: Application) : AndroidViewModel(application) {
    private val repository : ProductRepository
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
        val productDao = AppDatabase.getDatabase(application).productDao()
        repository = ProductRepository(RetrofitClient.apiService, productDao)

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