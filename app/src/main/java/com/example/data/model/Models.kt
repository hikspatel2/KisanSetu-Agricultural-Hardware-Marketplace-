package com.example.data.model

import com.squareup.moshi.JsonClass

enum class UserRole {
    FARMER,
    SELLER
}

enum class SellerStatus {
    PENDING,
    APPROVED,
    REJECTED,
    SUSPENDED
}

enum class OrderStatus {
    PLACED,
    SEARCHING_SELLER,
    SELLER_ASSIGNED,
    ACCEPTED,
    PREPARING,
    READY_FOR_DELIVERY,
    OUT_FOR_DELIVERY,
    DELIVERED,
    CANCELLED,
    REJECTED,
    FAILED
}

enum class PaymentMethod {
    COD,
    ONLINE_UPI,
    ONLINE_CARD
}

enum class PaymentStatus {
    PENDING,
    AUTHORIZED,
    PAID,
    FAILED,
    REFUNDED
}

@JsonClass(generateAdapter = true)
data class User(
    val id: String,
    val name: String,
    val phone: String,
    val role: UserRole,
    val email: String? = null,
    val address: String = "",
    val city: String = "",
    val pincode: String = "",
    val latitude: Double = 23.0225, // Default agricultural hub e.g., Gujarat
    val longitude: Double = 72.5714,
    val token: String = "token_${System.currentTimeMillis()}"
)

@JsonClass(generateAdapter = true)
data class SellerProfile(
    val id: String,
    val userId: String,
    val ownerName: String,
    val shopName: String,
    val shopAddress: String,
    val city: String,
    val state: String,
    val pincode: String,
    val latitude: Double,
    val longitude: Double,
    val shopImageUrl: String = "",
    val gstNumber: String = "",
    val bankAccount: String = "",
    val ifscCode: String = "",
    val status: SellerStatus = SellerStatus.PENDING,
    val rating: Double = 4.8,
    val totalRatings: Int = 12,
    val isAvailable: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class Category(
    val id: String,
    val name: String,
    val iconName: String,
    val subcategories: List<String>
)

@JsonClass(generateAdapter = true)
data class Product(
    val id: String,
    val sellerId: String,
    val sellerName: String,
    val name: String,
    val category: String,
    val subcategory: String,
    val brand: String,
    val sku: String,
    val description: String,
    val imageUrl: String,
    val price: Double,
    val discountPrice: Double? = null,
    val unit: String, // e.g. "meter", "piece", "bundle", "set", "hp"
    val stockQuantity: Int,
    val minOrderQuantity: Int = 1,
    val isAvailable: Boolean = true,
    val rating: Double = 4.5,
    val totalRatings: Int = 8,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class CartItem(
    val product: Product,
    val quantity: Int
) {
    val effectivePrice: Double
        get() = product.discountPrice ?: product.price

    val subtotal: Double
        get() = effectivePrice * quantity
}

@JsonClass(generateAdapter = true)
data class OrderItem(
    val productId: String,
    val productName: String,
    val brand: String,
    val unit: String,
    val price: Double,
    val quantity: Int,
    val total: Double,
    val imageUrl: String
)

@JsonClass(generateAdapter = true)
data class OrderTimelineEntry(
    val status: OrderStatus,
    val timestamp: Long,
    val note: String
)

@JsonClass(generateAdapter = true)
data class Order(
    val id: String,
    val orderNumber: String,
    val farmerId: String,
    val farmerName: String,
    val farmerPhone: String,
    val deliveryAddress: String,
    val deliveryLat: Double,
    val deliveryLng: Double,
    val sellerId: String? = null,
    val sellerName: String = "",
    val items: List<OrderItem>,
    val subtotal: Double,
    val discount: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val grandTotal: Double,
    val status: OrderStatus,
    val paymentMethod: PaymentMethod,
    val paymentStatus: PaymentStatus,
    val deliveryOtp: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val estimatedDeliveryTime: String = "Within 2-4 Hours",
    val timeline: List<OrderTimelineEntry> = emptyList()
)

@JsonClass(generateAdapter = true)
data class OrderRequest(
    val id: String,
    val orderId: String,
    val sellerId: String,
    val orderNumber: String,
    val orderAmount: Double,
    val itemCount: Int,
    val summaryText: String,
    val farmerArea: String,
    val distanceKm: Double,
    val status: String, // PENDING, ACCEPTED, EXPIRED, REJECTED
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class Review(
    val id: String,
    val orderId: String,
    val sellerId: String,
    val farmerId: String,
    val farmerName: String,
    val rating: Int,
    val comment: String,
    val createdAt: Long = System.currentTimeMillis()
)

@JsonClass(generateAdapter = true)
data class AppNotification(
    val id: String,
    val userId: String,
    val title: String,
    val message: String,
    val type: String, // ORDER_REQUEST, ORDER_UPDATE, SYSTEM
    val orderId: String? = null,
    val timestamp: Long = System.currentTimeMillis(),
    val isRead: Boolean = false
)

@JsonClass(generateAdapter = true)
data class AppSettings(
    val deliveryRadiusKm: Double = 15.0,
    val platformCommissionPercent: Double = 2.0,
    val minOrderAmountForFreeDelivery: Double = 2000.0,
    val baseDeliveryCharge: Double = 60.0,
    val codAvailable: Boolean = true,
    val supportPhone: String = "+91 98765 43210"
)
