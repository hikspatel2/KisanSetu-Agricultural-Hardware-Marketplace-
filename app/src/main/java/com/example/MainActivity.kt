package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.admin.AdminPanelSheet
import com.example.ui.auth.LoginScreen
import com.example.ui.auth.OtpVerificationScreen
import com.example.ui.auth.SellerRegistrationScreen
import com.example.ui.farmer.*
import com.example.ui.seller.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                KisanSetuApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun KisanSetuApp(viewModel: MainViewModel) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val snackbarMsg by viewModel.snackbarMessage.collectAsState()
    val showAdminPanel by viewModel.showAdminPanel.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let {
            snackbarHostState.showSnackbar(it)
            viewModel.dismissMessage()
        }
    }

    // Hardware back button support
    val isRootScreen = currentScreen is ScreenDestination.FarmerHome ||
            currentScreen is ScreenDestination.SellerDashboard ||
            currentScreen is ScreenDestination.Login

    BackHandler(enabled = !isRootScreen) {
        viewModel.navigateBack()
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when (val screen = currentScreen) {
                is ScreenDestination.Login -> {
                    LoginScreen(
                        viewModel = viewModel,
                        onNavigateToOtp = { phone, role ->
                            viewModel.navigateTo(ScreenDestination.OtpVerify(phone, role))
                        },
                        onRegisterSellerClick = {
                            viewModel.navigateTo(ScreenDestination.SellerRegister)
                        }
                    )
                }

                is ScreenDestination.OtpVerify -> {
                    OtpVerificationScreen(
                        phone = screen.phone,
                        role = screen.role,
                        onVerifyOtp = { otp ->
                            viewModel.verifyLoginOtp(screen.phone, otp, screen.role)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.SellerRegister -> {
                    SellerRegistrationScreen(
                        onRegister = { ownerName, phone, shopName, shopAddress, city, state, pincode, lat, lng, gst, bank, ifsc ->
                            viewModel.registerSeller(ownerName, phone, shopName, shopAddress, city, state, pincode, lat, lng, gst, bank, ifsc)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                // Farmer Flow
                is ScreenDestination.FarmerHome -> {
                    FarmerHomeScreen(
                        viewModel = viewModel,
                        onNavigateToProduct = { productId ->
                            viewModel.navigateTo(ScreenDestination.FarmerProductDetail(productId))
                        },
                        onNavigateToShop = { sellerId ->
                            viewModel.navigateTo(ScreenDestination.FarmerShopDetail(sellerId))
                        },
                        onNavigateToCategory = { catName ->
                            viewModel.selectedCategoryFilter.value = catName
                        },
                        onNavigateToCart = {
                            viewModel.navigateTo(ScreenDestination.FarmerCart)
                        },
                        onNavigateToOrders = {
                            viewModel.navigateTo(ScreenDestination.FarmerOrders)
                        },
                        onNavigateToProfile = {
                            viewModel.navigateTo(ScreenDestination.FarmerProfile)
                        }
                    )
                }

                is ScreenDestination.AgriculturalEquipment -> {
                    AgriculturalEquipmentScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onNavigateToProduct = { productId ->
                            viewModel.navigateTo(ScreenDestination.FarmerProductDetail(productId))
                        },
                        onNavigateToCart = {
                            viewModel.navigateTo(ScreenDestination.FarmerCart)
                        }
                    )
                }

                is ScreenDestination.FarmerProductDetail -> {
                    FarmerProductDetailScreen(
                        productId = screen.productId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onNavigateToShop = { sellerId ->
                            viewModel.navigateTo(ScreenDestination.FarmerShopDetail(sellerId))
                        },
                        onNavigateToCart = {
                            viewModel.navigateTo(ScreenDestination.FarmerCart)
                        }
                    )
                }

                is ScreenDestination.FarmerShopDetail -> {
                    FarmerShopDetailScreen(
                        sellerId = screen.sellerId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onNavigateToProduct = { productId ->
                            viewModel.navigateTo(ScreenDestination.FarmerProductDetail(productId))
                        }
                    )
                }

                is ScreenDestination.FarmerCart -> {
                    FarmerCartScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() },
                        onProceedToCheckout = {
                            viewModel.navigateTo(ScreenDestination.FarmerCheckout)
                        }
                    )
                }

                is ScreenDestination.FarmerCheckout -> {
                    FarmerCheckoutScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.FarmerOrderTracking -> {
                    FarmerOrderTrackingScreen(
                        orderId = screen.orderId,
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.FarmerOrders -> {
                    FarmerOrdersScreen(
                        viewModel = viewModel,
                        onNavigateToTracking = { orderId ->
                            viewModel.navigateTo(ScreenDestination.FarmerOrderTracking(orderId))
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.FarmerProfile -> {
                    FarmerProfileScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                // Seller Flow
                is ScreenDestination.SellerDashboard -> {
                    SellerDashboardScreen(
                        viewModel = viewModel,
                        onNavigateToOrders = {
                            viewModel.navigateTo(ScreenDestination.SellerOrders)
                        },
                        onNavigateToProducts = {
                            viewModel.navigateTo(ScreenDestination.SellerProducts)
                        },
                        onNavigateToAddProduct = {
                            viewModel.navigateTo(ScreenDestination.SellerAddProduct)
                        },
                        onNavigateToInventory = {
                            viewModel.navigateTo(ScreenDestination.SellerInventory)
                        },
                        onNavigateToProfile = {
                            viewModel.navigateTo(ScreenDestination.FarmerProfile)
                        }
                    )
                }

                is ScreenDestination.SellerOrders -> {
                    SellerOrdersScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.SellerProducts -> {
                    SellerProductsScreen(
                        viewModel = viewModel,
                        onNavigateToAddProduct = {
                            viewModel.navigateTo(ScreenDestination.SellerAddProduct)
                        },
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.SellerAddProduct -> {
                    SellerAddProductScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                is ScreenDestination.SellerInventory -> {
                    SellerInventoryScreen(
                        viewModel = viewModel,
                        onBack = { viewModel.navigateBack() }
                    )
                }

                else -> {
                    FarmerHomeScreen(
                        viewModel = viewModel,
                        onNavigateToProduct = { id -> viewModel.navigateTo(ScreenDestination.FarmerProductDetail(id)) },
                        onNavigateToShop = { id -> viewModel.navigateTo(ScreenDestination.FarmerShopDetail(id)) },
                        onNavigateToCategory = { cat -> viewModel.selectedCategoryFilter.value = cat },
                        onNavigateToCart = { viewModel.navigateTo(ScreenDestination.FarmerCart) },
                        onNavigateToOrders = { viewModel.navigateTo(ScreenDestination.FarmerOrders) },
                        onNavigateToProfile = { viewModel.navigateTo(ScreenDestination.FarmerProfile) }
                    )
                }
            }

            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 80.dp)
            )

            if (showAdminPanel) {
                AdminPanelSheet(
                    viewModel = viewModel,
                    onDismiss = { viewModel.toggleAdminPanel(false) }
                )
            }
        }
    }
