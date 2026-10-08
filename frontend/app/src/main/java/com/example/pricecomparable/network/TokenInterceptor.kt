package com.example.pricecomparable.network

import com.example.pricecomparable.auth.TokenManager
import okhttp3.Interceptor
import okhttp3.Response

class TokenInterceptor(private val tokenManager: TokenManager) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val token = tokenManager.getToken()
        val originalRequest = chain.request()
        
        android.util.Log.d("TokenInterceptor", "Request URL: ${originalRequest.url}")
        android.util.Log.d("TokenInterceptor", "Token exists: ${token != null}")
        
        val request = if (token != null) {
            originalRequest.newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            android.util.Log.w("TokenInterceptor", "No token available for request!")
            originalRequest
        }
        
        val response = chain.proceed(request)
        android.util.Log.d("TokenInterceptor", "Response code: ${response.code}")
        
        return response
    }
}

