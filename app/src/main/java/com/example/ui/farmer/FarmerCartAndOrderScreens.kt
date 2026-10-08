package com.example.ui.farmer

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.*
import com.example.ui.MainViewModel
import com.example.ui.components.DeliveryOtpCard
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.RatingBar
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerCartScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit,
    onProceedToCheckout: () -> Unit
) {
    val cartItems by viewModel.cartItems.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()

    val subtotal = cartItems.sumOf { it.subtotal }
    val deliveryFee = if (subtotal >= appSettings.minOrderAmountForFreeDelivery || subtotal == 0.0) 0.0 else appSettings.baseDeliveryCharge
    val grandTotal = subtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farm Hardware Cart (${cartItems.size})", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            if (cartItems.isNotEmpty()) {
                Surface(
                    shadowElevation = 8.dp,
                    color = MaterialTheme.colorScheme.surface,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Total Amount", fontSize = 12.sp, color = TextSecondary)
                                Text(
                                    "₹${grandTotal.toInt()}",
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }

                            Button(
                                onClick = onProceedToCheckout,
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier
                                    .height(48.dp)
                                    .testTag("proceed_checkout_btn")
                            ) {
                                Text("Proceed to Checkout", fontWeight = FontWeight.Bold)
                                Spacer(modifier = Modifier.width(6.dp))
                                Icon(Icons.Default.ArrowForward, contentDescription = null, modifier = Modifier.size(16.dp))
                            }
                        }
                    }
                }
            }
        }
    ) { padding ->
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        Icons.Default.RemoveShoppingCart,
                        contentDescription = null,
                        modifier = Modifier.size(72.dp),
                        tint = TextTertiary
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Your cart is empty", fontSize = 18.sp, fontWeight = FontWeight.Bold)
                    Text("Browse nearby agricultural hardware to add items", fontSize = 13.sp, color = TextSecondary)
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                // Shop notice banner
                item {
                    val shopName = cartItems.first().product.sellerName
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.Store, contentDescription = null, tint = AgriGreenPrimary)
                            Spacer(modifier = Modifier.width(10.dp))
                            Column {
                                Text("Single-Shop Order", fontSize = 11.sp, color = TextSecondary)
                                Text("Fulfilling from: $shopName", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = OnAgriGreenContainer)
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                }

                // Cart Items
                items(cartItems) { item ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AgriBorder),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(72.dp)
                                    .clip(RoundedCornerShape(8.dp))
                            ) {
                                com.example.ui.components.ProductThumbnail(
                                    product = item.product,
                                    modifier = Modifier.fillMaxSize(),
                                    showBadges = false
                                )
                            }

                            Spacer(modifier = Modifier.width(12.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = item.product.name,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 14.sp,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )
                                Text(
                                    text = "₹${item.effectivePrice.toInt()} per ${item.product.unit}",
                                    fontSize = 12.sp,
                                    color = TextSecondary
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    // Stepper
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        modifier = Modifier
                                            .background(AgriSurfaceVariant, RoundedCornerShape(8.dp))
                                            .padding(horizontal = 4.dp, vertical = 2.dp)
                                    ) {
                                        IconButton(
                                            onClick = { viewModel.updateCartQty(item.product.id, item.quantity - 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Remove, contentDescription = "Decrease", modifier = Modifier.size(16.dp))
                                        }

                                        Text(
                                            text = "${item.quantity}",
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 14.sp,
                                            modifier = Modifier.padding(horizontal = 8.dp)
                                        )

                                        IconButton(
                                            onClick = { viewModel.updateCartQty(item.product.id, item.quantity + 1) },
                                            modifier = Modifier.size(28.dp)
                                        ) {
                                            Icon(Icons.Default.Add, contentDescription = "Increase", modifier = Modifier.size(16.dp))
                                        }
                                    }

                                    Text(
                                        text = "₹${item.subtotal.toInt()}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                        }
                    }
                }

                // Bill details
                item {
                    Spacer(modifier = Modifier.height(12.dp))
                    Card(
                        colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text("BILL DETAILS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, letterSpacing = 1.sp)
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Items Subtotal", fontSize = 13.sp)
                                Text("₹${subtotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Medium)
                            }
                            Spacer(modifier = Modifier.height(4.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Farm Delivery Fee", fontSize = 13.sp)
                                Text(
                                    if (deliveryFee == 0.0) "FREE" else "₹${deliveryFee.toInt()}",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = if (deliveryFee == 0.0) StatusSuccess else TextPrimary
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(8.dp))

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Grand Total", fontSize = 15.sp, fontWeight = FontWeight.Bold)
                                Text("₹${grandTotal.toInt()}", fontSize = 16.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerCheckoutScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val cartItems by viewModel.cartItems.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()

    var selectedPayment by remember { mutableStateOf(PaymentMethod.COD) }
    var deliveryNotes by remember { mutableStateOf("Leave near farm gate pump house") }

    val subtotal = cartItems.sumOf { it.subtotal }
    val deliveryFee = if (subtotal >= appSettings.minOrderAmountForFreeDelivery) 0.0 else appSettings.baseDeliveryCharge
    val grandTotal = subtotal + deliveryFee

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Confirm Farm Order") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        bottomBar = {
            Surface(
                shadowElevation = 8.dp,
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Button(
                        onClick = { viewModel.placeFarmerOrder(selectedPayment) },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(50.dp)
                            .testTag("confirm_place_order_btn")
                    ) {
                        Text(
                            text = if (selectedPayment == PaymentMethod.COD) "Place Order with Cash on Delivery (₹${grandTotal.toInt()})"
                            else "Pay Online & Place Order (₹${grandTotal.toInt()})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                    }
                }
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
            // Delivery Address Card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgriGreenPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("DELIVERY LOCATION", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(currentUser?.name ?: "Farmer", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(currentUser?.address ?: "Farm Plot 42, Anand", fontSize = 13.sp, color = TextPrimary)
                    Text(currentUser?.phone ?: "+91 98250 11223", fontSize = 12.sp, color = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Order Matching Info
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Bolt, contentDescription = null, tint = AgriGreenPrimary)
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Real-time Order Matching: Nearby approved dealers receive this request immediately. You will receive a handover OTP to verify delivery.",
                        fontSize = 12.sp,
                        color = Color(0xFF1B5E20),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Text("SELECT PAYMENT MODE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(8.dp))

            // COD Option
            Card(
                onClick = { selectedPayment = PaymentMethod.COD },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPayment == PaymentMethod.COD) AgriGreenContainer else AgriSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPayment == PaymentMethod.COD,
                        onClick = { selectedPayment = PaymentMethod.COD }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Cash on Delivery (COD)", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Pay shop person after inspecting items at your farm", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // UPI / Online Option
            Card(
                onClick = { selectedPayment = PaymentMethod.ONLINE_UPI },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (selectedPayment == PaymentMethod.ONLINE_UPI) AgriGreenContainer else AgriSurfaceVariant
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = selectedPayment == PaymentMethod.ONLINE_UPI,
                        onClick = { selectedPayment = PaymentMethod.ONLINE_UPI }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Instant UPI / NetBanking", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Google Pay, PhonePe, Paytm, BHIM UPI", fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerOrderTrackingScreen(
    orderId: String,
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val allOrders by viewModel.allOrders.collectAsState()
    val order = allOrders.find { it.id == orderId }

    var userRating by remember { mutableIntStateOf(5) }
    var userComment by remember { mutableStateOf("") }
    var reviewSubmitted by remember { mutableStateOf(false) }

    if (order == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Order not found")
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Track Order ${order.orderNumber}") },
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
            // Status Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Text("Total: ₹${order.grandTotal.toInt()}", fontSize = 14.sp, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.SemiBold)
                }
                OrderStatusBadge(order.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // DELIVERY OTP CARD (when out for delivery)
            if (order.status == OrderStatus.OUT_FOR_DELIVERY) {
                DeliveryOtpCard(otp = order.deliveryOtp)
                Spacer(modifier = Modifier.height(16.dp))
            }

            // Dealer information card
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Assigned Dealer", fontSize = 11.sp, color = TextSecondary)
                        Text(order.sellerName.ifEmpty { "Finding nearby dealer..." }, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                    Surface(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            Icons.Default.Call,
                            contentDescription = "Call",
                            tint = Color.White,
                            modifier = Modifier.padding(8.dp).size(18.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Order Timeline
            Text("ORDER STATUS TIMELINE", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, letterSpacing = 1.sp)
            Spacer(modifier = Modifier.height(12.dp))

            order.timeline.forEachIndexed { index, entry ->
                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(AgriGreenPrimary),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(14.dp))
                        }
                        if (index < order.timeline.size - 1) {
                            Box(
                                modifier = Modifier
                                    .width(2.dp)
                                    .height(36.dp)
                                    .background(AgriGreenPrimary)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.padding(bottom = 16.dp)) {
                        Text(entry.status.name.replace('_', ' '), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text(entry.note, fontSize = 12.sp, color = TextSecondary)
                    }
                }
            }

            // REVIEW & RATING CARD (when delivered)
            if (order.status == OrderStatus.DELIVERED && !reviewSubmitted) {
                Spacer(modifier = Modifier.height(16.dp))
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Rate Your Experience", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                        Text("How was the hardware quality & delivery speed?", fontSize = 12.sp, color = TextSecondary)

                        Spacer(modifier = Modifier.height(8.dp))
                        RatingBar(rating = userRating, onRatingChange = { userRating = it })

                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = userComment,
                            onValueChange = { userComment = it },
                            placeholder = { Text("Write a comment for ${order.sellerName}...") },
                            modifier = Modifier.fillMaxWidth()
                        )

                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = {
                                viewModel.submitReview(
                                    orderId = order.id,
                                    sellerId = order.sellerId ?: "",
                                    rating = userRating,
                                    comment = userComment
                                )
                                reviewSubmitted = true
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Submit Review")
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerOrdersScreen(
    viewModel: MainViewModel,
    onNavigateToTracking: (String) -> Unit,
    onBack: () -> Unit
) {
    val farmerOrders by viewModel.farmerOrders.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("My Orders (${farmerOrders.size})") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (farmerOrders.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("No orders placed yet.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(farmerOrders) { order ->
                    Card(
                        onClick = { onNavigateToTracking(order.id) },
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
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
                            Text("Shop: ${order.sellerName}", fontSize = 13.sp, color = TextSecondary)
                            Text("${order.items.size} item(s) • Total: ₹${order.grandTotal.toInt()}", fontSize = 13.sp, fontWeight = FontWeight.Medium)

                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                TextButton(onClick = { onNavigateToTracking(order.id) }) {
                                    Text("Track Order →", fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FarmerProfileScreen(
    viewModel: MainViewModel,
    onBack: () -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val farmerProfile by viewModel.currentFarmerProfile.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Farmer Profile") },
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
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = AgriGreenContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(RoundedCornerShape(28.dp))
                            .background(AgriGreenPrimary),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Person, contentDescription = null, tint = Color.White, modifier = Modifier.size(32.dp))
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Column {
                        Text(currentUser?.name ?: "Mukesh Bhai Patel", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = OnAgriGreenContainer)
                        Text(currentUser?.phone ?: "+91 98250 11223", fontSize = 13.sp, color = TextSecondary)
                        Text("Role: FARMER / BUYER", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Agricultural Details Card
            farmerProfile?.let { fp ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("FARM & AGRICULTURAL DETAILS", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Farm Name", fontSize = 13.sp, color = TextSecondary)
                            Text(fp.farmName ?: "Patel Krushi Farm", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Land Size", fontSize = 13.sp, color = TextSecondary)
                            Text("${fp.farmSizeAcres ?: 12.5} Acres", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Irrigation Type", fontSize = 13.sp, color = TextSecondary)
                            Text(fp.irrigationType ?: "Drip & Openwell", fontSize = 13.sp, fontWeight = FontWeight.SemiBold, color = AgriGreenPrimary)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Crops Cultivated", fontSize = 13.sp, color = TextSecondary)
                            Text(fp.primaryCrops.joinToString(", "), fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Saved Farm Delivery Locations
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("SAVED DELIVERY LOCATIONS (${fp.savedAddresses.size})", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary, letterSpacing = 1.sp)
                        Spacer(modifier = Modifier.height(10.dp))

                        fp.savedAddresses.forEach { addr ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(Icons.Default.LocationOn, contentDescription = null, tint = AgriGreenPrimary, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text(addr.label, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        if (addr.isDefault) {
                                            Spacer(modifier = Modifier.width(6.dp))
                                            Surface(color = AgriGreenContainer, shape = RoundedCornerShape(4.dp)) {
                                                Text("DEFAULT", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = AgriGreenPrimary, modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp))
                                            }
                                        }
                                    }
                                    Text("${addr.addressLine}, ${addr.villageOrTown} (${addr.pincode})", fontSize = 12.sp, color = TextSecondary)
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Switch to Seller Role Card
            Card(
                onClick = { viewModel.switchRole(UserRole.SELLER) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Storefront, contentDescription = null, tint = HarvestGold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Switch to Hardware Shop Mode", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Manage inventory, view incoming nearby orders", fontSize = 12.sp, color = TextSecondary)
                    }
                    Icon(Icons.Default.SwapHoriz, contentDescription = null)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Open Admin Simulator Card
            Card(
                onClick = { viewModel.toggleAdminPanel(true) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Web Admin Simulator Panel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Approve sellers, set delivery radius, view backend DB", fontSize = 12.sp, color = TextSecondary)
                    }
                    Icon(Icons.Default.Tune, contentDescription = null)
                }
            }
        }
    }
}
