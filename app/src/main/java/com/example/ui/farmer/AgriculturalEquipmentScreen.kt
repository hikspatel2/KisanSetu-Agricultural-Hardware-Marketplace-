package com.example.ui.farmer

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.MainViewModel
import com.example.ui.components.ProductThumbnail
import com.example.ui.theme.*

/**
 * Pre-populated catalog of realistic agricultural equipment models for display and testing.
 */
val SampleAgriculturalEquipment: List<Product> = listOf(
    Product(
        id = "eq_pump_kirloskar_5hp",
        sellerId = "seller_kisan_center",
        sellerName = "Kisan Agro Hardware & Drip Center",
        name = "Kirloskar 5 HP Openwell Submersible Pump",
        category = "Water Pumps",
        subcategory = "Submersible Pumps",
        brand = "Kirloskar",
        sku = "EQP-KIRL-5HP-OW",
        description = "Heavy duty 3-phase openwell submersible pump designed for open wells, canals, and riverbed irrigation pumping.",
        imageUrl = "https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122?w=500",
        price = 24500.0,
        discountPrice = 22800.0,
        unit = "Set",
        stockQuantity = 8,
        rating = 4.9,
        totalRatings = 24
    ),
    Product(
        id = "eq_sprayer_neptune_16l",
        sellerId = "seller_patel",
        sellerName = "Patel Krushi Hardware",
        name = "Neptune 16L Battery Knapsack Sprayer",
        category = "Spray Pumps",
        subcategory = "Battery Sprayers",
        brand = "Neptune",
        sku = "EQP-NEP-16L-BAT",
        description = "Dual-function 12V 12Ah battery sprayer with brass telescopic lance and 4 precision spray nozzles.",
        imageUrl = "https://images.unsplash.com/photo-1615811361523-6bd03d7748e7?w=500",
        price = 2800.0,
        discountPrice = 2450.0,
        unit = "Piece",
        stockQuantity = 15,
        rating = 4.8,
        totalRatings = 32
    ),
    Product(
        id = "eq_drip_jain_16mm",
        sellerId = "seller_patel",
        sellerName = "Patel Krushi Hardware",
        name = "Jain Inline 16mm Drip Pipe (400m Roll)",
        category = "Irrigation & Drip",
        subcategory = "Drip Lateral",
        brand = "Jain Irrigation",
        sku = "IRR-JAIN-16-400",
        description = "High UV resistant 16mm inline drip lateral pipe with 40cm emitter spacing for precision irrigation.",
        imageUrl = "https://images.unsplash.com/photo-1592417817098-8f3d69106093?w=500",
        price = 3200.0,
        discountPrice = 2850.0,
        unit = "Roll",
        stockQuantity = 30,
        rating = 4.7,
        totalRatings = 19
    ),
    Product(
        id = "eq_rotavator_mahindra",
        sellerId = "seller_shree_ram",
        sellerName = "Shree Ram Farm Spares & Pumps",
        name = "Mahindra Heavy Duty Rotary Tiller Rotavator (6 Feet)",
        category = "Tractor Implements",
        subcategory = "Rotary Tillers",
        brand = "Mahindra",
        sku = "EQP-MAH-ROT-6FT",
        description = "Heavy duty multi-speed gearbox tractor rotavator with Boron steel L-type blades for superior seedbed preparation.",
        imageUrl = "https://images.unsplash.com/photo-1595974482597-4b8da8879bc5?w=500",
        price = 95000.0,
        discountPrice = 88000.0,
        unit = "Unit",
        stockQuantity = 3,
        rating = 4.9,
        totalRatings = 14
    ),
    Product(
        id = "eq_power_sprayer_aspee",
        sellerId = "seller_kisan_center",
        sellerName = "Kisan Agro Hardware & Drip Center",
        name = "Aspee 2-Stroke Portable Power Sprayer Engine",
        category = "Spray Pumps",
        subcategory = "Power Sprayers",
        brand = "Aspee",
        sku = "EQP-ASP-PWR-2ST",
        description = "High pressure portable petrol engine sprayer unit with 50-meter heavy duty delivery hose and spray gun.",
        imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500",
        price = 11500.0,
        discountPrice = 9800.0,
        unit = "Set",
        stockQuantity = 5,
        rating = 4.8,
        totalRatings = 11
    ),
    Product(
        id = "eq_pvc_supreme_4in",
        sellerId = "seller_patel",
        sellerName = "Patel Krushi Hardware",
        name = "Supreme 4-inch PVC Agricultural Pipe (20ft)",
        category = "Irrigation & Drip",
        subcategory = "Pipes",
        brand = "Supreme",
        sku = "IRR-SUP-PVC-4IN",
        description = "Rigid 20-foot PVC pipe with rubber seal socket for borewell discharge and main agricultural pipeline.",
        imageUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb186c5f9?w=500",
        price = 1150.0,
        discountPrice = 990.0,
        unit = "Piece (20ft)",
        stockQuantity = 50,
        rating = 4.6,
        totalRatings = 45
    ),
    Product(
        id = "eq_pruning_secateur_falcon",
        sellerId = "seller_shree_ram",
        sellerName = "Shree Ram Farm Spares & Pumps",
        name = "Falcon Heavy Duty Forged Steel Pruning Secateur",
        category = "Tools & Pruners",
        subcategory = "Hand Tools",
        brand = "Falcon",
        sku = "TOOL-FALC-PRUN-01",
        description = "Forged high carbon steel bypass garden shear for orchard pruning, cotton stalk trimming, and horticultural work.",
        imageUrl = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500",
        price = 620.0,
        discountPrice = 520.0,
        unit = "Piece",
        stockQuantity = 25,
        rating = 4.5,
        totalRatings = 37
    ),
    Product(
        id = "eq_starter_lt_mk1",
        sellerId = "seller_shree_ram",
        sellerName = "Shree Ram Farm Spares & Pumps",
        name = "L&T MK1 3-Phase Agricultural Motor Starter",
        category = "Water Pumps",
        subcategory = "Motor Starters",
        brand = "L&T Electrical",
        sku = "ELE-LT-MK1-DOL",
        description = "Genuine Larson & Toubro direct-on-line starter with overload relay for 3-phase farm motors (5HP to 7.5HP).",
        imageUrl = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500",
        price = 3100.0,
        discountPrice = 2850.0,
        unit = "Piece",
        stockQuantity = 18,
        rating = 4.9,
        totalRatings = 42
    ),
    Product(
        id = "eq_sprinkler_finolex_rotary",
        sellerId = "seller_kisan_center",
        sellerName = "Kisan Agro Hardware & Drip Center",
        name = "Finolex 3/4-inch Brass Rotary Impact Sprinkler",
        category = "Irrigation & Drip",
        subcategory = "Sprinklers",
        brand = "Finolex",
        sku = "IRR-SPR-FIN-34",
        description = "Full circle brass impact sprinkler head. Spray radius 12 to 14 meters. Ideal for wheat, potato, and groundnut fields.",
        imageUrl = "https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?w=500",
        price = 450.0,
        discountPrice = 380.0,
        unit = "Piece",
        stockQuantity = 60,
        rating = 4.6,
        totalRatings = 28
    ),
    Product(
        id = "eq_filter_jain_screen_2in",
        sellerId = "seller_patel",
        sellerName = "Patel Krushi Hardware",
        name = "Jain T-Type 2-inch Drip Screen Filter (120 Mesh)",
        category = "Irrigation & Drip",
        subcategory = "Filters",
        brand = "Jain",
        sku = "IRR-JAIN-FLT-2IN",
        description = "Stainless steel 120 mesh screen filter for preventing emitter clogging from sand and algae in canal and well water.",
        imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500",
        price = 1450.0,
        discountPrice = 1250.0,
        unit = "Piece",
        stockQuantity = 16,
        rating = 4.7,
        totalRatings = 15
    ),
    Product(
        id = "eq_post_hole_digger",
        sellerId = "seller_shree_ram",
        sellerName = "Shree Ram Farm Spares & Pumps",
        name = "Shaktiman Post Hole Digger Tractor Attachment",
        category = "Tractor Implements",
        subcategory = "Diggers",
        brand = "Shaktiman",
        sku = "EQP-SHAK-PHD-36",
        description = "Heavy duty PTO operated tractor post hole digger with 9-inch and 12-inch auger bits for fencing and orchard tree planting.",
        imageUrl = "https://images.unsplash.com/photo-1500382017468-9049fed747ef?w=500",
        price = 68000.0,
        discountPrice = 64000.0,
        unit = "Unit",
        stockQuantity = 2,
        rating = 4.8,
        totalRatings = 9
    ),
    Product(
        id = "eq_brush_cutter_honda",
        sellerId = "seller_kisan_center",
        sellerName = "Kisan Agro Hardware & Drip Center",
        name = "Honda 4-Stroke Brush Cutter & Crop Harvester",
        category = "Tools & Pruners",
        subcategory = "Power Cutters",
        brand = "Honda",
        sku = "TOOL-HON-BC-35",
        description = "35cc 4-stroke engine brush cutter with 80T paddy harvesting blade, nylon trimmer, and ergonomic backpack harness.",
        imageUrl = "https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?w=500",
        price = 28500.0,
        discountPrice = 26500.0,
        unit = "Set",
        stockQuantity = 7,
        rating = 4.9,
        totalRatings = 21
    )
)

