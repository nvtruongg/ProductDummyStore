package com.example.dummyjsonapp.presentation.favotite

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.dummyjsonapp.data.local.entity.ProductEntity
import com.example.dummyjsonapp.domain.repository.ProductRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavoriteViewModel @Inject constructor(
    private val repository: ProductRepository
): ViewModel(){
    private val _favoriteIds = MutableLiveData<Set<Int>>(emptySet())
    val favoriteIds: LiveData<Set<Int>> = _favoriteIds

    private val _favoriteProductsList = MutableLiveData<List<ProductEntity>>()
    val favoriteProductsList: LiveData<List<ProductEntity>> = _favoriteProductsList

    init {
        loadFavoriteIds()
    }

    fun loadFavoriteIds() {
        viewModelScope.launch(Dispatchers.IO) {
            val ids = repository.getAllFavoriteIds().toSet()
            _favoriteIds.postValue(ids)
        }
    }
    fun loadFavoriteProductsList() {
        viewModelScope.launch(Dispatchers.IO) {
            val result = repository.getFavoriteProducts()
            if (result.isSuccess) {
                _favoriteProductsList.postValue(result.getOrDefault(emptyList()))
            }
        }
    }
    fun toggleFavorite(productId: Int, isFavorite: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.toggleFavorite(productId, isFavorite)
            loadFavoriteIds()
            loadFavoriteProductsList()
        }
    }
}