package com.example.pricecomparable.network

import com.example.pricecomparable.auth.TokenManager
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {

    // LOCAL TESTING: Uncomment the line below to test with local backend
    // Emulator → use 10.0.2.2; real device → your PC's IP address (like 192.168.x.x)
    // private const val BASE_URL = "http://10.0.2.2:3000/" // USED FOR LOCAL TESTING
    
    // PRODUCTION: Use Render deployment
    private const val BASE_URL = "https://group-repository-2025-android-6-tkh0.onrender.com/"

    lateinit var api: ApiService
        private set

    lateinit var storeApi: StoreApiService
        private set

    fun createApi(tokenManager: TokenManager) {
        val client = OkHttpClient.Builder()
            .addInterceptor(TokenInterceptor(tokenManager))
            .build()

        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        api = retrofit.create(ApiService::class.java)
        storeApi = retrofit.create(StoreApiService::class.java)
    }
}
