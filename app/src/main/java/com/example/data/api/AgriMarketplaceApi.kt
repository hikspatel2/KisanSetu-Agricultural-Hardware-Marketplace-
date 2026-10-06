package com.example.data.api

import com.example.data.model.*
import com.squareup.moshi.JsonClass
import retrofit2.Response
import retrofit2.http.*

@JsonClass(generateAdapter = true)
data class LoginRequest(
    val phone: String,
    val role: UserRole
)

@JsonClass(generateAdapter = true)
data class VerifyOtpRequest(
    val phone: String,
    val otp: String,
    val role: UserRole
)

@JsonClass(generateAdapter = true)
data class AuthResponse(
    val success: Boolean,
    val message: String,
    val user: User?,
    val sellerProfile: SellerProfile? = null
)

@JsonClass(generateAdapter = true)
data class SellerRegistrationRequest(
    val userId: String,
    val ownerName: String,
    val phone: String,
    val shopName: String,
    val shopAddress: String,
    val city: String,
    val state: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    val gstNumber: String = "",
    val bankAccount: String = "",
    val ifscCode: String = "",
    val shopImageUrl: String = ""
)

@JsonClass(generateAdapter = true)
data class CreateOrderRequest(
    val farmerId: String,
    val farmerName: String,
    val farmerPhone: String,
    val deliveryAddress: String,
    val deliveryLat: Double,
    val deliveryLng: Double,
    val items: List<OrderItem>,
    val paymentMethod: PaymentMethod
)

@JsonClass(generateAdapter = true)
data class AcceptOrderRequest(
    val orderId: String,
    val sellerId: String
)

@JsonClass(generateAdapter = true)
data class UpdateOrderStatusRequest(
    val orderId: String,
    val sellerId: String,
    val status: OrderStatus
)

@JsonClass(generateAdapter = true)
data class VerifyDeliveryOtpRequest(
    val orderId: String,
    val sellerId: String,
    val otp: String
)

@JsonClass(generateAdapter = true)
data class AddProductRequest(
    val sellerId: String,
    val name: String,
    val category: String,
    val subcategory: String,
    val brand: String,
    val sku: String,
    val description: String,
    val price: Double,
    val discountPrice: Double?,
    val unit: String,
    val stockQuantity: Int,
    val minOrderQuantity: Int,
    val imageUrl: String
)

@JsonClass(generateAdapter = true)
data class UpdateStockRequest(
    val productId: String,
    val sellerId: String,
    val newStockQuantity: Int
)

@JsonClass(generateAdapter = true)
data class SubmitReviewRequest(
    val orderId: String,
    val sellerId: String,
    val farmerId: String,
    val farmerName: String,
    val rating: Int,
    val comment: String
)

@JsonClass(generateAdapter = true)
data class ApiResponse<T>(
    val success: Boolean,
    val message: String,
    val data: T? = null
)

interface AgriMarketplaceApi {
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<ApiResponse<String>>

    @POST("auth/verify-otp")
    suspend fun verifyOtp(@Body request: VerifyOtpRequest): Response<AuthResponse>

    @POST("sellers/register")
    suspend fun registerSeller(@Body request: SellerRegistrationRequest): Response<ApiResponse<SellerProfile>>

    @GET("sellers/{id}")
    suspend fun getSellerProfile(@Path("id") sellerId: String): Response<ApiResponse<SellerProfile>>

    @GET("shops/nearby")
    suspend fun getNearbyShops(
        @Query("lat") lat: Double,
        @Query("lng") lng: Double,
        @Query("radius") radius: Double?
    ): Response<ApiResponse<List<SellerProfile>>>

    @GET("categories")
    suspend fun getCategories(): Response<ApiResponse<List<Category>>>

    @GET("products")
    suspend fun getProducts(
        @Query("category") category: String?,
        @Query("search") search: String?,
        @Query("sellerId") sellerId: String?
    ): Response<ApiResponse<List<Product>>>

    @POST("products")
    suspend fun addProduct(@Body request: AddProductRequest): Response<ApiResponse<Product>>

    @PUT("inventory/{productId}")
    suspend fun updateStock(
        @Path("productId") productId: String,
        @Body request: UpdateStockRequest
    ): Response<ApiResponse<Product>>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<ApiResponse<Order>>

    @GET("orders/farmer/{farmerId}")
    suspend fun getFarmerOrders(@Path("farmerId") farmerId: String): Response<ApiResponse<List<Order>>>

    @GET("orders/seller/{sellerId}")
    suspend fun getSellerOrders(@Path("sellerId") sellerId: String): Response<ApiResponse<List<Order>>>

    @GET("orders/requests/{sellerId}")
    suspend fun getSellerOrderRequests(@Path("sellerId") sellerId: String): Response<ApiResponse<List<OrderRequest>>>

    @POST("orders/accept")
    suspend fun acceptOrder(@Body request: AcceptOrderRequest): Response<ApiResponse<Order>>

    @PUT("orders/{orderId}/status")
    suspend fun updateOrderStatus(
        @Path("orderId") orderId: String,
        @Body request: UpdateOrderStatusRequest
    ): Response<ApiResponse<Order>>

    @POST("delivery/verify-otp")
    suspend fun verifyDeliveryOtp(@Body request: VerifyDeliveryOtpRequest): Response<ApiResponse<Order>>

    @POST("reviews")
    suspend fun submitReview(@Body request: SubmitReviewRequest): Response<ApiResponse<Review>>

    @GET("notifications/{userId}")
    suspend fun getNotifications(@Path("userId") userId: String): Response<ApiResponse<List<AppNotification>>>

    @GET("settings")
    suspend fun getSettings(): Response<ApiResponse<AppSettings>>
}
