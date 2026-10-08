package com.example.pricecomparable.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pricecomparable.auth.TokenManager
import com.example.pricecomparable.model.LoginUiState
import com.example.pricecomparable.network.ApiClient
import com.example.pricecomparable.network.LoginBody
import com.example.pricecomparable.network.SignupBody
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class LoginViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState

    // ===== EVENT HANDLERS (called from View) =====

    fun onEmailChange(email: String) {
        _uiState.update { it.copy(email = email) }
    }

    fun onPasswordChange(password: String) {
        _uiState.update { it.copy(password = password) }
    }

    fun onFullNameChange(fullName: String) {
        _uiState.update { it.copy(fullName = fullName) }
    }

    fun onStoreOwnerChange(isStoreOwner: Boolean) {
        _uiState.update { it.copy(isStoreOwner = isStoreOwner) }
    }

    fun onCountryChange(country: String) {
        _uiState.update { it.copy(country = country) }
    }

    fun onCityChange(city: String) {
        _uiState.update { it.copy(city = city) }
    }

    fun onPostalCodeChange(postalCode: String) {
        _uiState.update { it.copy(postalCode = postalCode) }
    }

    fun onStreetNameChange(streetName: String) {
        _uiState.update { it.copy(streetName = streetName) }
    }

    fun onStreetNumberChange(streetNumber: String) {
        _uiState.update { it.copy(streetNumber = streetNumber) }
    }

    fun onTogglePasswordVisibility() {
        _uiState.update { it.copy(showPassword = !it.showPassword) }
    }

    fun onToggleSignUpMode() {
        _uiState.update { it.copy(signUpMode = !it.signUpMode) }
    }

    fun onDismissSuccessDialog() {
        _uiState.update {
            it.copy(
                showSuccessDialog = false,
                signUpMode = false,
                email = "",
                password = "",
                fullName = "",
                isStoreOwner = false,
                country = "",
                city = "",
                postalCode = "",
                streetName = "",
                streetNumber = ""
            )
        }
    }

    // ===== BUSINESS LOGIC =====

    fun submitForm(tokenManager: TokenManager) {
        val state = _uiState.value
        _uiState.update { it.copy(errorMessage = null, isLoading = true) }

        viewModelScope.launch {
            if (state.signUpMode) {
                performSignUp()
            } else {
                performLogin(tokenManager)
            }
        }
    }

    private suspend fun performSignUp() {
        try {
            val state = _uiState.value
            val fcmToken = FirebaseMessaging.getInstance().token.await()
            val response = ApiClient.api.signup(
                SignupBody(
                    email = state.email,
                    password = state.password,
                    fcmToken = fcmToken,
                    fullName = state.fullName,
                    isStoreOwner = state.isStoreOwner,
                    country = state.country.ifBlank { null },
                    city = state.city.ifBlank { null },
                    postalCode = state.postalCode.ifBlank { null },
                    streetName = state.streetName.ifBlank { null },
                    streetNumber = state.streetNumber.ifBlank { null }
                )
            )
            if (response.success) {
                _uiState.update { it.copy(showSuccessDialog = true, isLoading = false) }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(errorMessage = e.message ?: "Sign Up failed", isLoading = false)
            }
        }
    }

    private suspend fun performLogin(tokenManager: TokenManager) {
        try {
            val state = _uiState.value
            val response = ApiClient.api.login(LoginBody(state.email, state.password))
            tokenManager.saveToken(response.token)

            // Fetch user info to get the role
        try {
            val userInfo = ApiClient.api.getCurrentUser()
            tokenManager.saveRole(userInfo.role ?: "customer")
        } catch (e: Exception) {
            // If fetching user info fails, default to customer
            tokenManager.saveRole("customer")
        }  
            _uiState.update { it.copy(loginSuccess = true, isLoading = false) }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(errorMessage = e.message ?: "Login failed", isLoading = false)
            }
        }
    }
}

