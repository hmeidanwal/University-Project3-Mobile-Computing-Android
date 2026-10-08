package com.example.pricecomparable.viewmodel

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecomparable.repository.SearchRepository
import com.example.pricecomparable.model.Product
import kotlinx.coroutines.launch

class SearchViewModel : ViewModel() {

    private val repo = SearchRepository()

    val searchQuery = mutableStateOf("")
    val products = mutableStateOf<List<Product>>(emptyList())

    init {
        loadAllProducts()
    }

    fun updateSearchQuery(value: String) {
        searchQuery.value = value
        searchProducts(value)
    }

    private fun loadAllProducts() {
        viewModelScope.launch {
            try {
                products.value = repo.getAllProducts()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun searchProducts(query: String) {
        viewModelScope.launch {
            try {
                val result = if (query.isBlank()) repo.getAllProducts()
                else repo.searchProducts(query)

                products.value = result
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
