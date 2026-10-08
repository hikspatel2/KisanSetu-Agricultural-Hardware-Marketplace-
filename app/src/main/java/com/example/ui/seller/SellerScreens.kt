package com.example.ui.seller

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.data.model.SellerStatus
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.SellerStatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerDashboardScreen(
    viewModel: MainViewModel,
    onNavigateToOrders: () -> Unit,
    onNavigateToProducts: () -> Unit,
    onNavigateToAddProduct: () -> Unit,
    onNavigateToInventory: () -> Unit,
    onNavigateToProfile: () -> Unit
) {
    val currentSeller by viewModel.currentSellerProfile.collectAsState()
    val sellerOrders by viewModel.sellerOrders.collectAsState()
    val sellerRequests by viewModel.sellerRequests.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()

    val myProducts = allProducts.filter { it.sellerId == (currentSeller?.id ?: "") }
    val lowStockCount = myProducts.count { it.stockQuantity <= 5 }
    val completedOrders = sellerOrders.filter { it.status == OrderStatus.DELIVERED }
    val activeOrders = sellerOrders.filter { it.status != OrderStatus.DELIVERED && it.status != OrderStatus.CANCELLED }
    val totalSales = completedOrders.sumOf { it.grandTotal }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(currentSeller?.shopName ?: "Seller Dashboard", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text(currentSeller?.ownerName ?: "Dealer", fontSize = 12.sp, color = Color.White.copy(alpha = 0.8f))
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.toggleAdminPanel(true) }) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin Panel", tint = Color.White)
                    }
                    IconButton(onClick = { viewModel.switchRole(UserRole.FARMER) }) {
                        Icon(Icons.Default.SwapHoriz, contentDescription = "Switch to Farmer", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AgriGreenPrimary,
                    titleContentColor = Color.White
                )
            )
        },
        bottomBar = {
            NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                NavigationBarItem(
                    selected = true,
                    onClick = { /* Already Dashboard */ },
                    icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
                    label = { Text("Dashboard") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToOrders,
                    icon = {
                        BadgedBox(
                            badge = {
                                if (sellerRequests.isNotEmpty()) {
                                    Badge(containerColor = HarvestGold) { Text("${sellerRequests.size}") }
                                }
                            }
                        ) {
                            Icon(Icons.Default.ReceiptLong, contentDescription = "Orders")
                        }
                    },
                    label = { Text("Orders") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProducts,
                    icon = { Icon(Icons.Default.Inventory2, contentDescription = "Products") },
                    label = { Text("Products") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToInventory,
                    icon = { Icon(Icons.Default.FactCheck, contentDescription = "Inventory") },
                    label = { Text("Stock") }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Store, contentDescription = "Profile") },
                    label = { Text("Shop") }
                )
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            // Approval Status Header
            currentSeller?.let { seller ->
                if (seller.status == SellerStatus.PENDING) {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(Icons.Default.Warning, contentDescription = null, tint = Color(0xFFF57F17))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("AWAITING ADMIN APPROVAL", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFE65100))
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Your hardware shop registration is pending Admin verification. You cannot receive nearby farmer orders until approved.",
                                fontSize = 12.sp,
                                color = Color(0xFF5D4037)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedButton(
                                onClick = { viewModel.adminApproveSeller(seller.id) },
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text("⚡ Simulate Admin Approval (1-Tap)", fontSize = 12.sp)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                } else {
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            SellerStatusBadge(status = seller.status)
                            Spacer(modifier = Modifier.width(12.dp))
                            Text("Active & Ready for Local Orders", fontSize = 12.sp, color = OnAgriGreenContainer, fontWeight = FontWeight.SemiBold)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }
            }

            // Key Metrics Grid
            Text("TODAY'S OVERVIEW", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                MetricCard("Active Orders", "${activeOrders.size}", Icons.Default.PendingActions, AgriGreenPrimary, Modifier.weight(1f))
                MetricCard("Completed", "${completedOrders.size}", Icons.Default.CheckCircle, StatusSuccess, Modifier.weight(1f))
                MetricCard("Total Sales", "₹${totalSales.toInt()}", Icons.Default.CurrencyRupee, HarvestGold, Modifier.weight(1f))
            }

            if (lowStockCount > 0) {
                Spacer(modifier = Modifier.height(10.dp))
                Card(
                    onClick = onNavigateToInventory,
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFEBEE)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Warning, contentDescription = null, tint = StatusError)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("$lowStockCount hardware items low in stock (< 5 left)", fontSize = 13.sp, color = StatusError, fontWeight = FontWeight.SemiBold)
                        Spacer(modifier = Modifier.weight(1f))
                        Text("Update →", fontSize = 12.sp, color = StatusError, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // NEW ORDER REQUESTS (Atomic Broadcast Matching!)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "LIVE ORDER REQUESTS (${sellerRequests.size})",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextSecondary,
                    letterSpacing = 1.sp
                )
                if (sellerRequests.isNotEmpty()) {
                    Text("First to Accept", fontSize = 11.sp, color = HarvestGold, fontWeight = FontWeight.Bold)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            if (sellerRequests.isEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "No pending order requests. When a farmer places an order within your delivery radius, it will appear here for you to accept.",
                        fontSize = 13.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                sellerRequests.forEach { req ->
                    Card(
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF8E1)),
                        shape = RoundedCornerShape(14.dp),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 10.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text("ORDER #${req.orderNumber}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = Color(0xFFE65100))
                                Text("${req.distanceKm} km away", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = AgriGreenPrimary)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(req.summaryText, fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Delivery Area: ${req.farmerArea}", fontSize = 12.sp, color = TextSecondary)
                            Text("Total Value: ₹${req.orderAmount.toInt()}", fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                OutlinedButton(
                                    onClick = { viewModel.sellerRejectOrder(req.orderId) },
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text("Reject")
                                }

                                Button(
                                    onClick = { viewModel.sellerAcceptOrder(req.orderId) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                    modifier = Modifier
                                        .weight(1f)
                                        .testTag("accept_order_btn_${req.orderId}")
                                ) {
                                    Text("Accept Order", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quick Navigation Shortcuts
            Text("SHOP MANAGEMENT ACTIONS", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                ActionCard("Add Product", Icons.Default.AddBox, onNavigateToAddProduct, Modifier.weight(1f))
                ActionCard("Manage Stock", Icons.Default.FactCheck, onNavigateToInventory, Modifier.weight(1f))
                ActionCard("All Orders", Icons.Default.ReceiptLong, onNavigateToOrders, Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color, modifier: Modifier = Modifier) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Icon(icon, contentDescription = null, tint = color, modifier = Modifier.size(20.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(value, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = color)
            Text(title, fontSize = 10.sp, color = TextSecondary, maxLines = 1)
        }
    }
}

@Composable
fun ActionCard(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector, onClick: () -> Unit, modifier: Modifier = Modifier) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(icon, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(24.dp))
            Spacer(modifier = Modifier.height(6.dp))
            Text(title, fontSize = 12.sp, fontWeight = FontWeight.SemiBold, maxLines = 1)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerOrdersScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentSeller by viewModel.currentSellerProfile.collectAsState()
    val sellerOrders by viewModel.sellerOrders.collectAsState()

    var showOtpDialogForOrder by remember { mutableStateOf<String?>(null) }
    var enteredOtp by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Shop Orders (${sellerOrders.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (sellerOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No orders assigned yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(sellerOrders) { order ->
                    Card(
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                OrderStatusBadge(order.status)
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text("Customer: ${order.farmerName} (${order.farmerPhone})", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            Text("Address: ${order.deliveryAddress}", fontSize = 12.sp, color = TextSecondary)
                            Text("Total: ₹${order.grandTotal.toInt()} • Payment: ${order.paymentMethod}", fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)

                            Spacer(modifier = Modifier.height(10.dp))

                            // Order items summary
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(AgriSurfaceVariant, RoundedCornerShape(8.dp))
                                    .padding(8.dp)
                            ) {
                                order.items.forEach { item ->
                                    Text("• ${item.productName} (x${item.quantity} ${item.unit})", fontSize = 12.sp)
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // Progression buttons according to order workflow:
                            when (order.status) {
                                OrderStatus.ACCEPTED -> {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.PREPARING) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Start Preparing Order")
                                    }
                                }
                                OrderStatus.PREPARING -> {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.READY_FOR_DELIVERY) },
                                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Text("Mark Ready for Dispatch")
                                    }
                                }
                                OrderStatus.READY_FOR_DELIVERY -> {
                                    Button(
                                        onClick = { viewModel.updateOrderStatus(order.id, OrderStatus.OUT_FOR_DELIVERY) },
                                        colors = ButtonDefaults.buttonColors(containerColor = HarvestGold),
                                        modifier = Modifier.fillMaxWidth()
                                    ) {
                                        Icon(Icons.Default.DeliveryDining, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Dispatch (Out for Delivery)")
                                    }
                                }
                                OrderStatus.OUT_FOR_DELIVERY -> {
                                    Button(
                                        onClick = {
                                            showOtpDialogForOrder = order.id
                                            enteredOtp = ""
                                        },
                                        colors = ButtonDefaults.buttonColors(containerColor = StatusSuccess),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .testTag("verify_otp_deliver_btn_${order.id}")
                                    ) {
                                        Icon(Icons.Default.VpnKey, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text("Enter Customer OTP to Deliver")
                                    }
                                }
                                OrderStatus.DELIVERED -> {
                                    Text(
                                        text = "✓ Order Successfully Delivered",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = StatusSuccess
                                    )
                                }
                                else -> {}
                            }
                        }
                    }
                }
            }
        }

        // OTP Dialog
        if (showOtpDialogForOrder != null) {
            AlertDialog(
                onDismissRequest = { showOtpDialogForOrder = null },
                title = { Text("Verify Customer Delivery OTP") },
                text = {
                    Column {
                        Text(
                            "Ask the farmer for the 4-digit OTP shown on their KisanSetu order screen.",
                            fontSize = 13.sp,
                            color = TextSecondary
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = enteredOtp,
                            onValueChange = { enteredOtp = it },
                            label = { Text("4-Digit OTP") },
                            singleLine = true,
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("seller_otp_dialog_input")
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            val id = showOtpDialogForOrder!!
                            viewModel.verifyDeliveryOtp(id, enteredOtp)
                            showOtpDialogForOrder = null
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                        modifier = Modifier.testTag("seller_otp_dialog_confirm")
                    ) {
                        Text("Confirm & Deliver")
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showOtpDialogForOrder = null }) {
                        Text("Cancel")
                    }
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerProductsScreen(
    viewModel: MainViewModel,
    onNavigateToAddProduct: () -> Unit,
    onBack: () -> Unit
) {
    val currentSeller by viewModel.currentSellerProfile.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val myProducts = allProducts.filter { it.sellerId == (currentSeller?.id ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Products (${myProducts.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = AgriGreenPrimary,
                contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(myProducts) { product ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(64.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            com.example.ui.components.ProductThumbnail(
                                product = product,
                                modifier = Modifier.fillMaxSize(),
                                showBadges = false
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.brand, fontSize = 11.sp, color = TextSecondary)
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("₹${product.price.toInt()} / ${product.unit}", fontSize = 13.sp, color = AgriGreenPrimary, fontWeight = FontWeight.SemiBold)
                            Text("Current Stock: ${product.stockQuantity} units", fontSize = 12.sp, color = if (product.stockQuantity > 5) StatusSuccess else StatusError)
                        }

                        Switch(
                            checked = product.isAvailable,
                            onCheckedChange = { viewModel.toggleProductAvailability(product.id) }
                        )
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerAddProductScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val categories by viewModel.categories.collectAsState()

    var name by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("Irrigation") }
    var subcategory by remember { mutableStateOf("Drip Pipes") }
    var brand by remember { mutableStateOf("Jain Irrigation") }
    var sku by remember { mutableStateOf("AGRI-IRR-001") }
    var description by remember { mutableStateOf("Agricultural grade durable equipment with ISI standard.") }
    var priceText by remember { mutableStateOf("1500") }
    var unit by remember { mutableStateOf("Piece") }
    var stockText by remember { mutableStateOf("25") }
    var minOrderText by remember { mutableStateOf("1") }
    var imageUrl by remember { mutableStateOf("https://images.unsplash.com/photo-1592417817098-8f3d69106093?w=500") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add Agricultural Product") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        ) {
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Product Name (e.g. 16mm Drip Pipe, 5HP Motor)") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Category selector
            Text("Category", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = TextSecondary)
            Spacer(modifier = Modifier.height(4.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("Irrigation", "Agricultural Tools", "Farm Equipment", "Electrical").forEach { cat ->
                    FilterChip(
                        selected = selectedCategory == cat,
                        onClick = {
                            selectedCategory = cat
                            subcategory = when (cat) {
                                "Irrigation" -> "Drip Pipes"
                                "Agricultural Tools" -> "Spray Pumps"
                                "Farm Equipment" -> "Water Pumps"
                                else -> "Starters"
                            }
                        },
                        label = { Text(cat, fontSize = 11.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = brand,
                    onValueChange = { brand = it },
                    label = { Text("Brand") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = sku,
                    onValueChange = { sku = it },
                    label = { Text("SKU Code") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = priceText,
                    onValueChange = { priceText = it },
                    label = { Text("Price (₹)") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = unit,
                    onValueChange = { unit = it },
                    label = { Text("Unit (Piece, Roll, Bundle)") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = stockText,
                    onValueChange = { stockText = it },
                    label = { Text("Stock Quantity") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = minOrderText,
                    onValueChange = { minOrderText = it },
                    label = { Text("Min Order Qty") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description & Specifications") },
                maxLines = 3,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = {
                    viewModel.addSellerProduct(
                        name = name,
                        category = selectedCategory,
                        subcategory = subcategory,
                        brand = brand,
                        sku = sku,
                        description = description,
                        price = priceText.toDoubleOrNull() ?: 500.0,
                        discountPrice = null,
                        unit = unit,
                        stock = stockText.toIntOrNull() ?: 10,
                        minOrderQty = minOrderText.toIntOrNull() ?: 1,
                        imageUrl = imageUrl
                    )
                },
                enabled = name.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
            ) {
                Text("Publish Hardware Product", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerInventoryScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentSeller by viewModel.currentSellerProfile.collectAsState()
    val allProducts by viewModel.allProducts.collectAsState()
    val myProducts = allProducts.filter { it.sellerId == (currentSeller?.id ?: "") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Quick Stock Counter") },
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
                .padding(padding),
            contentPadding = PaddingValues(16.dp)
        ) {
            items(myProducts) { product ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(50.dp)
                                .clip(RoundedCornerShape(8.dp))
                        ) {
                            com.example.ui.components.ProductThumbnail(
                                product = product,
                                modifier = Modifier.fillMaxSize(),
                                showBadges = false
                            )
                        }

                        Spacer(modifier = Modifier.width(12.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(product.name, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("SKU: ${product.sku} • ₹${product.price.toInt()}", fontSize = 12.sp, color = TextSecondary)
                        }

                        // Quick +/- buttons
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .background(AgriSurfaceVariant, RoundedCornerShape(8.dp))
                                .padding(4.dp)
                        ) {
                            IconButton(
                                onClick = { viewModel.updateStock(product.id, (product.stockQuantity - 1).coerceAtLeast(0)) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                            }

                            Text(
                                text = "${product.stockQuantity}",
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                modifier = Modifier.padding(horizontal = 8.dp)
                            )

                            IconButton(
                                onClick = { viewModel.updateStock(product.id, product.stockQuantity + 1) },
                                modifier = Modifier.size(32.dp)
                            ) {
                                Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    }
}
