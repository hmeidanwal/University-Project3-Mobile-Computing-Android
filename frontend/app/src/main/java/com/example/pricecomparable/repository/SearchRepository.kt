package com.example.pricecomparable.repository

import com.example.pricecomparable.network.ApiClient
import com.example.pricecomparable.model.Product
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class SearchRepository {

    private val api = ApiClient.api

    suspend fun getAllProducts(): List<Product> {
        return api.getAllProducts()
    }

    suspend fun searchProducts(query: String): List<Product> {
        return api.searchProducts(query)
    }
}
