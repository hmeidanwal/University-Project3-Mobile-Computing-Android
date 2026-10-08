package com.example.pricecomparable.network

import okhttp3.MultipartBody
import com.example.pricecomparable.model.Product
import com.example.pricecomparable.model.storeOwner.StoreProductUi
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.DELETE


data class StoreInfo(
    val country: String?,
    val city: String?,
    val postal_code: String?,
    val street_name: String?,
    val street_number: String?
)

data class UserResponse(
    val id: Int?,
    val fcm_token: String?,
    val notifications: Boolean?,
    val picture: String?,
    val account_email: String?,
    val full_name: String?,
    val role: String?, // ADDED THIS LINE TO GET THE ROLE OF THE USER
    val store_info: StoreInfo? // ADDED THIS LINE TO GET THE STORE INFO
)

data class LoginBody(val email: String, val password: String)
data class SignupBody(
    val email: String,
    val password: String,
    val fcmToken: String,
    val fullName: String,
    val isStoreOwner: Boolean = false,
    val country: String? = null,
    val city: String? = null,
    val postalCode: String? = null,
    val streetName: String? = null,
    val streetNumber: String? = null
)
data class UpdateFcmTokenBody(val fcmToken: String)
data class LoginResponse(val token: String)
data class SignupResponse(val success: Boolean)
data class UpdateFcmTokenResponse(val success: Boolean)

data class UpdateUserPictureBody(
    val picture: String
)

interface ApiService {

    // =====================USER ENDPOINTS=========================
    // GET /account/me - Get current authenticated user
    @GET("/account/me")
    suspend fun getCurrentUser(): UserResponse

    // GET /users/{id}
    @GET("/users/{id}")
    suspend fun getUser(@Path("id") id: String): UserResponse

    // PATCH /account/me - Update current user (picture, notifications, etc.)
    @PATCH("/account/me")
    suspend fun updateCurrentUser(
        @Body body: UpdateUserPictureBody
    ): Response<Unit>

    // =====================PRODUCTS ENDPOINTS=====================
    @GET("/products")
    suspend fun getAllProducts(): List<Product>

    @GET("/products/search")
    suspend fun searchProducts(
        @Query("q") query: String
    ): List<Product>

    // ============ STORE OWNER PRODUCT MANAGEMENT ================
    
    @POST("/products")
    suspend fun createProduct(
        @Body product: StoreProductUi
    ): StoreProductUi
    
    @GET("/products")
    suspend fun getStoreProducts(
        @Query("filter") filter: String? = null
    ): List<StoreProductUi>
    
    @PATCH("/products/{id}")
    suspend fun updateProduct(
        @Path("id") id: Int,
        @Body product: StoreProductUi
    ): Response<Unit>
    
    @DELETE("/products/{id}")
    suspend fun deleteProduct(
        @Path("id") id: Int
    ): Response<Unit>


    // =====================AUTH ENDPOINTS=====================
    // POST /auth/login
    @POST("/auth/login")
    suspend fun login(@Body body: LoginBody): LoginResponse

    // POST /auth/signup
    @POST("/auth/signup")
    suspend fun signup(@Body body: SignupBody): SignupResponse

    // PATCH /account/token
    @PATCH("/account/token")
    suspend fun updateFcmToken(@Body body: UpdateFcmTokenBody): UpdateFcmTokenResponse
}
