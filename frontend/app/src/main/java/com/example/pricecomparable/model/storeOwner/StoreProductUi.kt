package com.example.pricecomparable.model.storeOwner

data class StoreProductUi(
    val id: Int? = null,
    val name: String,
    val amount: String,
    val unit: String,
    val price: Double,
    val image: String? = null,
    val storeId: Int
)