package com.example.ui.farmer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Category
import com.example.data.model.Product
import com.example.data.model.SellerProfile
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.ProductCard
import com.example.ui.components.ShopCard
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerHomeScreen(
    viewModel: MainViewModel,
    onNavigateToProduct: (String) -> Unit,
    onNavigateToShop: (String) -> Unit,
    onNavigateToCategory: (String) -> Unit,
    onNavigateToCart: () -> Unit,
    onNavigateToOrders: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()
    val categories by viewModel.categories.collectAsState()
    val nearbyShops by viewModel.nearbyApprovedShops.collectAsState()
    val filteredProducts by viewModel.filteredProducts.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategoryFilter.collectAsState()

    val cartItemCount = cartItems.sumOf { it.quantity }
    val discountedDeals = remember(allProducts) {
        allProducts.filter { it.discountPrice != null && it.discountPrice < it.price }
    }

    Scaffold(
        topBar = {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.horizontalGradient(
                            listOf(AgriGreenPrimary, Color(0xFF0F766E), Color(0xFF065F46))
                        )
                    )
            ) {
                TopAppBar(
                    title = {
                        Column {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.LocationOn,
                                    contentDescription = null,
                                    tint = HarvestGoldLight,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentUser?.address?.ifEmpty { "Anand Agricultural Zone" } ?: "Anand, Gujarat",
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                            Text(
                                text = "Express Delivery within ${appSettings.deliveryRadiusKm.toInt()} km",
                                fontSize = 11.sp,
                                color = AgriGreenContainer,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    },
                    actions = {
                        // Switch Role icon (Farmer <-> Seller)
                        IconButton(onClick = { viewModel.switchRole(UserRole.SELLER) }) {
                            Icon(
                                imageVector = Icons.Default.Storefront,
                                contentDescription = "Switch to Seller",
                                tint = Color.White
                            )
                        }

                        // Admin Simulator button
                        IconButton(onClick = { viewModel.toggleAdminPanel(true) }) {
                            Icon(
                                imageVector = Icons.Default.AdminPanelSettings,
                                contentDescription = "Admin Panel",
                                tint = Color.White
                            )
                        }

                        // Cart icon with badge
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge(
                                        containerColor = HarvestGold,
                                        contentColor = Color.White
                                    ) {
                                        Text("$cartItemCount", fontWeight = FontWeight.Bold)
                                    }
                                }
                            },
                            modifier = Modifier.padding(end = 8.dp)
                        ) {
                            IconButton(
                                onClick = onNavigateToCart,
                                modifier = Modifier.testTag("home_cart_button")
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = Color.White
                                )
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = Color.Transparent
                    )
                )
            }
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already Home */ },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
                    label = { Text("Home", fontWeight = FontWeight.SemiBold) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { onNavigateToCategory("Irrigation") },
                    icon = { Icon(Icons.Default.Category, contentDescription = "Categories") },
                    label = { Text("Categories") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToCart,
                    icon = {
                        BadgedBox(
                            badge = {
                                if (cartItemCount > 0) {
                                    Badge(containerColor = HarvestGold) { Text("$cartItemCount") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ShoppingCart, contentDescription = "Cart")
                        }
                    },
                    label = { Text("Cart") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToOrders,
                    icon = { Icon(Icons.Default.ReceiptLong, contentDescription = "Orders") },
                    label = { Text("Orders") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
                    label = { Text("Profile") }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(AgriBackground),
            contentPadding = PaddingValues(bottom = 28.dp)
        ) {
            // Search Bar Area
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                listOf(Color(0xFF0F766E), AgriGreenPrimary, AgriBackground)
                            )
                        )
                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp)
                ) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { viewModel.searchQuery.value = it },
                        placeholder = {
                            Text(
                                "Search drip pipe, pumps, sprayers, cables...",
                                fontSize = 13.sp,
                                color = TextTertiary
                            )
                        },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = null,
                                tint = AgriGreenPrimary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(onClick = { viewModel.searchQuery.value = "" }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            focusedBorderColor = AgriGreenPrimary,
                            unfocusedBorderColor = AgriBorder
                        ),
                        shape = RoundedCornerShape(14.dp),
                        singleLine = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("farmer_search_input")
                    )
                }
            }

            // Promotional Agriculture Hero Banner
            item {
                Card(
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.Transparent),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.linearGradient(
                                    listOf(
                                        Color(0xFF14532D),
                                        Color(0xFF0F766E),
                                        Color(0xFF0369A1)
                                    )
                                )
                            )
                            .padding(18.dp)
                    ) {
                        Column {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween,
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Surface(
                                    color = HarvestGold,
                                    shape = RoundedCornerShape(6.dp)
                                ) {
                                    Text(
                                        text = "🌾 KRUSHI DHAMAKA SALE",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.ExtraBold,
                                        color = Color.Black,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    )
                                }

                                Surface(
                                    color = Color.White.copy(alpha = 0.2f),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                                    ) {
                                        Icon(
                                            Icons.Default.Bolt,
                                            contentDescription = null,
                                            tint = HarvestGoldLight,
                                            modifier = Modifier.size(12.dp)
                                        )
                                        Spacer(modifier = Modifier.width(3.dp))
                                        Text(
                                            "2 Hr Farm Delivery",
                                            fontSize = 10.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color.White
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(10.dp))

                            Text(
                                text = "Drip Pipes, Motors & Agricultural Hardware",
                                fontSize = 17.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                lineHeight = 22.sp
                            )

                            Text(
                                text = "Up to 30% Off on ISI certified drip lines, knapsack spray pumps & valves directly from verified nearby hardware dealers.",
                                fontSize = 12.sp,
                                color = Color.White.copy(alpha = 0.9f),
                                lineHeight = 16.sp,
                                modifier = Modifier.padding(vertical = 4.dp)
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    color = Color.White,
                                    shape = RoundedCornerShape(8.dp)
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "Cash on Delivery Available",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AgriGreenPrimary
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.CheckCircle,
                                            contentDescription = null,
                                            tint = AgriGreenPrimary,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.width(8.dp))

                                Surface(
                                    color = HarvestGold,
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier.clickable {
                                        viewModel.navigateTo(com.example.ui.ScreenDestination.AgriculturalEquipment)
                                    }
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                    ) {
                                        Text(
                                            text = "Equipment Grid",
                                            fontSize = 11.sp,
                                            fontWeight = FontWeight.ExtraBold,
                                            color = Color.Black
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(
                                            Icons.Default.ArrowForward,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier.size(13.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Visual Category Selector Row
            item {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    Text(
                        text = "EXPLORE BY HARDWARE CATEGORY",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = TextSecondary,
                        letterSpacing = 1.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(horizontal = 16.dp)
                    ) {
                        // "All" Category Item
                        item {
                            CategoryCircleItem(
                                title = "All",
                                icon = Icons.Default.Apps,
                                isSelected = selectedCategory == null,
                                activeColor = AgriGreenPrimary,
                                onClick = { viewModel.selectedCategoryFilter.value = null }
                            )
                        }

                        // Dynamic Backend Categories
                        items(categories) { cat ->
                            val (catIcon, catColor) = when {
                                cat.name.contains("Irrigation", ignoreCase = true) -> Pair(Icons.Default.WaterDrop, Color(0xFF0284C7))
                                cat.name.contains("Tool", ignoreCase = true) -> Pair(Icons.Default.Build, Color(0xFFEA580C))
                                cat.name.contains("Equipment", ignoreCase = true) -> Pair(Icons.Default.Agriculture, Color(0xFF047857))
                                cat.name.contains("Electrical", ignoreCase = true) -> Pair(Icons.Default.Bolt, Color(0xFF2563EB))
                                else -> Pair(Icons.Default.Handyman, Color(0xFF64748B))
                            }

                            CategoryCircleItem(
                                title = cat.name,
                                icon = catIcon,
                                isSelected = selectedCategory.equals(cat.name, ignoreCase = true),
                                activeColor = catColor,
                                onClick = {
                                    viewModel.selectedCategoryFilter.value =
                                        if (selectedCategory.equals(cat.name, ignoreCase = true)) null else cat.name
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(18.dp))
            }

            // Hot Deals / Discounted Hardware (if available)
            if (discountedDeals.isNotEmpty() && selectedCategory == null && searchQuery.isEmpty()) {
                item {
                    Column {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = "🔥 TODAY'S TOP HARDWARE DEALS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = AgriDealOrange,
                                    letterSpacing = 1.sp
                                )
                            }
                            Text(
                                text = "Special Farmer Discount",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            contentPadding = PaddingValues(horizontal = 16.dp)
                        ) {
                            items(discountedDeals) { deal ->
                                Box(modifier = Modifier.width(180.dp)) {
                                    ProductCard(
                                        product = deal,
                                        onProductClick = { onNavigateToProduct(deal.id) },
                                        onAddToCart = { viewModel.addToCart(deal) }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }

            // Nearby Approved Hardware Shops
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "NEARBY HARDWARE SHOPS (${nearbyShops.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )

                        Text(
                            text = "Within ${appSettings.deliveryRadiusKm.toInt()} km",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = AgriGreenPrimary
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (nearbyShops.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No approved hardware shops found within ${appSettings.deliveryRadiusKm.toInt()} km. Tap the top admin icon to increase radius or approve shops.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    } else {
                        nearbyShops.forEach { (seller, distance) ->
                            ShopCard(
                                seller = seller,
                                distanceKm = distance,
                                onShopClick = { onNavigateToShop(seller.id) },
                                modifier = Modifier.padding(bottom = 10.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Available Products Grid Header
            item {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedCategory != null) "$selectedCategory HARDWARE (${filteredProducts.size})" else "ALL AGRICULTURAL HARDWARE (${filteredProducts.size})",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.ExtraBold,
                            color = TextSecondary,
                            letterSpacing = 1.sp
                        )

                        if (selectedCategory != null) {
                            Text(
                                text = "Clear filter",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = AgriGreenPrimary,
                                modifier = Modifier.clickable { viewModel.selectedCategoryFilter.value = null }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (filteredProducts.isEmpty()) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = "No matching products found.",
                                fontSize = 13.sp,
                                color = TextSecondary,
                                modifier = Modifier.padding(16.dp)
                            )
                        }
                    }
                }
            }

            // Products in 2-column layout
            items(filteredProducts.chunked(2)) { pair ->
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

/**
 * Modern circular category button with high visual feedback.
 */
@Composable
private fun CategoryCircleItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .width(72.dp)
    ) {
        Surface(
            shape = CircleShape,
            color = if (isSelected) activeColor else activeColor.copy(alpha = 0.12f),
            border = if (isSelected) BorderStroke(2.dp, activeColor) else BorderStroke(1.dp, AgriBorder),
            shadowElevation = if (isSelected) 3.dp else 0.dp,
            modifier = Modifier.size(54.dp)
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = if (isSelected) Color.White else activeColor,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = title,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = if (isSelected) TextPrimary else TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
