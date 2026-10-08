package com.example.ui.farmer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.Product
import com.example.ui.MainViewModel
import com.example.ui.components.ProductCard
import com.example.ui.components.ProductThumbnail
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProductDetailScreen(
    productId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToShop: (String) -> Unit,
    onNavigateToCart: () -> Unit
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val product = allProducts.find { it.id == productId }

    var selectedQuantity by remember { mutableIntStateOf(1) }

    if (product == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Product not found", fontSize = 16.sp, color = TextSecondary)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(product.name, maxLines = 1, fontSize = 16.sp, fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToCart) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                border = BorderStroke(1.dp, AgriBorder),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Quantity selector
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .background(AgriSurfaceVariant, RoundedCornerShape(12.dp))
                            .border(1.dp, AgriBorder, RoundedCornerShape(12.dp))
                            .padding(2.dp)
                    ) {
                        IconButton(
                            onClick = { if (selectedQuantity > product.minOrderQuantity) selectedQuantity-- },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = "Decrease", tint = AgriGreenPrimary)
                        }

                        Text(
                            text = "$selectedQuantity",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp,
                            color = TextPrimary,
                            modifier = Modifier.padding(horizontal = 10.dp)
                        )

                        IconButton(
                            onClick = { if (selectedQuantity < product.stockQuantity) selectedQuantity++ },
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Increase", tint = AgriGreenPrimary)
                        }
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Button(
                        onClick = {
                            viewModel.addToCart(product, selectedQuantity)
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp)
                            .testTag("detail_add_to_cart_btn")
                    ) {
                        Icon(Icons.Default.AddShoppingCart, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Add to Cart", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                    }
                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AgriBackground)
                .verticalScroll(rememberScrollState())
        ) {
            // High-detail Hero Thumbnail with category vector art & Coil layer
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
            ) {
                ProductThumbnail(
                    product = product,
                    modifier = Modifier.fillMaxSize(),
                    showBadges = true
                )
            }

            Column(modifier = Modifier.padding(16.dp)) {
                // Brand & Category Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = product.brand.uppercase(),
                        fontSize = 13.sp,
                        color = AgriGreenPrimary,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )

                    Surface(
                        color = HarvestGoldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Icon(Icons.Default.Star, contentDescription = null, tint = HarvestGoldDark, modifier = Modifier.size(12.dp))
                            Spacer(modifier = Modifier.width(3.dp))
                            Text("${product.rating} ★ Rating", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = OnHarvestGoldContainer)
                        }
                    }
                }

                Text(
                    text = product.name,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    lineHeight = 26.sp,
                    modifier = Modifier.padding(vertical = 4.dp)
                )

                // Price & Savings Row
                val price = product.discountPrice ?: product.price
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Text(
                        text = "₹${price.toInt()}",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenPrimary
                    )

                    if (product.discountPrice != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "₹${product.price.toInt()}",
                            fontSize = 16.sp,
                            color = TextTertiary,
                            textDecoration = TextDecoration.LineThrough
                        )

                        Spacer(modifier = Modifier.width(8.dp))
                        val savings = (product.price - price).toInt()
                        val discountPercent = (((product.price - price) / product.price) * 100).toInt()
                        Surface(
                            color = AgriGreenContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Text(
                                text = "Save ₹$savings ($discountPercent% OFF)",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = OnAgriGreenContainer,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "per ${product.unit}",
                        fontSize = 13.sp,
                        color = TextSecondary
                    )
                }

                // Stock & Delivery status badges
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 6.dp)
                ) {
                    Surface(
                        color = if (product.stockQuantity > 5) AgriGreenContainer else HarvestGoldContainer,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(if (product.stockQuantity > 5) AgriGreenPrimary else HarvestGoldDark)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = if (product.stockQuantity > 5) "In Stock (${product.stockQuantity} available)" else "Low Stock (${product.stockQuantity} left)",
                                color = if (product.stockQuantity > 5) OnAgriGreenContainer else OnHarvestGoldContainer,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(8.dp))

                    Surface(
                        color = AgriSurfaceVariant,
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text(
                            text = "Min Order: ${product.minOrderQuantity} ${product.unit}",
                            color = TextSecondary,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Agricultural Quality Assurances Card
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                    border = BorderStroke(1.dp, AgriBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Verified, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("100% Genuine ISI Certified Hardware", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.LocalShipping, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Fast Farm Delivery within 2-4 Hours", fontSize = 12.sp, color = TextSecondary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Payments, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Cash on Delivery & UPI accepted at farm gate", fontSize = 12.sp, color = TextSecondary)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // Seller Information Card
                Card(
                    onClick = { onNavigateToShop(product.sellerId) },
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, AgriBorder),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AgriGreenContainer,
                            modifier = Modifier.size(40.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(Icons.Default.Storefront, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(22.dp))
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Sold & Dispatched by",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            Text(
                                text = product.sellerName,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text("View Shop →", color = AgriGreenPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Product Description & Specs
                Text(
                    text = "SPECIFICATIONS & DETAILS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.ExtraBold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = product.description,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    lineHeight = 20.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Hardware Spec Sheet
                Surface(
                    color = Color.White,
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, AgriBorder),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("SKU Code:", fontSize = 12.sp, color = TextSecondary)
                            Text(product.sku, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Category:", fontSize = 12.sp, color = TextSecondary)
                            Text(product.category, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subcategory:", fontSize = 12.sp, color = TextSecondary)
                            Text(product.subcategory, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Standard Unit:", fontSize = 12.sp, color = TextSecondary)
                            Text(product.unit, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextPrimary)
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerShopDetailScreen(
    sellerId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onNavigateToProduct: (String) -> Unit
) {
    val allSellers by viewModel.allSellers.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val seller = allSellers.find { it.id == sellerId }
    val shopProducts = allProducts.filter { it.sellerId == sellerId && it.isAvailable }

    if (seller == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Shop not found", fontSize = 16.sp, color = TextSecondary)
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(seller.shopName, fontWeight = FontWeight.Bold, fontSize = 16.sp) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AgriBackground),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            item {
                // Shop Banner with gradient
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(
                            Brush.linearGradient(
                                listOf(Color(0xFF0F766E), Color(0xFF15803D))
                            )
                        )
                ) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Storefront, contentDescription = null, tint = Color.White, modifier = Modifier.size(48.dp))
                            Text("AUTHORIZED HARDWARE DEALER", fontSize = 11.sp, fontWeight = FontWeight.ExtraBold, color = Color.White.copy(alpha = 0.9f))
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
                }

                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = seller.shopName,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Owner: ${seller.ownerName} • ${seller.city}, ${seller.state} - ${seller.pincode}",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                    Text(
                        text = seller.shopAddress,
                        fontSize = 12.sp,
                        color = TextTertiary
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Surface(
                            color = HarvestGoldContainer,
                            shape = RoundedCornerShape(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp)
                            ) {
                                Icon(Icons.Default.Star, contentDescription = null, tint = HarvestGoldDark, modifier = Modifier.size(14.dp))
                                Spacer(modifier = Modifier.width(3.dp))
                                Text("${seller.rating} (${seller.totalRatings} ratings)", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = OnHarvestGoldContainer)
                            }
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Text("• Open for Farm Delivery", color = StatusSuccess, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                    }

                    Spacer(modifier = Modifier.height(16.dp))
                    HorizontalDivider(color = AgriBorder)
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "AVAILABLE HARDWARE CATALOG (${shopProducts.size})",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 1.sp
                    )
                }
            }

            items(shopProducts.chunked(2)) { pair ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    ProductCard(
                        product = pair[0],
                        onProductClick = { onNavigateToProduct(pair[0].id) },
                        onAddToCart = { viewModel.addToCart(pair[0]) },
                        modifier = Modifier.weight(1f)
                    )
                    if (pair.size > 1) {
                        ProductCard(
                            product = pair[1],
                            onProductClick = { onNavigateToProduct(pair[1].id) },
                            onAddToCart = { viewModel.addToCart(pair[1]) },
                            modifier = Modifier.weight(1f)
                        )
                    } else {
                        Spacer(modifier = Modifier.weight(1f))
                    }
                }
            }
        }
    }
}
