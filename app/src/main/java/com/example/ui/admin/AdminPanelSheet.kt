package com.example.ui.admin

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SellerStatus
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.SellerStatusBadge
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminPanelSheet(
    viewModel: MainViewModel,
    onDismiss: () -> Unit
) {
    val allSellers by viewModel.allSellers.collectAsState()
    val allOrders by viewModel.allOrders.collectAsState()
    val appSettings by viewModel.appSettings.collectAsState()

    var selectedTab by remember { mutableIntStateOf(0) }
    var radiusSlider by remember(appSettings.deliveryRadiusKm) { mutableFloatStateOf(appSettings.deliveryRadiusKm.toFloat()) }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.85f)
                .padding(horizontal = 16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = null,
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Web Admin Simulator Panel", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }

                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close")
                }
            }

            Text(
                "Simulates the Web Admin Panel sharing the same backend database.",
                fontSize = 12.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(12.dp))

            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Sellers (${allSellers.size})", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Radius & Config", fontSize = 12.sp) }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Global Orders (${allOrders.size})", fontSize = 12.sp) }
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when (selectedTab) {
                0 -> {
                    // Sellers management: Approve / Reject
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(allSellers) { seller ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 10.dp)
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(seller.shopName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                        SellerStatusBadge(status = seller.status)
                                    }

                                    Text("Owner: ${seller.ownerName} • ${seller.city}", fontSize = 12.sp, color = TextSecondary)
                                    Text("GPS: ${seller.latitude}, ${seller.longitude}", fontSize = 11.sp, color = TextTertiary)

                                    Spacer(modifier = Modifier.height(8.dp))

                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                                    ) {
                                        if (seller.status != SellerStatus.APPROVED) {
                                            Button(
                                                onClick = { viewModel.adminApproveSeller(seller.id) },
                                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Approve Shop", fontSize = 12.sp)
                                            }
                                        }

                                        if (seller.status != SellerStatus.REJECTED) {
                                            OutlinedButton(
                                                onClick = { viewModel.adminRejectSeller(seller.id) },
                                                modifier = Modifier.weight(1f)
                                            ) {
                                                Text("Reject Shop", fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
                1 -> {
                    // Settings & Radius Configuration
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = 12.dp)
                    ) {
                        Text("DELIVERY RADIUS CONFIGURATION", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))

                        Card(
                            colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text("Max Discovery Radius", fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                                    Text("${radiusSlider.toInt()} KM", fontWeight = FontWeight.Bold, color = AgriGreenPrimary, fontSize = 16.sp)
                                }

                                Slider(
                                    value = radiusSlider,
                                    onValueChange = { radiusSlider = it },
                                    valueRange = 5f..50f,
                                    steps = 8
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                                Button(
                                    onClick = { viewModel.adminUpdateRadius(radiusSlider.toDouble()) },
                                    colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text("Save Radius to Backend Settings")
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))
                        Text("QUICK ROLE SWITCHER", fontWeight = FontWeight.Bold, fontSize = 12.sp, color = TextSecondary)
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Button(
                                onClick = {
                                    viewModel.switchRole(UserRole.FARMER)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = AgriGreenPrimary),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🌾 Farmer Role", fontSize = 12.sp)
                            }

                            Button(
                                onClick = {
                                    viewModel.switchRole(UserRole.SELLER)
                                    onDismiss()
                                },
                                colors = ButtonDefaults.buttonColors(containerColor = HarvestGold),
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("🏪 Seller Role", fontSize = 12.sp)
                            }
                        }
                    }
                }
                2 -> {
                    // Global Orders list
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp)
                    ) {
                        items(allOrders) { order ->
                            Card(
                                shape = RoundedCornerShape(10.dp),
                                colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(bottom = 8.dp)
                            ) {
                                Column(modifier = Modifier.padding(10.dp)) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween
                                    ) {
                                        Text(order.orderNumber, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                        OrderStatusBadge(order.status)
                                    }
                                    Text("Farmer: ${order.farmerName} • Shop: ${order.sellerName}", fontSize = 12.sp, color = TextSecondary)
                                    Text("Grand Total: ₹${order.grandTotal.toInt()} • Delivery OTP: ${order.deliveryOtp}", fontSize = 12.sp, fontWeight = FontWeight.Medium)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
