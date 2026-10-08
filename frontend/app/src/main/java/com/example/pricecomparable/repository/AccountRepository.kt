package com.example.pricecomparable.repository

import android.graphics.Bitmap
import android.util.Base64
import android.util.Log
import java.io.ByteArrayOutputStream
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.example.pricecomparable.network.ApiClient
import com.example.pricecomparable.network.UserResponse
import com.example.pricecomparable.network.UpdateUserPictureBody

class AccountRepository {

    private val api = ApiClient.api
    private val TAG = "AccountRepository"

    suspend fun getCurrentUser(): UserResponse = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Fetching current user...")
            val user = api.getCurrentUser()
            Log.d(TAG, "User fetched successfully: ${user.account_email}")
            Log.d(TAG, "User has picture: ${user.picture != null}")
            user
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP Error fetching current user: ${e.code()} - ${e.message()}")
            Log.e(TAG, "Response body: ${e.response()?.errorBody()?.string()}")
            e.printStackTrace()
            throw e
        } catch (e: Exception) {
            Log.e(TAG, "Error fetching current user: ${e.message}", e)
            e.printStackTrace()
            throw e
        }
    }

    suspend fun uploadProfilePhoto(bitmap: Bitmap) = withContext(Dispatchers.IO) {
        try {
            Log.d(TAG, "Starting photo upload...")
            // Convert bitmap to Base64
            val outputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            val base64String = Base64.encodeToString(outputStream.toByteArray(), Base64.DEFAULT)
            
            Log.d(TAG, "Base64 string length: ${base64String.length}")
            
            // Add data URI prefix for base64 image
            val base64Image = "data:image/jpeg;base64,$base64String"
            
            // Upload to backend
            Log.d(TAG, "Sending update request to backend...")
            Log.d(TAG, "Base64 image preview: ${base64Image.take(100)}...")
            Log.d(TAG, "Full URL should be: https://group-repository-2025-android-6-tkh0.onrender.com/account/me")
            
            val response = api.updateCurrentUser(UpdateUserPictureBody(picture = base64Image))
            Log.d(TAG, "Photo uploaded successfully. Response code: ${response.code()}")
            
            if (!response.isSuccessful) {
                val errorBody = response.errorBody()?.string()
                Log.e(TAG, "Upload failed! Code: ${response.code()}, Error: $errorBody")
                throw Exception("Upload failed with code ${response.code()}: $errorBody")
            }
            
            // Verify the update by fetching user data
            Log.d(TAG, "Verifying update by fetching user data...")
            val updatedUser = api.getCurrentUser()
            if (updatedUser.picture != null) {
                Log.d(TAG, "SUCCESS: Picture is now in database! Length: ${updatedUser.picture.length}")
            } else {
                Log.w(TAG, "WARNING: Picture not found after update!")
            }
        } catch (e: retrofit2.HttpException) {
            Log.e(TAG, "HTTP Error uploading photo: ${e.code()} - ${e.message()}")
            val errorBodyString = e.response()?.errorBody()?.string()
            Log.e(TAG, "Response body: $errorBodyString")
            Log.e(TAG, "Request URL: ${e.response()?.raw()?.request?.url}")
            Log.e(TAG, "Request method: ${e.response()?.raw()?.request?.method}")
            e.printStackTrace()
            
            // More specific error message
            val errorMsg = when (e.code()) {
                404 -> "Endpoint not found. Check if URL is correct: ${e.response()?.raw()?.request?.url}"
                401, 403 -> "Authentication failed. Token may be expired or invalid."
                else -> "Failed to upload photo: HTTP ${e.code()} - ${e.message()}"
            }
            throw Exception(errorMsg)
        } catch (e: Exception) {
            Log.e(TAG, "Error uploading photo: ${e.message}", e)
            e.printStackTrace()
            throw e
        }
    }
}
