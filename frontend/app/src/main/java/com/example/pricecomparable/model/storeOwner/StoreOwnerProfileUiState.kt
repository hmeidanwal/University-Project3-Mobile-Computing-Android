package com.example.pricecomparable.model.storeOwner

data class StoreOwnerProfileUiState(
    val products: List<StoreProductUi> = emptyList(),
    val searchQuery: String = "",
    val isProductDialogOpen: Boolean = false,
    val editingProduct: StoreProductUi? = null,
    val isLoading: Boolean = false,
    val error: String? = null, 
    val storeName: String = "",
    val storeAddress: String = "",
    val storeOwnerId: Int = 1
) {
    val filteredProducts: List<StoreProductUi>
        get() = products.filter { it.name.contains(searchQuery, ignoreCase = true) }
}