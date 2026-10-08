package com.example.pricecomparable.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecomparable.model.storeOwner.StoreOwnerProfileUiState
import com.example.pricecomparable.model.storeOwner.StoreProductUi
import com.example.pricecomparable.network.ApiClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class StoreOwnerProfileViewModel() : ViewModel() {

    private val apiService = ApiClient.api

    private val _uiState = MutableStateFlow(StoreOwnerProfileUiState())
    val uiState: StateFlow<StoreOwnerProfileUiState> = _uiState.asStateFlow()

    init {
        loadStoreOwnerInfo()
        loadProducts()
    }

    // LOAD STORE OWNER INFO
    private fun loadStoreOwnerInfo() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val userInfo = ApiClient.api.getCurrentUser()
                val storeName = userInfo.full_name ?: "Store name"
                val storeOwnerId = userInfo.id ?: 1
                
                // Build address from store_info
                val storeInfo = userInfo.store_info
                val address = if (storeInfo != null) {
                    buildString {
                        storeInfo.street_name?.let { append(it) }
                        storeInfo.street_number?.let { append(" $it") }
                        if (isNotEmpty()) append(", ")
                        storeInfo.city?.let { append(it) }
                        storeInfo.postal_code?.let { append(" $it") }
                    }.ifBlank { "Address" }
                } else {
                    "Address"
                }
                
                _uiState.update { 
                    it.copy(
                        storeName = storeName,
                        storeAddress = address,
                        storeOwnerId = storeOwnerId,
                        isLoading = false
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        storeName = "Store name",
                        storeAddress = "Address",
                        storeOwnerId = 1, // FALLBACK ID
                        isLoading = false
                    ) 
                }
            }
        }
    }

    private fun loadProducts() {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                val products = apiService.getStoreProducts()
                _uiState.update { 
                    it.copy(
                        products = products,
                        isLoading = false,
                        error = null
                    ) 
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Unknown error",
                        isLoading = false
                    ) 
                }
            }
        }
    }

    fun onSearchQueryChange(query: String) {
        _uiState.update { it.copy(searchQuery = query) }
    }

    fun onAddProductClick() {
        _uiState.update { 
            it.copy(
                isProductDialogOpen = true,
                editingProduct = null
            ) 
        }
    }

    fun onEditProductClick(product: StoreProductUi) {
        _uiState.update { 
            it.copy(
                isProductDialogOpen = true,
                editingProduct = product
            ) 
        }
    }

    fun onDeleteProductClick(product: StoreProductUi) {
        viewModelScope.launch {
            try {
                product.id?.let { id ->
                    apiService.deleteProduct(id)
                    _uiState.update { state ->
                        state.copy(
                            products = state.products.filterNot { it.id == id }
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(error = e.message ?: "Failed to delete product") 
                }
            }
        }
    }

    fun onDismissProductDialog() {
        _uiState.update { 
            it.copy(
                isProductDialogOpen = false,
                editingProduct = null
            ) 
        }
    }

    fun onSaveProduct(
        name: String,
        amount: String,
        unit: String,
        price: String,
        discount: String,
        discountStart: String,
        discountEnd: String
    ) {
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true) }
                
                val priceValue = parsePrice(price)
                val editing = _uiState.value.editingProduct
                val productUi = StoreProductUi(
                    id = editing?.id,
                    name = name,
                    amount = amount,
                    unit = unit,
                    price = priceValue,
                    image = editing?.image,
                    storeId = _uiState.value.storeOwnerId
                )
                
                if (editing == null) {
                    val response = apiService.createProduct(productUi)
                    _uiState.update { state ->
                        state.copy(
                            products = state.products + response,
                            isProductDialogOpen = false,
                            isLoading = false
                        )
                    }
                } else {
                    apiService.updateProduct(editing.id!!, productUi)
                    _uiState.update { state ->
                        state.copy(
                            products = state.products.map { p ->
                                if (p.id == editing.id) productUi else p
                            },
                            isProductDialogOpen = false,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { 
                    it.copy(
                        error = e.message ?: "Failed to save product",
                        isLoading = false
                    ) 
                }
            }
        }
    }

    private fun parsePrice(priceString: String): Double {
        return priceString
            .replace("€", "")
            .replace(" ", "")
            .replace(",", ".")
            .toDoubleOrNull() ?: 0.0
    }
}