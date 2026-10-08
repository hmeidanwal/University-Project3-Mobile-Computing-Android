package com.example.pricecomparable.viewmodel

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecomparable.repository.AccountRepository
import kotlinx.coroutines.launch

class AccountViewModel : ViewModel() {

    private val repository = AccountRepository()
    private val TAG = "AccountViewModel"

    val userName = mutableStateOf("Loading...")
    val userEmail = mutableStateOf("Loading...")
    val profilePhoto = mutableStateOf<Bitmap?>(null)
    val locationEnabled = mutableStateOf(false)
    val notificationsEnabled = mutableStateOf(true)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)
    val successMessage = mutableStateOf<String?>(null)

    init {
        loadUserData()
    }

    private fun loadUserData() {
        viewModelScope.launch {
            isLoading.value = true
            errorMessage.value = null
            try {
                Log.d(TAG, "Loading user data...")
                val user = repository.getCurrentUser()
                Log.d(TAG, "User loaded: ${user.account_email}")
                
                userEmail.value = user.account_email ?: "Unknown"
                userName.value = user.full_name ?: user.account_email?.substringBefore("@")?.replaceFirstChar { it.uppercase() } ?: "User"
                notificationsEnabled.value = user.notifications ?: false
                
                Log.d(TAG, "User email: ${userEmail.value}, Name: ${userName.value}")
                
                // Load profile photo from database if available
                user.picture?.let { pictureBase64 ->
                    try {
                        Log.d(TAG, "Loading profile photo from database...")
                        // Remove data URI prefix if present
                        val base64String = if (pictureBase64.contains(",")) {
                            pictureBase64.substringAfter(",")
                        } else {
                            pictureBase64
                        }
                        val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        if (bitmap != null) {
                            profilePhoto.value = bitmap
                            Log.d(TAG, "Profile photo loaded successfully")
                        } else {
                            Log.w(TAG, "Failed to decode bitmap from base64")
                        }
                    } catch (e: Exception) {
                        Log.e(TAG, "Error loading profile photo: ${e.message}", e)
                        e.printStackTrace()
                    }
                } ?: run {
                    Log.d(TAG, "No profile photo in database")
                }
            } catch (e: Exception) {
                Log.e(TAG, "Error loading user data: ${e.message}", e)
                e.printStackTrace()
                userName.value = "Error loading user"
                userEmail.value = ""
                errorMessage.value = "Failed to load user data: ${e.message}"
            } finally {
                isLoading.value = false
            }
        }
    }

    fun updateProfilePhoto(bitmap: Bitmap) {
        profilePhoto.value = bitmap
        // Automatically upload when photo is taken
        uploadProfilePhoto(bitmap)
    }

    fun toggleLocation(enabled: Boolean) {
        locationEnabled.value = enabled
    }

    fun toggleNotifications(enabled: Boolean) {
        notificationsEnabled.value = enabled
    }

    private fun uploadProfilePhoto(bitmap: Bitmap) {
        viewModelScope.launch {
            try {
                isLoading.value = true
                errorMessage.value = null
                successMessage.value = null
                Log.d(TAG, "Uploading profile photo...")
                repository.uploadProfilePhoto(bitmap)
                Log.d(TAG, "Photo upload successful, reloading user data...")
                successMessage.value = "Photo saved successfully!"
                // Reload user data to ensure sync
                loadUserData()
            } catch (e: Exception) {
                Log.e(TAG, "Error uploading photo: ${e.message}", e)
                e.printStackTrace()
                errorMessage.value = "Failed to upload photo: ${e.message}"
                successMessage.value = null
            } finally {
                isLoading.value = false
            }
        }
    }
}