package com.example.pricecomparable.model

data class LoginUiState(
    val email: String = "",
    val password: String = "",
    val fullName: String = "",
    val isStoreOwner: Boolean = false,
    val country: String = "",
    val city: String = "",
    val postalCode: String = "",
    val streetName: String = "",
    val streetNumber: String = "",
    val showPassword: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val signUpMode: Boolean = false,
    val showSuccessDialog: Boolean = false,
    val loginSuccess: Boolean = false
)