/**
 * ViewModel-integrated entry point for the Agricultural Equipment Screen.
 */
@Composable
fun AgriculturalEquipmentScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit = {},
    onNavigateToProduct: (String) -> Unit = {},
    onNavigateToCart: () -> Unit = {}
) {
    val allProducts by viewModel.allProducts.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val totalCartCount = cartItems.sumOf { it.quantity }

    AgriculturalEquipmentScreen(
        products = if (allProducts.isNotEmpty()) allProducts else SampleAgriculturalEquipment,
        cartItemCount = totalCartCount,
        onProductClick = { product -> onNavigateToProduct(product.id) },
        onAddToCart = { product -> viewModel.addToCart(product) },
        onBack = onBack,
        onCartClick = onNavigateToCart
    )
}

/**
 * Primary Composable screen displaying a grid of agricultural equipment using Material 3 cards.
 * Includes equipment images, names, prices, search, category filter chips, and add-to-cart actions.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AgriculturalEquipmentScreen(
    products: List<Product> = SampleAgriculturalEquipment,
    cartItemCount: Int = 0,
    onProductClick: (Product) -> Unit = {},
    onAddToCart: (Product) -> Unit = {},
    onBack: (() -> Unit)? = null,
    onCartClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }

    // Categories available for filtering
    val categories = remember(products) {
        listOf("All") + products.map { it.category }.distinct().sorted()
    }

    // Filter products based on query and selected category
    val filteredProducts = remember(products, searchQuery, selectedCategory) {
        products.filter { product ->
            val matchesCategory = selectedCategory == "All" || product.category.equals(selectedCategory, ignoreCase = true)
            val matchesQuery = searchQuery.isBlank() ||
                    product.name.contains(searchQuery, ignoreCase = true) ||
                    product.brand.contains(searchQuery, ignoreCase = true) ||
                    product.category.contains(searchQuery, ignoreCase = true) ||
                    product.description.contains(searchQuery, ignoreCase = true)
            matchesCategory && matchesQuery
        }
    }

    Scaffold(
        modifier = modifier.testTag("agricultural_equipment_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Agricultural Equipment",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Pumps, Sprayers, Tillers & Farm Hardware",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(
                            onClick = onBack,
                            modifier = Modifier.testTag("equipment_back_button")
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = "Back",
                                tint = TextPrimary
                            )
                        }
                    }
                },
                actions = {
                    if (onCartClick != null) {
                        IconButton(
                            onClick = onCartClick,
                            modifier = Modifier.testTag("equipment_cart_button")
                        ) {
                            BadgedBox(
                                badge = {
                                    if (cartItemCount > 0) {
                                        Badge(
                                            containerColor = HarvestGoldDark,
                                            contentColor = Color.White
                                        ) {
                                            Text("$cartItemCount")
                                        }
                                    }
                                }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ShoppingCart,
                                    contentDescription = "Shopping Cart",
                                    tint = AgriGreenPrimary
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(AgriBackground)
        ) {
            // Search Bar Input
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        text = "Search equipment, pumps, sprayers, brands...",
                        fontSize = 13.sp,
                        color = TextTertiary
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(20.dp)
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear",
                                tint = TextSecondary,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                    focusedBorderColor = AgriGreenPrimary,
                    unfocusedBorderColor = AgriBorder
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .testTag("equipment_search_field")
            )

            // Category Filter Chips Row
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 8.dp)
            ) {
                items(categories) { category ->
                    val isSelected = category == selectedCategory
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedCategory = category },
                        label = {
                            Text(
                                text = category,
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = AgriGreenPrimary,
                            selectedLabelColor = Color.White,
                            containerColor = MaterialTheme.colorScheme.surface,
                            labelColor = TextPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) AgriGreenPrimary else AgriBorder,
                            selectedBorderColor = AgriGreenPrimary,
                            enabled = true,
                            selected = isSelected
                        ),
                        shape = RoundedCornerShape(20.dp)
                    )
                }
            }

            // Results Count Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${filteredProducts.size} Equipment Available",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary
                )
                if (selectedCategory != "All" || searchQuery.isNotEmpty()) {
                    Text(
                        text = "Reset Filters",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = AgriGreenPrimary,
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            selectedCategory = "All"
                        }
                    )
                }
            }

            // Equipment Grid or Empty State
            if (filteredProducts.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(32.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = AgriSurfaceVariant,
                            modifier = Modifier.size(72.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = Icons.Default.PrecisionManufacturing,
                                    contentDescription = null,
                                    tint = TextSecondary,
                                    modifier = Modifier.size(36.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "No equipment found",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try adjusting your search terms or filter category.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                searchQuery = ""
                                selectedCategory = "All"
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Text("Show All Equipment")
                        }
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    modifier = Modifier
                        .fillMaxSize()
                        .testTag("equipment_grid")
                ) {
                    items(filteredProducts, key = { it.id }) { product ->
                        AgriculturalEquipmentCard(
                            product = product,
                            onProductClick = { onProductClick(product) },
                            onAddToCart = { onAddToCart(product) }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Material 3 Card displaying an individual agricultural equipment item with:
 * - Equipment Image (with high visual fidelity and fallback illustration)
 * - Equipment Name
 * - Equipment Price (with discount and unit)
 * - Brand badge, rating, and Add-to-Cart button
 */
