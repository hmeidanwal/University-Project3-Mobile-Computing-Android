package com.example.pricecomparable.network

import retrofit2.Response
import retrofit2.http.*

data class StoreOwnerName(
    val full_name: String,
    val email: String
)

data class Store(
    val id: Int,
    val picture: String?,
    val country: String?,
    val city: String?,
    val postal_code: String?,
    val street_name: String?,
    val street_number: String?,
    val account_email: String?,
    val full_name: String? = null
)






interface StoreApiService {

    @GET("account/storeOwnerNames")
    suspend fun getAllOwnerNames(): Response<List<StoreOwnerName>>

    @GET("store-owners")
    suspend fun getAllStores(): Response<List<Store>>

    @GET("stores/{storeId}")
    suspend fun getStoreById(
        @Path("storeId") storeId: Int
    ): Response<Store>

}