package com.example.pricecomparable.model

data class Product(
    val name: String,
    val description: String,
    val storeName: String,
    val price: Double,
    val image: String? = null
)
