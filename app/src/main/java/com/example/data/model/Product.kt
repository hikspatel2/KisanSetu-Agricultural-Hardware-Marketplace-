package com.example.data.model

import com.squareup.moshi.JsonClass
import java.util.UUID

/**
 * Data model representing agricultural equipment and farm hardware products.
 * Includes equipment name, price, images, specifications, and availability details.
 */
@JsonClass(generateAdapter = true)
data class Product(
    val id: String = UUID.randomUUID().toString(),
    val sellerId: String = "seller_default",
    val sellerName: String = "Agri Equipment Spares",
    val name: String,
    val category: String = "Farm Equipment",
    val subcategory: String = "Agricultural Equipment",
    val brand: String = "AgroTech",
    val sku: String = "EQP-${System.currentTimeMillis()}",
    val description: String = "",
    val imageUrl: String = "",
    val price: Double,
    val discountPrice: Double? = null,
    val unit: String = "piece", // e.g. "meter", "piece", "bundle", "set", "hp"
    val stockQuantity: Int = 10,
    val minOrderQuantity: Int = 1,
    val isAvailable: Boolean = true,
    val rating: Double = 4.5,
    val totalRatings: Int = 8,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    /**
     * Secondary convenience constructor for simple agricultural equipment definitions.
     */
    constructor(
        id: String = UUID.randomUUID().toString(),
        name: String,
        price: Double,
        imageUrl: String = "",
        category: String = "Farm Equipment",
        description: String = "",
        brand: String = "AgroTech"
    ) : this(
        id = id,
        sellerId = "seller_default",
        sellerName = "Agri Equipment Spares",
        name = name,
        category = category,
        subcategory = "Agricultural Equipment",
        brand = brand,
        sku = "EQP-${System.currentTimeMillis()}",
        description = description,
        imageUrl = imageUrl,
        price = price,
        discountPrice = null,
        unit = "piece",
        stockQuantity = 10,
        minOrderQuantity = 1,
        isAvailable = true,
        rating = 4.5,
        totalRatings = 8,
        createdAt = System.currentTimeMillis(),
        updatedAt = System.currentTimeMillis()
    )

    /**
     * Alias property for imageUrl to support access via 'image'.
     */
    val image: String
        get() = imageUrl

    /**
     * Calculated effective price taking any active discount into account.
     */
    val effectivePrice: Double
        get() = discountPrice ?: price
}
