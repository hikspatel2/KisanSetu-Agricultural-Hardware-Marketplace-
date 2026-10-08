package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.data.model.SellerProfile
import com.example.data.model.SellerStatus
import com.example.ui.theme.*

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, label) = when (status) {
        OrderStatus.PLACED -> Triple(Color(0xFFE0F2FE), Color(0xFF0369A1), "Placed")
        OrderStatus.SEARCHING_SELLER -> Triple(HarvestGoldContainer, OnHarvestGoldContainer, "Searching Shop")
        OrderStatus.SELLER_ASSIGNED -> Triple(AgriGreenContainer, OnAgriGreenContainer, "Shop Assigned")
        OrderStatus.ACCEPTED -> Triple(AgriGreenContainer, OnAgriGreenContainer, "Accepted")
        OrderStatus.PREPARING -> Triple(Color(0xFFEDE9FE), Color(0xFF6D28D9), "Preparing")
        OrderStatus.READY_FOR_DELIVERY -> Triple(Color(0xFFCCFBF1), Color(0xFF0F766E), "Ready")
        OrderStatus.OUT_FOR_DELIVERY -> Triple(Color(0xFFFFEDD5), Color(0xFFC2410C), "Out for Delivery")
        OrderStatus.DELIVERED -> Triple(AgriGreenContainer, AgriGreenPrimary, "Delivered")
        OrderStatus.CANCELLED -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "Cancelled")
        OrderStatus.REJECTED -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "Declined")
        OrderStatus.FAILED -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "Failed")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(12.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.2f)),
        modifier = modifier
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        )
    }
}

