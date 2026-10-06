package com.example.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.*
import com.example.data.repository.AgriMarketplaceRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed class ScreenDestination {
    // Auth & Onboarding
    data object Login : ScreenDestination()
    data class OtpVerify(val phone: String, val role: UserRole) : ScreenDestination()
    data object SellerRegister : ScreenDestination()

    // Farmer destinations
    data object FarmerHome : ScreenDestination()
    data class FarmerCategoryProducts(val categoryName: String) : ScreenDestination()
    data class FarmerProductDetail(val productId: String) : ScreenDestination()
    data class FarmerShopDetail(val sellerId: String) : ScreenDestination()
    data object FarmerCart : ScreenDestination()
    data object FarmerCheckout : ScreenDestination()
    data class FarmerOrderTracking(val orderId: String) : ScreenDestination()
    data object FarmerOrders : ScreenDestination()
    data object FarmerProfile : ScreenDestination()
    data object FarmerNotifications : ScreenDestination()

    // Seller destinations
    data object SellerDashboard : ScreenDestination()
    data object SellerOrders : ScreenDestination()
    data class SellerOrderDetail(val orderId: String) : ScreenDestination()
    data object SellerProducts : ScreenDestination()
    data object SellerAddProduct : ScreenDestination()
    data object SellerInventory : ScreenDestination()
    data object SellerProfileScreen : ScreenDestination()
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    val repository = AgriMarketplaceRepository(application)

    // Current navigation state
    private val _currentScreen = MutableStateFlow<ScreenDestination>(ScreenDestination.FarmerHome)
    val currentScreen: StateFlow<ScreenDestination> = _currentScreen.asStateFlow()

    private val screenBackStack = mutableListOf<ScreenDestination>()

    // Message / Snackbar
    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    // Admin Panel BottomSheet visible toggle
    private val _showAdminPanel = MutableStateFlow(false)
    val showAdminPanel: StateFlow<Boolean> = _showAdminPanel.asStateFlow()

    // Search and filter state for farmer
    val searchQuery = MutableStateFlow("")
    val selectedCategoryFilter = MutableStateFlow<String?>(null)
    val maxPriceFilter = MutableStateFlow<Double?>(null)
    val maxDistanceFilter = MutableStateFlow<Double?>(null)

    // Data streams from repository
    val currentUser = repository.currentUser
    val currentSellerProfile = repository.currentSellerProfile
    val appSettings = repository.appSettings
    val categories = repository.categories
    val allSellers = repository.sellers
    val allProducts = repository.products
    val allOrders = repository.orders
    val orderRequests = repository.orderRequests
    val notifications = repository.notifications
    val cartItems = repository.getCartItemsFlow().stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtered Nearby Approved Shops for Farmer
    val nearbyApprovedShops = combine(currentUser, allSellers, appSettings) { user, sellers, settings ->
        val userLat = user?.latitude ?: 23.0225
        val userLng = user?.longitude ?: 72.5714
        val radius = settings.deliveryRadiusKm
        sellers.filter { it.status == SellerStatus.APPROVED && it.isAvailable }
            .map { seller ->
                val dist = repository.calculateDistanceKm(userLat, userLng, seller.latitude, seller.longitude)
                seller to dist
            }
            .filter { (_, dist) -> dist <= radius }
            .sortedBy { it.second }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Filtered Products for Farmer Discovery
    val filteredProducts = combine(
        allProducts,
        nearbyApprovedShops,
        searchQuery,
        selectedCategoryFilter,
        maxPriceFilter
    ) { products, nearbyShops, query, category, maxPrice ->
        val approvedSellerIds = nearbyShops.map { it.first.id }.toSet()
        products.filter { prod ->
            // Must belong to an approved nearby seller
            approvedSellerIds.contains(prod.sellerId) &&
            prod.isAvailable &&
            (category == null || prod.category.equals(category, ignoreCase = true)) &&
            (query.isEmpty() ||
                    prod.name.contains(query, ignoreCase = true) ||
                    prod.brand.contains(query, ignoreCase = true) ||
                    prod.category.contains(query, ignoreCase = true) ||
                    prod.sku.contains(query, ignoreCase = true)) &&
            (maxPrice == null || prod.price <= maxPrice)
        }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Farmer Orders
    val farmerOrders = combine(currentUser, allOrders) { user, orders ->
        val farmerId = user?.id ?: ""
        orders.filter { it.farmerId == farmerId }.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    // Seller Orders & Requests
    val sellerOrders = combine(currentSellerProfile, allOrders) { seller, orders ->
        val sellerId = seller?.id ?: ""
        orders.filter { it.sellerId == sellerId }.sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    val sellerRequests = combine(currentSellerProfile, orderRequests) { seller, requests ->
        val sellerId = seller?.id ?: ""
        requests.filter { it.sellerId == sellerId && it.status == "PENDING" }
            .sortedByDescending { it.createdAt }
    }.stateIn(viewModelScope, SharingStarted.Lazily, emptyList())

    fun navigateTo(destination: ScreenDestination) {
        if (_currentScreen.value != destination) {
            screenBackStack.add(_currentScreen.value)
            _currentScreen.value = destination
        }
    }

    fun navigateBack(): Boolean {
        if (screenBackStack.isNotEmpty()) {
            _currentScreen.value = screenBackStack.removeAt(screenBackStack.size - 1)
            return true
        }
        return false
    }

    fun showMessage(msg: String) {
        _snackbarMessage.value = msg
    }

    fun dismissMessage() {
        _snackbarMessage.value = null
    }

    fun toggleAdminPanel(show: Boolean) {
        _showAdminPanel.value = show
    }

    // Role switching and fast demo actions
    fun switchRole(role: UserRole) {
        repository.switchUserRole(role)
        screenBackStack.clear()
        if (role == UserRole.FARMER) {
            _currentScreen.value = ScreenDestination.FarmerHome
            showMessage("Switched to Farmer mode (Mukesh Bhai Patel)")
        } else {
            _currentScreen.value = ScreenDestination.SellerDashboard
            showMessage("Switched to Seller mode (Patel Krushi Hardware)")
        }
    }

    fun verifyLoginOtp(phone: String, otp: String, role: UserRole) {
        viewModelScope.launch {
            val result = repository.verifyOtp(phone, otp, role)
            if (result.isSuccess) {
                screenBackStack.clear()
                if (role == UserRole.FARMER) {
                    _currentScreen.value = ScreenDestination.FarmerHome
                    showMessage("Welcome, Farmer!")
                } else {
                    val profile = repository.currentSellerProfile.value
                    if (profile == null) {
                        _currentScreen.value = ScreenDestination.SellerRegister
                    } else {
                        _currentScreen.value = ScreenDestination.SellerDashboard
                        showMessage("Welcome, ${profile.shopName}!")
                    }
                }
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Login failed")
            }
        }
    }

    fun registerSeller(
        ownerName: String,
        phone: String,
        shopName: String,
        shopAddress: String,
        city: String,
        state: String,
        pincode: String,
        latitude: Double,
        longitude: Double,
        gstNumber: String,
        bankAccount: String,
        ifscCode: String
    ) {
        viewModelScope.launch {
            val user = currentUser.value
            val request = com.example.data.api.SellerRegistrationRequest(
                userId = user?.id ?: "user_seller_${System.currentTimeMillis()}",
                ownerName = ownerName,
                phone = phone,
                shopName = shopName,
                shopAddress = shopAddress,
                city = city,
                state = state,
                pincode = pincode,
                latitude = latitude,
                longitude = longitude,
                gstNumber = gstNumber,
                bankAccount = bankAccount,
                ifscCode = ifscCode
            )
            val result = repository.registerSeller(request)
            if (result.isSuccess) {
                _currentScreen.value = ScreenDestination.SellerDashboard
                showMessage("Shop registered! Status is PENDING Admin approval.")
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Registration failed")
            }
        }
    }

    fun addToCart(product: Product, quantity: Int = 1) {
        viewModelScope.launch {
            val result = repository.addToCart(product, quantity)
            if (result.isSuccess) {
                showMessage("Added ${product.name} to cart")
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Cannot add to cart")
            }
        }
    }

    fun updateCartQty(productId: String, newQty: Int) {
        viewModelScope.launch {
            val result = repository.updateCartQuantity(productId, newQty)
            if (result.isFailure) {
                showMessage(result.exceptionOrNull()?.message ?: "Error updating quantity")
            }
        }
    }

    fun removeFromCart(productId: String) {
        viewModelScope.launch {
            repository.removeFromCart(productId)
        }
    }

    fun placeFarmerOrder(paymentMethod: PaymentMethod) {
        val farmer = currentUser.value
        val items = cartItems.value
        if (farmer == null) {
            showMessage("Please log in as Farmer to place an order")
            return
        }
        if (items.isEmpty()) {
            showMessage("Cart is empty")
            return
        }

        viewModelScope.launch {
            val result = repository.placeOrder(farmer, items, paymentMethod)
            if (result.isSuccess) {
                val order = result.getOrNull()!!
                showMessage("Order ${order.orderNumber} placed successfully!")
                _currentScreen.value = ScreenDestination.FarmerOrderTracking(order.id)
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Order placement failed")
            }
        }
    }

    fun sellerAcceptOrder(orderId: String) {
        val seller = currentSellerProfile.value
        if (seller == null) {
            showMessage("No active seller profile")
            return
        }
        viewModelScope.launch {
            val result = repository.acceptOrder(orderId, seller.id)
            if (result.isSuccess) {
                showMessage("Order accepted! Status updated to ACCEPTED.")
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Failed to accept order")
            }
        }
    }

    fun sellerRejectOrder(orderId: String) {
        val seller = currentSellerProfile.value ?: return
        viewModelScope.launch {
            repository.rejectOrderRequest(orderId, seller.id)
            showMessage("Order request declined")
        }
    }

    fun updateOrderStatus(orderId: String, nextStatus: OrderStatus) {
        val seller = currentSellerProfile.value ?: return
        viewModelScope.launch {
            val result = repository.updateOrderStatus(orderId, seller.id, nextStatus)
            if (result.isSuccess) {
                showMessage("Order status updated to ${nextStatus.name}")
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Update failed")
            }
        }
    }

    fun verifyDeliveryOtp(orderId: String, enteredOtp: String) {
        val seller = currentSellerProfile.value ?: return
        viewModelScope.launch {
            val result = repository.verifyDeliveryOtp(orderId, seller.id, enteredOtp)
            if (result.isSuccess) {
                showMessage("OTP Verified! Order marked as DELIVERED.")
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Incorrect OTP")
            }
        }
    }

    fun addSellerProduct(
        name: String,
        category: String,
        subcategory: String,
        brand: String,
        sku: String,
        description: String,
        price: Double,
        discountPrice: Double?,
        unit: String,
        stock: Int,
        minOrderQty: Int,
        imageUrl: String
    ) {
        val seller = currentSellerProfile.value
        if (seller == null) {
            showMessage("No seller profile")
            return
        }
        viewModelScope.launch {
            val request = com.example.data.api.AddProductRequest(
                sellerId = seller.id,
                name = name,
                category = category,
                subcategory = subcategory,
                brand = brand,
                sku = sku,
                description = description,
                price = price,
                discountPrice = discountPrice,
                unit = unit,
                stockQuantity = stock,
                minOrderQuantity = minOrderQty,
                imageUrl = imageUrl
            )
            val result = repository.addProduct(request)
            if (result.isSuccess) {
                showMessage("Product published successfully!")
                _currentScreen.value = ScreenDestination.SellerProducts
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Failed to add product")
            }
        }
    }

    fun updateStock(productId: String, newStock: Int) {
        val seller = currentSellerProfile.value ?: return
        viewModelScope.launch {
            val result = repository.updateProductStock(productId, seller.id, newStock)
            if (result.isSuccess) {
                showMessage("Stock updated")
            }
        }
    }

    fun toggleProductAvailability(productId: String) {
        val seller = currentSellerProfile.value ?: return
        viewModelScope.launch {
            repository.toggleProductAvailability(productId, seller.id)
        }
    }

    fun submitReview(orderId: String, sellerId: String, rating: Int, comment: String) {
        val user = currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.submitReview(
                orderId = orderId,
                sellerId = sellerId,
                farmerId = user.id,
                farmerName = user.name,
                rating = rating,
                comment = comment
            )
            if (result.isSuccess) {
                showMessage("Review submitted! Thank you.")
            } else {
                showMessage(result.exceptionOrNull()?.message ?: "Cannot submit review")
            }
        }
    }

    // Admin testing controls
    fun adminApproveSeller(sellerId: String) {
        repository.adminSetSellerStatus(sellerId, SellerStatus.APPROVED)
        showMessage("Seller approved! They can now receive orders.")
    }

    fun adminRejectSeller(sellerId: String) {
        repository.adminSetSellerStatus(sellerId, SellerStatus.REJECTED)
        showMessage("Seller marked as REJECTED.")
    }

    fun adminUpdateRadius(radiusKm: Double) {
        repository.adminSetDeliveryRadius(radiusKm)
        showMessage("Delivery radius set to ${radiusKm.toInt()} km")
    }
}