@Composable
fun AgriculturalEquipmentCard(
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
            .testTag("equipment_card_${product.id}")
    ) {
        Column {
            // Equipment Image Visual Banner Area
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(138.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .testTag("equipment_image_${product.id}")
            ) {
                ProductThumbnail(
                    product = product,
                    modifier = Modifier.fillMaxSize(),
                    showBadges = true
                )
            }

            // Equipment Details Area
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp)
            ) {
                // Category & Brand Tag
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = product.brand.uppercase(),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = AgriGreenDark,
                        letterSpacing = 0.5.sp,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // Rating chip
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
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Equipment Name
                Text(
                    text = product.name,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 17.sp,
                    modifier = Modifier
                        .height(34.dp)
                        .testTag("equipment_name_${product.id}")
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Price and Unit
                val effectivePrice = product.effectivePrice
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.testTag("equipment_price_${product.id}")
                ) {
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

                Spacer(modifier = Modifier.height(8.dp))

                // Add to Cart Button with minimum 48dp touch target accessibility
                Button(
                    onClick = onAddToCart,
                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(36.dp)
                        .testTag("equipment_add_to_cart_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.AddShoppingCart,
                        contentDescription = "Add to Cart",
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Add to Cart",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

/**
 * Alternate alias composable matching EquipmentGridScreen naming convention.
 */
@Composable
fun EquipmentGridScreen(
    products: List<Product> = SampleAgriculturalEquipment,
    onProductClick: (Product) -> Unit = {},
    onAddToCart: (Product) -> Unit = {},
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AgriculturalEquipmentScreen(
        products = products,
        onProductClick = onProductClick,
        onAddToCart = onAddToCart,
        onBack = onBack,
        modifier = modifier
    )
}

/**
 * Alternate alias composable matching AgriculturalEquipmentGridScreen naming convention.
 */
@Composable
fun AgriculturalEquipmentGridScreen(
    products: List<Product> = SampleAgriculturalEquipment,
    onProductClick: (Product) -> Unit = {},
    onAddToCart: (Product) -> Unit = {},
    onBack: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    AgriculturalEquipmentScreen(
        products = products,
        onProductClick = onProductClick,
        onAddToCart = onAddToCart,
        onBack = onBack,
        modifier = modifier
    )
}