@Composable
fun SellerStatusBadge(status: SellerStatus, modifier: Modifier = Modifier) {
    val (bgColor, textColor, text) = when (status) {
        SellerStatus.APPROVED -> Triple(AgriGreenContainer, AgriGreenPrimary, "VERIFIED SHOP")
        SellerStatus.PENDING -> Triple(HarvestGoldContainer, OnHarvestGoldContainer, "PENDING APPROVAL")
        SellerStatus.REJECTED -> Triple(Color(0xFFFEE2E2), Color(0xFFB91C1C), "REJECTED")
        SellerStatus.SUSPENDED -> Triple(Color(0xFFF1F5F9), Color(0xFF475569), "SUSPENDED")
    }

    Surface(
        color = bgColor,
        shape = RoundedCornerShape(8.dp),
        border = BorderStroke(1.dp, textColor.copy(alpha = 0.25f)),
        modifier = modifier
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .clip(CircleShape)
                    .background(textColor)
            )
            Spacer(modifier = Modifier.width(5.dp))
            Text(
                text = text,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

/**
 * Beautiful, high-fidelity Agricultural Product Thumbnail component.
 * Combines high-resolution online images with rich category-specific vector artwork & gradient
 * so products ALWAYS look vibrant, crisp, and hardware-themed, even if network is offline or slow!
 */
@Composable
fun ProductThumbnail(
    product: Product,
    modifier: Modifier = Modifier,
    contentScale: ContentScale = ContentScale.Crop,
    showBadges: Boolean = true
) {
    val (gradientColors, iconVector, categoryLabel) = when {
        product.category.contains("Irrigation", ignoreCase = true) -> Triple(
            listOf(Color(0xFF0284C7), Color(0xFF0D9488), Color(0xFF047857)),
            Icons.Default.WaterDrop,
            "Irrigation"
        )
        product.category.contains("Tool", ignoreCase = true) -> Triple(
            listOf(Color(0xFFEA580C), Color(0xFFF59E0B), Color(0xFFD97706)),
            Icons.Default.Build,
            "Tools"
        )
        product.category.contains("Equipment", ignoreCase = true) -> Triple(
            listOf(Color(0xFF047857), Color(0xFF065F46), Color(0xFF134E4A)),
            Icons.Default.Agriculture,
            "Equipment"
        )
        product.category.contains("Electrical", ignoreCase = true) -> Triple(
            listOf(Color(0xFF2563EB), Color(0xFF4F46E5), Color(0xFF1E3A8A)),
            Icons.Default.Bolt,
            "Electrical"
        )
        else -> Triple(
            listOf(Color(0xFF475569), Color(0xFF334155), Color(0xFF1E293B)),
            Icons.Default.Handyman,
            "Hardware"
        )
    }

    Box(
        modifier = modifier
            .background(Brush.linearGradient(gradientColors))
    ) {
        // Decorative geometric canvas backdrop for agricultural hardware feel
        Canvas(modifier = Modifier.fillMaxSize()) {
            val w = size.width
            val h = size.height

            // Concentric tech rings & waves
            drawCircle(
                color = Color.White.copy(alpha = 0.08f),
                radius = w * 0.45f,
                center = Offset(w * 0.85f, h * 0.25f),
                style = Stroke(width = 2.dp.toPx())
            )
            drawCircle(
                color = Color.White.copy(alpha = 0.05f),
                radius = w * 0.65f,
                center = Offset(w * 0.85f, h * 0.25f),
                style = Stroke(width = 3.dp.toPx())
            )

            // Bottom industrial grid line
            drawLine(
                color = Color.White.copy(alpha = 0.15f),
                start = Offset(0f, h * 0.75f),
                end = Offset(w, h * 0.75f),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Central Hardware Icon Silhouette
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Surface(
                shape = CircleShape,
                color = Color.White.copy(alpha = 0.18f),
                modifier = Modifier.size(54.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = Color.White.copy(alpha = 0.95f),
                        modifier = Modifier.size(30.dp)
                    )
                }
            }
        }

        // Web Image Loaded via Coil (layered gracefully)
        if (product.imageUrl.isNotBlank()) {
            AsyncImage(
                model = product.imageUrl,
                contentDescription = product.name,
                contentScale = contentScale,
                modifier = Modifier.fillMaxSize()
            )
        }

        // Soft gradient overlay at top & bottom for high-contrast badge reading
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.35f),
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.25f)
                        )
                    )
                )
        )

        if (showBadges) {
            // Category Tag (Top-Start)
            Surface(
                color = Color.Black.copy(alpha = 0.65f),
                shape = RoundedCornerShape(bottomEnd = 10.dp, topStart = 16.dp),
                modifier = Modifier.align(Alignment.TopStart)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Icon(
                        imageVector = iconVector,
                        contentDescription = null,
                        tint = HarvestGoldLight,
                        modifier = Modifier.size(11.dp)
                    )
                    Spacer(modifier = Modifier.width(3.dp))
                    Text(
                        text = product.subcategory.ifEmpty { categoryLabel },
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            // Discount Badge (Top-End)
            if (product.discountPrice != null && product.discountPrice < product.price) {
                val discountPercent = (((product.price - product.discountPrice) / product.price) * 100).toInt()
                Surface(
                    color = AgriDealOrange,
                    shape = RoundedCornerShape(bottomStart = 10.dp, topEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "$discountPercent% OFF",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 7.dp, vertical = 4.dp)
                    )
                }
            } else if (product.stockQuantity in 1..5) {
                Surface(
                    color = HarvestGoldDark,
                    shape = RoundedCornerShape(bottomStart = 10.dp, topEnd = 16.dp),
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Text(
                        text = "Low Stock (${product.stockQuantity})",
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                    )
                }
            }

            // Brand tag watermark (Bottom-Start)
            Surface(
                color = Color.White.copy(alpha = 0.9f),
                shape = RoundedCornerShape(topEnd = 6.dp),
                modifier = Modifier.align(Alignment.BottomStart)
            ) {
                Text(
                    text = product.brand.uppercase(),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = AgriGreenDark,
                    letterSpacing = 0.4.sp,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    onProductClick: () -> Unit,
    onAddToCart: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onProductClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, AgriBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("product_card_${product.id}")
    ) {
        Column {
            // Visual Banner Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
                ProductThumbnail(
                    product = product,
                    modifier = Modifier.fillMaxSize(),
                    showBadges = true
                )
            }

            // Product Details Area
            Column(modifier = Modifier.padding(12.dp)) {
                // Title
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp,
                    modifier = Modifier.height(34.dp)
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Rating & Seller Info
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Surface(
                        color = HarvestGoldContainer,
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = HarvestGoldDark,
                                modifier = Modifier.size(10.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "${product.rating}",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnHarvestGoldContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    Text(
                        text = product.sellerName,
                        fontSize = 10.sp,
                        color = TextSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Price & Add to Cart Action Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        val effectivePrice = product.discountPrice ?: product.price
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "₹${effectivePrice.toInt()}",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.ExtraBold,
                                color = AgriGreenPrimary
                            )
                            if (product.discountPrice != null) {
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "₹${product.price.toInt()}",
                                    fontSize = 11.sp,
                                    color = TextTertiary,
                                    textDecoration = TextDecoration.LineThrough
                                )
                            }
                        }

                        Text(
                            text = "per ${product.unit}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }

                    // Direct Add Button with emerald background & badge
                    Button(
                        onClick = onAddToCart,
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 0.dp),
                        modifier = Modifier
                            .height(32.dp)
                            .testTag("add_to_cart_btn_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Add",
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            text = "Add",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ShopCard(
    seller: SellerProfile,
    distanceKm: Double,
    onShopClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onShopClick,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, AgriBorder),
        modifier = modifier
            .fillMaxWidth()
            .testTag("shop_card_${seller.id}")
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Hardware Store Emblem / Thumbnail
            Box(
                modifier = Modifier
                    .size(76.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(Color(0xFF0F766E), Color(0xFF15803D))
                        )
                    )
            ) {
                // Hardware shop vector art
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(32.dp)
                        )
                        Text(
                            text = "AGRO HARDWARE",
                            fontSize = 7.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White.copy(alpha = 0.8f)
                        )
                    }
                }

                if (seller.shopImageUrl.isNotBlank()) {
                    AsyncImage(
                        model = seller.shopImageUrl,
                        contentDescription = seller.shopName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Verified tick in corner
                Surface(
                    color = AgriGreenPrimary,
                    shape = CircleShape,
                    modifier = Modifier
                        .size(18.dp)
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = seller.shopName,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = "Dealer: ${seller.ownerName} • ${seller.city}",
                    fontSize = 12.sp,
                    color = TextSecondary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = seller.shopAddress,
                    fontSize = 11.sp,
                    color = TextTertiary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(6.dp))

                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Rating
                    Surface(
                        color = HarvestGoldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "Rating",
                                tint = HarvestGoldDark,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(3.dp))
                            Text(
                                text = "${seller.rating}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnHarvestGoldContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    // Distance
                    Surface(
                        color = AgriGreenContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.LocationOn,
                                contentDescription = "Distance",
                                tint = AgriGreenPrimary,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(2.dp))
                            Text(
                                text = "$distanceKm km",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnAgriGreenContainer
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(
                        text = "• Fast Delivery",
                        fontSize = 11.sp,
                        color = StatusSuccess,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }
    }
}

@Composable
fun DeliveryOtpCard(
    otp: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = HarvestGoldContainer),
        border = BorderStroke(1.dp, HarvestGold.copy(alpha = 0.5f)),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.VpnKey,
                    contentDescription = "OTP",
                    tint = OnHarvestGoldContainer,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "DELIVERY HANDOVER OTP",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnHarvestGoldContainer,
                    letterSpacing = 1.sp
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Surface(
                color = Color.White,
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, HarvestGold.copy(alpha = 0.3f)),
                shadowElevation = 2.dp
            ) {
                Text(
                    text = otp,
                    fontSize = 32.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = OnHarvestGoldContainer,
                    letterSpacing = 6.sp,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Share this 4-digit OTP with the hardware shop delivery boy only after inspecting your items.",
                fontSize = 12.sp,
                color = OnHarvestGoldContainer.copy(alpha = 0.85f),
                lineHeight = 16.sp
            )
        }
    }
}

@Composable
fun RatingBar(
    rating: Int,
    onRatingChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, horizontalArrangement = Arrangement.Center) {
        for (i in 1..5) {
            IconButton(
                onClick = { onRatingChange(i) },
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = if (i <= rating) Icons.Default.Star else Icons.Default.StarBorder,
                    contentDescription = "$i Stars",
                    tint = if (i <= rating) HarvestGold else Color.Gray,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
    }
}

