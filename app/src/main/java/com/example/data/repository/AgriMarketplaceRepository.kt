package com.example.data.repository

import android.content.Context
import com.example.data.api.AddProductRequest
import com.example.data.api.SellerRegistrationRequest
import com.example.data.local.AppDatabase
import com.example.data.local.LocalCartItem
import com.example.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlin.math.*

class AgriMarketplaceRepository(private val context: Context) :
    IAuthRepository,
    IFarmerProfileRepository,
    ISellerProfileRepository {

    private val db = AppDatabase.getDatabase(context)
    private val cartDao = db.cartDao()
    private val scope = CoroutineScope(Dispatchers.IO)
    private val orderMutex = Mutex()

    // -------------------------------------------------------------------------
    // Session State
    // -------------------------------------------------------------------------
    private val _currentUser = MutableStateFlow<User?>(null)
    override val currentUserFlow: Flow<User?> = _currentUser.asStateFlow()
    val currentUser: StateFlow<User?> = _currentUser.asStateFlow()

    private val _currentFarmerProfile = MutableStateFlow<FarmerProfile?>(null)
    val currentFarmerProfile: StateFlow<FarmerProfile?> = _currentFarmerProfile.asStateFlow()

    private val _currentSellerProfile = MutableStateFlow<SellerProfile?>(null)
    override val currentSellerProfileFlow: Flow<SellerProfile?> = _currentSellerProfile.asStateFlow()
    val currentSellerProfile: StateFlow<SellerProfile?> = _currentSellerProfile.asStateFlow()

    private val _currentSession = MutableStateFlow<AuthSession?>(null)
    override val currentSessionFlow: Flow<AuthSession?> = _currentSession.asStateFlow()

    // App Settings
    private val _appSettings = MutableStateFlow(AppSettings())
    val appSettings: StateFlow<AppSettings> = _appSettings.asStateFlow()

    // Notifications
    private val _notifications = MutableStateFlow<List<AppNotification>>(emptyList())
    val notifications: StateFlow<List<AppNotification>> = _notifications.asStateFlow()

    // -------------------------------------------------------------------------
    // Shared Server Database Emulation
    // -------------------------------------------------------------------------
    private val _categories = MutableStateFlow<List<Category>>(emptyList())
    val categories: StateFlow<List<Category>> = _categories.asStateFlow()

    private val _sellers = MutableStateFlow<List<SellerProfile>>(emptyList())
    val sellers: StateFlow<List<SellerProfile>> = _sellers.asStateFlow()

    private val _products = MutableStateFlow<List<Product>>(emptyList())
    val products: StateFlow<List<Product>> = _products.asStateFlow()

    private val _orders = MutableStateFlow<List<Order>>(emptyList())
    val orders: StateFlow<List<Order>> = _orders.asStateFlow()

    private val _orderRequests = MutableStateFlow<List<OrderRequest>>(emptyList())
    val orderRequests: StateFlow<List<OrderRequest>> = _orderRequests.asStateFlow()

    private val _reviews = MutableStateFlow<List<Review>>(emptyList())
    val reviews: StateFlow<List<Review>> = _reviews.asStateFlow()

    init {
        seedInitialDatabase()
        // Default login as Farmer for immediate rich experience
        loginAsDefaultFarmer()
    }

    private fun seedInitialDatabase() {
        val seededCategories = listOf(
            Category(
                id = "cat_irrigation",
                name = "Irrigation",
                iconName = "water_drop",
                subcategories = listOf("Drip Pipes", "PVC Pipes", "HDPE Pipes", "Sprinklers", "Drippers", "Valves", "Filters", "Connectors")
            ),
            Category(
                id = "cat_tools",
                name = "Agricultural Tools",
                iconName = "build",
                subcategories = listOf("Spray Pumps", "Hand Tools", "Pruning Tools", "Cutters")
            ),
            Category(
                id = "cat_equipment",
                name = "Farm Equipment",
                iconName = "agriculture",
                subcategories = listOf("Water Pumps", "Motors", "Machinery Parts")
            ),
            Category(
                id = "cat_electrical",
                name = "Electrical",
                iconName = "bolt",
                subcategories = listOf("Cables", "Starters", "Capacitors", "Switches")
            ),
            Category(
                id = "cat_hardware",
                name = "Other Hardware",
                iconName = "handyman",
                subcategories = listOf("Bearings", "Belts", "Nuts & Bolts", "Fittings", "Tapes")
            )
        )
        _categories.value = seededCategories

        // Seeded Sellers (Lat/Lng around Anand/Vadodara agricultural area in Gujarat: 23.0225, 72.5714)
        val seller1 = SellerProfile(
            id = "seller_patel",
            userId = "user_seller_1",
            ownerName = "Ramesh Patel",
            shopName = "Patel Krushi Hardware",
            shopAddress = "Station Road, Near APMC Market",
            city = "Anand",
            state = "Gujarat",
            pincode = "388001",
            latitude = 23.0250,
            longitude = 72.5750, // ~0.5 km away
            shopImageUrl = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=400",
            gstNumber = "24AAECP1234F1Z5",
            bankAccount = "919293949596",
            ifscCode = "SBIN0001234",
            status = SellerStatus.APPROVED,
            rating = 4.8,
            totalRatings = 34
        )

        val seller2 = SellerProfile(
            id = "seller_kisan_center",
            userId = "user_seller_2",
            ownerName = "Kishore Bhai",
            shopName = "Kisan Agro Hardware & Drip Center",
            shopAddress = "Highway Cross Road, Taluka Bazaar",
            city = "Nadiad",
            state = "Gujarat",
            pincode = "387001",
            latitude = 23.0400,
            longitude = 72.5900, // ~2.8 km away
            shopImageUrl = "https://images.unsplash.com/photo-1513836279014-a89f7a76ae86?w=400",
            gstNumber = "24BBCDP5678K1Z2",
            bankAccount = "818283848586",
            ifscCode = "BARB0NADIAD",
            status = SellerStatus.APPROVED,
            rating = 4.6,
            totalRatings = 21
        )

        val seller3 = SellerProfile(
            id = "seller_shree_ram",
            userId = "user_seller_3",
            ownerName = "Mahesh Shah",
            shopName = "Shree Ram Farm Spares & Pumps",
            shopAddress = "GIDC Main Road, Sector 3",
            city = "Anand",
            state = "Gujarat",
            pincode = "388121",
            latitude = 23.0700,
            longitude = 72.6100, // ~6.5 km away
            shopImageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=400",
            gstNumber = "24CCFPR9012M1Z8",
            bankAccount = "717273747576",
            ifscCode = "HDFC0004321",
            status = SellerStatus.APPROVED,
            rating = 4.9,
            totalRatings = 48
        )

        val pendingSeller = SellerProfile(
            id = "seller_pending_1",
            userId = "user_seller_pending",
            ownerName = "Dinesh Prajapati",
            shopName = "GreenField Agro Spares",
            shopAddress = "Village Gate, Borsad Road",
            city = "Anand",
            state = "Gujarat",
            pincode = "388540",
            latitude = 23.0550,
            longitude = 72.5850,
            shopImageUrl = "https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?w=400",
            gstNumber = "24DDGPQ3456N1Z1",
            status = SellerStatus.PENDING,
            rating = 5.0,
            totalRatings = 0
        )

        _sellers.value = listOf(seller1, seller2, seller3, pendingSeller)

        // Seeded Products
        val seededProducts = listOf(
            Product(
                id = "p_drip_16mm",
                sellerId = "seller_patel",
                sellerName = "Patel Krushi Hardware",
                name = "Jain Inline 16mm Drip Pipe (400m Roll)",
                category = "Irrigation",
                subcategory = "Drip Pipes",
                brand = "Jain Irrigation",
                sku = "IRR-JAIN-16-400",
                description = "High UV resistant 16mm inline drip lateral pipe with 40cm dripper spacing. Ideal for cotton, banana, and vegetables.",
                imageUrl = "https://images.unsplash.com/photo-1592417817098-8f3d69106093?w=500",
                price = 3200.0,
                discountPrice = 2850.0,
                unit = "Roll",
                stockQuantity = 24,
                minOrderQuantity = 1,
                rating = 4.8
            ),
            Product(
                id = "p_pvc_4inch",
                sellerId = "seller_patel",
                sellerName = "Patel Krushi Hardware",
                name = "Supreme 4-inch PVC Agricultural Pipe (6kg/cm²)",
                category = "Irrigation",
                subcategory = "PVC Pipes",
                brand = "Supreme",
                sku = "IRR-SUP-PVC-4IN",
                description = "Rigid 20-foot PVC pipe with rubber seal socket for borewell discharge and main irrigation delivery pipelines.",
                imageUrl = "https://images.unsplash.com/photo-1541888946425-d0fbb186c5f9?w=500",
                price = 1150.0,
                discountPrice = 990.0,
                unit = "Piece (20ft)",
                stockQuantity = 45,
                minOrderQuantity = 2,
                rating = 4.7
            ),
            Product(
                id = "p_spray_pump_16l",
                sellerId = "seller_patel",
                sellerName = "Patel Krushi Hardware",
                name = "Neptune 16L Battery Knapsack Sprayer",
                category = "Agricultural Tools",
                subcategory = "Spray Pumps",
                brand = "Neptune",
                sku = "TOOL-NEP-BAT-16",
                description = "Dual-function 12V 12Ah battery and manual spray pump with brass telescopic lance and 4 adjustable spray nozzles.",
                imageUrl = "https://images.unsplash.com/photo-1615811361523-6bd03d7748e7?w=500",
                price = 2800.0,
                discountPrice = 2450.0,
                unit = "Piece",
                stockQuantity = 15,
                minOrderQuantity = 1,
                rating = 4.9
            ),
            Product(
                id = "p_water_pump_5hp",
                sellerId = "seller_kisan_center",
                sellerName = "Kisan Agro Hardware & Drip Center",
                name = "Kirloskar 5 HP Openwell Submersible Pump",
                category = "Farm Equipment",
                subcategory = "Water Pumps",
                brand = "Kirloskar",
                sku = "EQP-KIRL-5HP-OW",
                description = "Heavy duty 3-phase openwell submersible pump designed for open wells, canals, and riverbed irrigation pumping.",
                imageUrl = "https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122?w=500",
                price = 24500.0,
                discountPrice = 22800.0,
                unit = "Set",
                stockQuantity = 6,
                minOrderQuantity = 1,
                rating = 4.9
            ),
            Product(
                id = "p_brass_valve_2inch",
                sellerId = "seller_kisan_center",
                sellerName = "Kisan Agro Hardware & Drip Center",
                name = "Leader 2-inch Brass Ball Valve with Handle",
                category = "Irrigation",
                subcategory = "Valves",
                brand = "Leader",
                sku = "IRR-VAL-LEAD-2IN",
                description = "Full port heavy forged brass ball valve, leak-proof quarter-turn shutoff for agricultural header units.",
                imageUrl = "https://images.unsplash.com/photo-1585771724684-38269d6639fd?w=500",
                price = 850.0,
                discountPrice = 720.0,
                unit = "Piece",
                stockQuantity = 30,
                minOrderQuantity = 1,
                rating = 4.6
            ),
            Product(
                id = "p_sprinkler_brass",
                sellerId = "seller_kisan_center",
                sellerName = "Kisan Agro Hardware & Drip Center",
                name = "Rotary Brass Impact Sprinkler 3/4-inch",
                category = "Irrigation",
                subcategory = "Sprinklers",
                brand = "Finolex",
                sku = "IRR-SPR-FIN-34",
                description = "Full circle brass impact sprinkler head. Spray radius 12 to 14 meters. Ideal for wheat, potato, and groundnut fields.",
                imageUrl = "https://images.unsplash.com/photo-1530836369250-ef72a3f5cda8?w=500",
                price = 450.0,
                discountPrice = 380.0,
                unit = "Piece",
                stockQuantity = 50,
                minOrderQuantity = 2,
                rating = 4.5
            ),
            Product(
                id = "p_starter_3phase",
                sellerId = "seller_shree_ram",
                sellerName = "Shree Ram Farm Spares & Pumps",
                name = "L&T MK1 3-Phase DOL Agricultural Motor Starter",
                category = "Electrical",
                subcategory = "Starters",
                brand = "L&T Electrical",
                sku = "ELE-LT-MK1-DOL",
                description = "Genuine Larson & Toubro direct-on-line starter with overload relay for 3-phase farm motors (5HP to 7.5HP).",
                imageUrl = "https://images.unsplash.com/photo-1558494949-ef010cbdcc31?w=500",
                price = 3100.0,
                discountPrice = 2850.0,
                unit = "Piece",
                stockQuantity = 18,
                minOrderQuantity = 1,
                rating = 4.9
            ),
            Product(
                id = "p_copper_cable_sub",
                sellerId = "seller_shree_ram",
                sellerName = "Shree Ram Farm Spares & Pumps",
                name = "Finolex 4.0 sq mm 3-Core Submersible Copper Cable (100m)",
                category = "Electrical",
                subcategory = "Cables",
                brand = "Finolex",
                sku = "ELE-FIN-SUB-4MM",
                description = "100% electrolytic bright copper conductor with water-resistant PVC insulation for underground borewell pumps.",
                imageUrl = "https://images.unsplash.com/photo-1544717305-2782549b5136?w=500",
                price = 8400.0,
                discountPrice = 7700.0,
                unit = "Bundle",
                stockQuantity = 8,
                minOrderQuantity = 1,
                rating = 4.8
            ),
            Product(
                id = "p_pruning_shears",
                sellerId = "seller_shree_ram",
                sellerName = "Shree Ram Farm Spares & Pumps",
                name = "Falcon Heavy Duty Bypass Pruning Secateur",
                category = "Agricultural Tools",
                subcategory = "Pruning Tools",
                brand = "Falcon",
                sku = "TOOL-FALC-PRUN-01",
                description = "Forged high carbon steel bypass garden shear for orchard pruning, cotton stalk trimming, and horticultural grafting.",
                imageUrl = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=500",
                price = 620.0,
                discountPrice = 520.0,
                unit = "Piece",
                stockQuantity = 22,
                minOrderQuantity = 1,
                rating = 4.6
            ),
            Product(
                id = "p_screen_filter_2inch",
                sellerId = "seller_patel",
                sellerName = "Patel Krushi Hardware",
                name = "Jain T-Type 2-inch Drip Screen Filter (120 Mesh)",
                category = "Irrigation",
                subcategory = "Filters",
                brand = "Jain Irrigation",
                sku = "IRR-JAIN-FLT-2IN",
                description = "Stainless steel 120 mesh screen filter for preventing emitter clogging from sand and algae in canal and well water.",
                imageUrl = "https://images.unsplash.com/photo-1581092160607-ee22621dd758?w=500",
                price = 1450.0,
                discountPrice = 1250.0,
                unit = "Piece",
                stockQuantity = 16,
                minOrderQuantity = 1,
                rating = 4.7
            )
        )
        _products.value = seededProducts

        // Seeded sample order
        val sampleTimeline = listOf(
            OrderTimelineEntry(OrderStatus.PLACED, System.currentTimeMillis() - 7200000, "Order placed by farmer"),
            OrderTimelineEntry(OrderStatus.ACCEPTED, System.currentTimeMillis() - 5400000, "Patel Krushi Hardware accepted the order"),
            OrderTimelineEntry(OrderStatus.PREPARING, System.currentTimeMillis() - 3600000, "Items packed and readied for dispatch"),
            OrderTimelineEntry(OrderStatus.OUT_FOR_DELIVERY, System.currentTimeMillis() - 1800000, "Out for delivery with shop staff")
        )

        val sampleOrder = Order(
            id = "ord_1001",
            orderNumber = "KS-2026-1001",
            farmerId = "farmer_1",
            farmerName = "Mukesh Bhai Patel",
            farmerPhone = "+91 98250 11223",
            deliveryAddress = "Farm Plot #42, Vasna Road, Anand",
            deliveryLat = 23.0225,
            deliveryLng = 72.5714,
            sellerId = "seller_patel",
            sellerName = "Patel Krushi Hardware",
            items = listOf(
                OrderItem(
                    productId = "p_drip_16mm",
                    productName = "Jain Inline 16mm Drip Pipe (400m Roll)",
                    brand = "Jain Irrigation",
                    unit = "Roll",
                    price = 2850.0,
                    quantity = 2,
                    total = 5700.0,
                    imageUrl = "https://images.unsplash.com/photo-1592417817098-8f3d69106093?w=500"
                )
            ),
            subtotal = 5700.0,
            discount = 0.0,
            deliveryFee = 0.0,
            grandTotal = 5700.0,
            status = OrderStatus.OUT_FOR_DELIVERY,
            paymentMethod = PaymentMethod.COD,
            paymentStatus = PaymentStatus.PENDING,
            deliveryOtp = "4829", // Pre-generated OTP for farmer to give to seller
            createdAt = System.currentTimeMillis() - 7200000,
            updatedAt = System.currentTimeMillis() - 1800000,
            timeline = sampleTimeline
        )
        _orders.value = listOf(sampleOrder)

        // Seed notification
        val initialNotification = AppNotification(
            id = "notif_1",
            userId = "farmer_1",
            title = "Order Out For Delivery",
            message = "Your order KS-2026-1001 is on the way. Share delivery OTP 4829 with the delivery person.",
            type = "ORDER_UPDATE",
            orderId = "ord_1001",
            timestamp = System.currentTimeMillis() - 1800000
        )
        _notifications.value = listOf(initialNotification)
    }

    // -------------------------------------------------------------------------
    // Haversine Distance Calculation & Nearby Logic
    // -------------------------------------------------------------------------
    fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0 // Radius of earth in KM
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c * 10).roundToInt() / 10.0 // 1 decimal place
    }

    fun getApprovedSellersWithinRadius(userLat: Double, userLng: Double): List<Pair<SellerProfile, Double>> {
        val radius = _appSettings.value.deliveryRadiusKm
        return _sellers.value
            .filter { it.status == SellerStatus.APPROVED && it.isAvailable }
            .map { seller ->
                val distance = calculateDistanceKm(userLat, userLng, seller.latitude, seller.longitude)
                seller to distance
            }
            .filter { (_, dist) -> dist <= radius }
            .sortedBy { it.second }
    }

    // -------------------------------------------------------------------------
    // Authentication & Profile
    // -------------------------------------------------------------------------
    // -------------------------------------------------------------------------
    // Authentication & Profile Implementations
    // -------------------------------------------------------------------------
    fun loginAsDefaultFarmer() {
        val farmer = User(
            id = "farmer_1",
            name = "Mukesh Bhai Patel",
            phone = "+91 98250 11223",
            role = UserRole.FARMER,
            email = "mukesh.farmer@agri.in",
            address = "Farm Plot #42, Vasna Road",
            city = "Anand",
            pincode = "388001",
            latitude = 23.0225,
            longitude = 72.5714,
            farmerProfileId = "fp_farmer_1"
        )
        val defaultFarmerProfile = FarmerProfile(
            id = "fp_farmer_1",
            userId = farmer.id,
            fullName = farmer.name,
            farmName = "Patel Krushi Farm",
            farmSizeAcres = 12.5,
            primaryCrops = listOf("Cotton", "Wheat", "Tobacco", "Vegetables"),
            irrigationType = "Drip & Openwell Submersible",
            savedAddresses = listOf(
                DeliveryAddress(
                    id = "addr_1",
                    label = "Farm Plot 42",
                    addressLine = "Plot #42, Vasna Road",
                    villageOrTown = "Vasna",
                    district = "Anand",
                    state = "Gujarat",
                    pincode = "388001",
                    latitude = 23.0225,
                    longitude = 72.5714,
                    isDefault = true
                ),
                DeliveryAddress(
                    id = "addr_2",
                    label = "Borewell Pump House",
                    addressLine = "Near Canal Sub-Division #3",
                    villageOrTown = "Chikhodra",
                    district = "Anand",
                    state = "Gujarat",
                    pincode = "388320",
                    latitude = 23.0410,
                    longitude = 72.5890,
                    isDefault = false
                )
            ),
            defaultAddress = DeliveryAddress(
                id = "addr_1",
                label = "Farm Plot 42",
                addressLine = "Plot #42, Vasna Road",
                villageOrTown = "Vasna",
                district = "Anand",
                state = "Gujarat",
                pincode = "388001",
                latitude = 23.0225,
                longitude = 72.5714,
                isDefault = true
            )
        )
        _currentUser.value = farmer
        _currentFarmerProfile.value = defaultFarmerProfile
        _currentSellerProfile.value = null
        _currentSession.value = AuthSession(
            user = farmer,
            role = UserRole.FARMER,
            farmerProfile = defaultFarmerProfile,
            token = farmer.token
        )
    }

    fun loginAsDefaultSeller() {
        val sellerUser = User(
            id = "user_seller_1",
            name = "Ramesh Patel",
            phone = "+91 98790 98765",
            role = UserRole.SELLER,
            email = "patel.hardware@agri.in",
            address = "Station Road, Near APMC Market",
            city = "Anand",
            pincode = "388001",
            latitude = 23.0250,
            longitude = 72.5750,
            sellerProfileId = "seller_patel"
        )
        val profile = _sellers.value.find { it.id == "seller_patel" }
        _currentUser.value = sellerUser
        _currentFarmerProfile.value = null
        _currentSellerProfile.value = profile
        _currentSession.value = AuthSession(
            user = sellerUser,
            role = UserRole.SELLER,
            sellerProfile = profile,
            token = sellerUser.token
        )
    }

    fun switchUserRole(role: UserRole) {
        if (role == UserRole.FARMER) {
            loginAsDefaultFarmer()
        } else {
            loginAsDefaultSeller()
        }
    }

    override suspend fun requestOtp(phone: String, requestedRole: UserRole): Result<String> {
        return Result.success("OTP sent to +91 $phone successfully.")
    }

    override suspend fun verifyOtp(phone: String, otp: String, role: UserRole): Result<AuthSession> {
        if (otp.length != 4 && otp.length != 6) {
            return Result.failure(Exception("Please enter a valid 4 or 6 digit OTP"))
        }

        if (role == UserRole.FARMER) {
            val user = User(
                id = "farmer_" + phone.takeLast(4),
                name = "Farmer ($phone)",
                phone = phone,
                role = UserRole.FARMER,
                address = "Agricultural Farm Address",
                city = "Anand",
                pincode = "388001"
            )
            val profile = FarmerProfile(
                id = "fp_" + user.id,
                userId = user.id,
                fullName = user.name
            )
            _currentUser.value = user
            _currentFarmerProfile.value = profile
            _currentSellerProfile.value = null
            val session = AuthSession(user = user, role = UserRole.FARMER, farmerProfile = profile, token = user.token)
            _currentSession.value = session
            return Result.success(session)
        } else {
            // Find existing seller or create placeholder profile
            val existingSeller = _sellers.value.find { it.userId.endsWith(phone.takeLast(4)) }
            val sellerUser = User(
                id = "seller_user_" + phone.takeLast(4),
                name = existingSeller?.ownerName ?: "Shop Owner ($phone)",
                phone = phone,
                role = UserRole.SELLER
            )
            _currentUser.value = sellerUser
            _currentFarmerProfile.value = null
            _currentSellerProfile.value = existingSeller
            val session = AuthSession(user = sellerUser, role = UserRole.SELLER, sellerProfile = existingSeller, token = sellerUser.token)
            _currentSession.value = session
            return Result.success(session)
        }
    }

    override suspend fun switchActiveRole(targetRole: UserRole): Result<AuthSession> {
        switchUserRole(targetRole)
        val session = _currentSession.value ?: return Result.failure(Exception("No active session"))
        return Result.success(session)
    }

    override suspend fun getCurrentUser(): User? = _currentUser.value

    override suspend fun isAuthenticated(): Boolean = _currentUser.value != null

    override suspend fun logout(): Result<Unit> {
        _currentUser.value = null
        _currentFarmerProfile.value = null
        _currentSellerProfile.value = null
        _currentSession.value = null
        return Result.success(Unit)
    }

    override suspend fun refreshToken(): Result<String> {
        val newToken = "token_${System.currentTimeMillis()}"
        _currentUser.value = _currentUser.value?.copy(token = newToken)
        _currentSession.value = _currentSession.value?.copy(token = newToken)
        return Result.success(newToken)
    }

    // -------------------------------------------------------------------------
    // IFarmerProfileRepository Implementations
    // -------------------------------------------------------------------------
    override fun getFarmerProfileFlow(userId: String): Flow<FarmerProfile?> = _currentFarmerProfile.asStateFlow()

    override suspend fun getFarmerProfile(userId: String): Result<FarmerProfile> {
        val profile = _currentFarmerProfile.value ?: return Result.failure(Exception("Farmer profile not found"))
        return Result.success(profile)
    }

    override suspend fun updateFarmerProfile(profile: FarmerProfile): Result<FarmerProfile> {
        _currentFarmerProfile.value = profile.copy(updatedAt = System.currentTimeMillis())
        _currentUser.value = _currentUser.value?.copy(name = profile.fullName)
        return Result.success(profile)
    }

    override suspend fun updatePreferredLanguage(userId: String, languageCode: String): Result<Unit> {
        _currentFarmerProfile.value = _currentFarmerProfile.value?.copy(preferredLanguage = languageCode)
        return Result.success(Unit)
    }

    override suspend fun getSavedAddresses(farmerId: String): Flow<List<DeliveryAddress>> =
        _currentFarmerProfile.map { it?.savedAddresses ?: emptyList() }

    override suspend fun addDeliveryAddress(farmerId: String, address: DeliveryAddress): Result<DeliveryAddress> {
        val current = _currentFarmerProfile.value ?: return Result.failure(Exception("Profile not found"))
        val updatedList = current.savedAddresses + address
        val defaultAddr = if (address.isDefault || current.defaultAddress == null) address else current.defaultAddress
        _currentFarmerProfile.value = current.copy(savedAddresses = updatedList, defaultAddress = defaultAddr)
        return Result.success(address)
    }

    override suspend fun setDefaultAddress(farmerId: String, addressId: String): Result<Unit> {
        val current = _currentFarmerProfile.value ?: return Result.failure(Exception("Profile not found"))
        val target = current.savedAddresses.find { it.id == addressId } ?: return Result.failure(Exception("Address not found"))
        val updatedList = current.savedAddresses.map { it.copy(isDefault = it.id == addressId) }
        _currentFarmerProfile.value = current.copy(savedAddresses = updatedList, defaultAddress = target.copy(isDefault = true))
        return Result.success(Unit)
    }

    override suspend fun deleteDeliveryAddress(farmerId: String, addressId: String): Result<Unit> {
        val current = _currentFarmerProfile.value ?: return Result.failure(Exception("Profile not found"))
        val updatedList = current.savedAddresses.filter { it.id != addressId }
        _currentFarmerProfile.value = current.copy(savedAddresses = updatedList)
        return Result.success(Unit)
    }

    // -------------------------------------------------------------------------
    // ISellerProfileRepository Implementations
    // -------------------------------------------------------------------------
    override suspend fun getSellerProfile(sellerId: String): Result<SellerProfile> {
        val seller = _sellers.value.find { it.id == sellerId } ?: return Result.failure(Exception("Seller not found"))
        return Result.success(seller)
    }

    override suspend fun getSellerProfileByUserId(userId: String): Result<SellerProfile?> {
        val seller = _sellers.value.find { it.userId == userId }
        return Result.success(seller)
    }

    override suspend fun updateSellerProfile(profile: SellerProfile): Result<SellerProfile> {
        val updated = profile.copy(updatedAt = System.currentTimeMillis())
        _sellers.value = _sellers.value.map { if (it.id == updated.id) updated else it }
        if (_currentSellerProfile.value?.id == updated.id) {
            _currentSellerProfile.value = updated
        }
        return Result.success(updated)
    }

    override suspend fun updateShopLocation(sellerId: String, latitude: Double, longitude: Double): Result<Unit> {
        _sellers.value = _sellers.value.map {
            if (it.id == sellerId) it.copy(latitude = latitude, longitude = longitude, updatedAt = System.currentTimeMillis()) else it
        }
        if (_currentSellerProfile.value?.id == sellerId) {
            _currentSellerProfile.value = _currentSellerProfile.value?.copy(latitude = latitude, longitude = longitude)
        }
        return Result.success(Unit)
    }

    override suspend fun updateShopAvailability(sellerId: String, isAvailable: Boolean): Result<Unit> {
        _sellers.value = _sellers.value.map {
            if (it.id == sellerId) it.copy(isAvailable = isAvailable, updatedAt = System.currentTimeMillis()) else it
        }
        if (_currentSellerProfile.value?.id == sellerId) {
            _currentSellerProfile.value = _currentSellerProfile.value?.copy(isAvailable = isAvailable)
        }
        return Result.success(Unit)
    }

    override fun observeSellerStatus(sellerId: String): Flow<SellerStatus> =
        _sellers.map { list ->
            list.find { it.id == sellerId }?.status ?: SellerStatus.PENDING
        }

    override suspend fun registerSeller(request: SellerRegistrationRequest): Result<SellerProfile> {
        val newSeller = SellerProfile(
            id = "seller_" + System.currentTimeMillis().toString().takeLast(6),
            userId = request.userId,
            ownerName = request.ownerName,
            shopName = request.shopName,
            shopAddress = request.shopAddress,
            city = request.city,
            state = request.state,
            pincode = request.pincode,
            latitude = request.latitude,
            longitude = request.longitude,
            shopImageUrl = request.shopImageUrl.ifEmpty { "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=400" },
            gstNumber = request.gstNumber,
            bankAccount = request.bankAccount,
            ifscCode = request.ifscCode,
            status = SellerStatus.PENDING, // Mandatory: Pending admin approval!
            rating = 5.0,
            totalRatings = 0,
            isAvailable = true,
            createdAt = System.currentTimeMillis()
        )

        _sellers.value = _sellers.value + newSeller
        _currentSellerProfile.value = newSeller

        // Update user state
        val updatedUser = _currentUser.value?.copy(
            name = request.ownerName,
            address = request.shopAddress,
            city = request.city,
            pincode = request.pincode,
            latitude = request.latitude,
            longitude = request.longitude
        )
        _currentUser.value = updatedUser

        // Create notification
        addNotification(
            userId = request.userId,
            title = "Registration Submitted",
            message = "Your shop registration for '${request.shopName}' is pending Admin review.",
            type = "SYSTEM"
        )

        return Result.success(newSeller)
    }

    // -------------------------------------------------------------------------
    // Cart & Room Local Storage
    // -------------------------------------------------------------------------
    fun getCartItemsFlow(): Flow<List<CartItem>> {
        return cartDao.getAllCartItems().map { localItems ->
            localItems.map { local ->
                val product = Product(
                    id = local.productId,
                    sellerId = local.sellerId,
                    sellerName = local.sellerName,
                    name = local.productName,
                    category = local.category,
                    subcategory = "",
                    brand = local.brand,
                    sku = "",
                    description = "",
                    imageUrl = local.imageUrl,
                    price = local.price,
                    discountPrice = local.discountPrice,
                    unit = local.unit,
                    stockQuantity = local.stockQuantity
                )
                CartItem(product = product, quantity = local.quantity)
            }
        }
    }

    suspend fun addToCart(product: Product, quantityToAdd: Int = 1): Result<Unit> {
        val currentItems = cartDao.getCartItemsList()
        // Single-seller Cart rule check
        if (currentItems.isNotEmpty()) {
            val existingSellerId = currentItems.first().sellerId
            if (existingSellerId != product.sellerId) {
                return Result.failure(
                    Exception(
                        "Your cart already has items from '${currentItems.first().sellerName}'. " +
                        "For agricultural hardware delivery, orders must be from a single shop. " +
                        "Please clear your cart or complete your order first."
                    )
                )
            }
        }

        val existing = currentItems.find { it.productId == product.id }
        val newQuantity = (existing?.quantity ?: 0) + quantityToAdd

        if (newQuantity > product.stockQuantity) {
            return Result.failure(Exception("Cannot add more than available stock (${product.stockQuantity})"))
        }

        val localItem = LocalCartItem(
            productId = product.id,
            sellerId = product.sellerId,
            sellerName = product.sellerName,
            productName = product.name,
            brand = product.brand,
            category = product.category,
            price = product.price,
            discountPrice = product.discountPrice,
            unit = product.unit,
            imageUrl = product.imageUrl,
            quantity = newQuantity,
            stockQuantity = product.stockQuantity
        )
        cartDao.insertOrUpdate(localItem)
        return Result.success(Unit)
    }

    suspend fun updateCartQuantity(productId: String, newQuantity: Int): Result<Unit> {
        if (newQuantity <= 0) {
            cartDao.deleteByProductId(productId)
            return Result.success(Unit)
        }
        val items = cartDao.getCartItemsList()
        val item = items.find { it.productId == productId } ?: return Result.failure(Exception("Item not found"))

        if (newQuantity > item.stockQuantity) {
            return Result.failure(Exception("Stock limit reached (${item.stockQuantity})"))
        }

        cartDao.insertOrUpdate(item.copy(quantity = newQuantity))
        return Result.success(Unit)
    }

    suspend fun removeFromCart(productId: String) {
        cartDao.deleteByProductId(productId)
    }

    suspend fun clearCart() {
        cartDao.clearCart()
    }

    // -------------------------------------------------------------------------
    // Order Placement, Matching, and Atomic First-To-Accept
    // -------------------------------------------------------------------------
    suspend fun placeOrder(
        farmer: User,
        items: List<CartItem>,
        paymentMethod: PaymentMethod
    ): Result<Order> = orderMutex.withLock {
        if (items.isEmpty()) return Result.failure(Exception("Cart is empty"))

        // STEP 1: Validate stock on backend
        for (item in items) {
            val serverProduct = _products.value.find { it.id == item.product.id }
                ?: return Result.failure(Exception("Product '${item.product.name}' is no longer available"))

            if (serverProduct.stockQuantity < item.quantity) {
                return Result.failure(
                    Exception("Insufficient stock for '${serverProduct.name}'. Available: ${serverProduct.stockQuantity}")
                )
            }
        }

        // Calculate totals
        val subtotal = items.sumOf { it.subtotal }
        val deliveryFee = if (subtotal >= _appSettings.value.minOrderAmountForFreeDelivery) 0.0 else _appSettings.value.baseDeliveryCharge
        val grandTotal = subtotal + deliveryFee

        val orderNumber = "KS-2026-" + (1000 + _orders.value.size + 1)
        val orderId = "ord_" + System.currentTimeMillis()

        // Generate a secure 4-digit Delivery OTP
        val secureDeliveryOtp = (1000..9999).random().toString()

        val orderItems = items.map { item ->
            OrderItem(
                productId = item.product.id,
                productName = item.product.name,
                brand = item.product.brand,
                unit = item.product.unit,
                price = item.effectivePrice,
                quantity = item.quantity,
                total = item.subtotal,
                imageUrl = item.product.imageUrl
            )
        }

        val primarySellerId = items.first().product.sellerId
        val primarySeller = _sellers.value.find { it.id == primarySellerId }

        val newOrder = Order(
            id = orderId,
            orderNumber = orderNumber,
            farmerId = farmer.id,
            farmerName = farmer.name,
            farmerPhone = farmer.phone,
            deliveryAddress = farmer.address.ifEmpty { "Plot 42, Farm Area, Anand" },
            deliveryLat = farmer.latitude,
            deliveryLng = farmer.longitude,
            sellerId = primarySellerId,
            sellerName = primarySeller?.shopName ?: "Agricultural Hardware",
            items = orderItems,
            subtotal = subtotal,
            discount = 0.0,
            deliveryFee = deliveryFee,
            grandTotal = grandTotal,
            status = OrderStatus.SEARCHING_SELLER,
            paymentMethod = paymentMethod,
            paymentStatus = if (paymentMethod == PaymentMethod.COD) PaymentStatus.PENDING else PaymentStatus.PAID,
            deliveryOtp = secureDeliveryOtp,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            estimatedDeliveryTime = "Within 2-4 Hours",
            timeline = listOf(
                OrderTimelineEntry(OrderStatus.PLACED, System.currentTimeMillis(), "Order placed by farmer"),
                OrderTimelineEntry(OrderStatus.SEARCHING_SELLER, System.currentTimeMillis(), "Notifying nearby approved hardware shops")
            )
        )

        // Deduct inventory immediately to guarantee stock reservation
        _products.value = _products.value.map { prod ->
            val orderItem = items.find { it.product.id == prod.id }
            if (orderItem != null) {
                prod.copy(
                    stockQuantity = max(0, prod.stockQuantity - orderItem.quantity),
                    updatedAt = System.currentTimeMillis()
                )
            } else prod
        }

        // STEP 2 & 3: Find approved sellers within radius
        val eligibleSellers = getApprovedSellersWithinRadius(farmer.latitude, farmer.longitude)
        val targetSellers = if (eligibleSellers.any { it.first.id == primarySellerId }) {
            eligibleSellers.filter { it.first.id == primarySellerId }
        } else {
            eligibleSellers
        }

        // STEP 4: Create OrderRequest for eligible sellers
        val newRequests = targetSellers.map { (seller, distance) ->
            OrderRequest(
                id = "req_${System.currentTimeMillis()}_${seller.id}",
                orderId = orderId,
                sellerId = seller.id,
                orderNumber = orderNumber,
                orderAmount = grandTotal,
                itemCount = items.sumOf { it.quantity },
                summaryText = items.joinToString(", ") { "${it.product.name} (x${it.quantity})" },
                farmerArea = "${farmer.city} (${farmer.address.take(25)}...)",
                distanceKm = distance,
                status = "PENDING"
            )
        }

        _orderRequests.value = _orderRequests.value + newRequests
        _orders.value = listOf(newOrder) + _orders.value

        // Clear farmer's cart
        clearCart()

        // Send notifications
        addNotification(
            userId = farmer.id,
            title = "Order Placed ($orderNumber)",
            message = "We are matching your order with nearby hardware shops within ${_appSettings.value.deliveryRadiusKm.toInt()} km.",
            type = "ORDER_UPDATE",
            orderId = orderId
        )

        targetSellers.forEach { (seller, dist) ->
            addNotification(
                userId = seller.userId,
                title = "New Order Request ($orderNumber)",
                message = "New nearby order of ₹${grandTotal.toInt()} from $dist km away. Tap to accept.",
                type = "ORDER_REQUEST",
                orderId = orderId
            )
        }

        return Result.success(newOrder)
    }

    /**
     * ATOMIC FIRST-TO-ACCEPT LOCKING:
     * Only ONE seller can accept an order. First seller to accept gets locked in.
     * Other sellers are prevented and notified that the order has expired.
     */
    suspend fun acceptOrder(orderId: String, sellerId: String): Result<Order> = orderMutex.withLock {
        val existingOrder = _orders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found"))

        // Check if seller is approved
        val seller = _sellers.value.find { it.id == sellerId }
            ?: return Result.failure(Exception("Seller not found"))

        if (seller.status != SellerStatus.APPROVED) {
            return Result.failure(Exception("Your shop is not approved to accept orders yet."))
        }

        // If order already accepted or assigned to someone else
        if (existingOrder.status != OrderStatus.SEARCHING_SELLER && existingOrder.status != OrderStatus.PLACED) {
            if (existingOrder.sellerId == sellerId && existingOrder.status == OrderStatus.ACCEPTED) {
                return Result.success(existingOrder) // idempotent
            }
            return Result.failure(Exception("Order has already been accepted by another seller."))
        }

        // Lock in this seller!
        val updatedTimeline = existingOrder.timeline + OrderTimelineEntry(
            OrderStatus.ACCEPTED,
            System.currentTimeMillis(),
            "Accepted by ${seller.shopName}"
        )

        val updatedOrder = existingOrder.copy(
            sellerId = sellerId,
            sellerName = seller.shopName,
            status = OrderStatus.ACCEPTED,
            updatedAt = System.currentTimeMillis(),
            timeline = updatedTimeline
        )

        _orders.value = _orders.value.map { if (it.id == orderId) updatedOrder else it }

        // Expire all other requests for this order
        _orderRequests.value = _orderRequests.value.map { req ->
            if (req.orderId == orderId) {
                if (req.sellerId == sellerId) req.copy(status = "ACCEPTED")
                else req.copy(status = "EXPIRED")
            } else req
        }

        // Notify farmer
        addNotification(
            userId = existingOrder.farmerId,
            title = "Order Accepted!",
            message = "${seller.shopName} has accepted your order ($orderId). Packing has begun.",
            type = "ORDER_UPDATE",
            orderId = orderId
        )

        return Result.success(updatedOrder)
    }

    suspend fun rejectOrderRequest(orderId: String, sellerId: String): Result<Unit> = orderMutex.withLock {
        _orderRequests.value = _orderRequests.value.map { req ->
            if (req.orderId == orderId && req.sellerId == sellerId) {
                req.copy(status = "REJECTED")
            } else req
        }
        return Result.success(Unit)
    }

    // -------------------------------------------------------------------------
    // Order Progression & OTP Verification
    // -------------------------------------------------------------------------
    suspend fun updateOrderStatus(orderId: String, sellerId: String, nextStatus: OrderStatus): Result<Order> = orderMutex.withLock {
        val existingOrder = _orders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found"))

        if (existingOrder.sellerId != sellerId) {
            return Result.failure(Exception("Unauthorized: This order is assigned to another shop."))
        }

        if (nextStatus == OrderStatus.DELIVERED) {
            return Result.failure(Exception("Orders cannot be directly marked DELIVERED without customer OTP verification."))
        }

        val note = when (nextStatus) {
            OrderStatus.PREPARING -> "Shop is preparing and packing your agricultural equipment"
            OrderStatus.READY_FOR_DELIVERY -> "Order is packed and ready for delivery"
            OrderStatus.OUT_FOR_DELIVERY -> "Out for delivery. Farmer must provide OTP upon handover."
            OrderStatus.CANCELLED -> "Order cancelled"
            else -> "Status updated to $nextStatus"
        }

        val updatedTimeline = existingOrder.timeline + OrderTimelineEntry(
            nextStatus,
            System.currentTimeMillis(),
            note
        )

        val updatedOrder = existingOrder.copy(
            status = nextStatus,
            updatedAt = System.currentTimeMillis(),
            timeline = updatedTimeline
        )

        _orders.value = _orders.value.map { if (it.id == orderId) updatedOrder else it }

        addNotification(
            userId = existingOrder.farmerId,
            title = "Order ${nextStatus.name.replace('_', ' ')}",
            message = note,
            type = "ORDER_UPDATE",
            orderId = orderId
        )

        return Result.success(updatedOrder)
    }

    /**
     * Server-side OTP Verification for final delivery
     */
    suspend fun verifyDeliveryOtp(orderId: String, sellerId: String, enteredOtp: String): Result<Order> = orderMutex.withLock {
        val order = _orders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found"))

        if (order.sellerId != sellerId) {
            return Result.failure(Exception("Unauthorized seller"))
        }

        if (order.status != OrderStatus.OUT_FOR_DELIVERY) {
            return Result.failure(Exception("Order is not currently Out for Delivery"))
        }

        if (order.deliveryOtp.trim() != enteredOtp.trim()) {
            return Result.failure(Exception("Invalid Delivery OTP. Please ask the farmer for the 4-digit code on their screen."))
        }

        val updatedTimeline = order.timeline + OrderTimelineEntry(
            OrderStatus.DELIVERED,
            System.currentTimeMillis(),
            "Successfully delivered. Customer OTP verified."
        )

        val completedOrder = order.copy(
            status = OrderStatus.DELIVERED,
            paymentStatus = PaymentStatus.PAID,
            updatedAt = System.currentTimeMillis(),
            timeline = updatedTimeline
        )

        _orders.value = _orders.value.map { if (it.id == orderId) completedOrder else it }

        addNotification(
            userId = order.farmerId,
            title = "Order Delivered!",
            message = "Your order #${order.orderNumber} has been delivered. Please rate your experience.",
            type = "ORDER_UPDATE",
            orderId = orderId
        )

        return Result.success(completedOrder)
    }

    // -------------------------------------------------------------------------
    // Product Management (Seller)
    // -------------------------------------------------------------------------
    suspend fun addProduct(request: AddProductRequest): Result<Product> {
        val seller = _sellers.value.find { it.id == request.sellerId }
            ?: return Result.failure(Exception("Seller shop not found"))

        if (seller.status != SellerStatus.APPROVED) {
            return Result.failure(Exception("Your shop is currently ${seller.status}. Only APPROVED shops can publish products."))
        }

        val newProduct = Product(
            id = "prod_" + System.currentTimeMillis(),
            sellerId = request.sellerId,
            sellerName = seller.shopName,
            name = request.name,
            category = request.category,
            subcategory = request.subcategory,
            brand = request.brand,
            sku = request.sku.ifEmpty { "SKU-" + (1000..9999).random() },
            description = request.description,
            imageUrl = request.imageUrl.ifEmpty { "https://images.unsplash.com/photo-1592417817098-8f3d69106093?w=500" },
            price = request.price,
            discountPrice = request.discountPrice,
            unit = request.unit,
            stockQuantity = request.stockQuantity,
            minOrderQuantity = request.minOrderQuantity,
            isAvailable = true,
            rating = 5.0,
            totalRatings = 0,
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis()
        )

        _products.value = listOf(newProduct) + _products.value
        return Result.success(newProduct)
    }

    suspend fun updateProductStock(productId: String, sellerId: String, newStock: Int): Result<Product> {
        val product = _products.value.find { it.id == productId }
            ?: return Result.failure(Exception("Product not found"))

        if (product.sellerId != sellerId) {
            return Result.failure(Exception("Unauthorized: Product belongs to another shop"))
        }

        val updated = product.copy(
            stockQuantity = max(0, newStock),
            isAvailable = newStock > 0,
            updatedAt = System.currentTimeMillis()
        )

        _products.value = _products.value.map { if (it.id == productId) updated else it }
        return Result.success(updated)
    }

    suspend fun toggleProductAvailability(productId: String, sellerId: String): Result<Product> {
        val product = _products.value.find { it.id == productId }
            ?: return Result.failure(Exception("Product not found"))

        if (product.sellerId != sellerId) {
            return Result.failure(Exception("Unauthorized"))
        }

        val updated = product.copy(isAvailable = !product.isAvailable, updatedAt = System.currentTimeMillis())
        _products.value = _products.value.map { if (it.id == productId) updated else it }
        return Result.success(updated)
    }

    // -------------------------------------------------------------------------
    // Reviews
    // -------------------------------------------------------------------------
    suspend fun submitReview(
        orderId: String,
        sellerId: String,
        farmerId: String,
        farmerName: String,
        rating: Int,
        comment: String
    ): Result<Review> {
        val order = _orders.value.find { it.id == orderId }
            ?: return Result.failure(Exception("Order not found"))

        if (order.status != OrderStatus.DELIVERED) {
            return Result.failure(Exception("Reviews can only be submitted for completed and delivered orders."))
        }

        val review = Review(
            id = "rev_" + System.currentTimeMillis(),
            orderId = orderId,
            sellerId = sellerId,
            farmerId = farmerId,
            farmerName = farmerName,
            rating = rating.coerceIn(1, 5),
            comment = comment,
            createdAt = System.currentTimeMillis()
        )

        _reviews.value = listOf(review) + _reviews.value

        // Update seller average rating
        val sellerReviews = _reviews.value.filter { it.sellerId == sellerId }
        val avg = sellerReviews.map { it.rating }.average()
        _sellers.value = _sellers.value.map { s ->
            if (s.id == sellerId) {
                s.copy(rating = (avg * 10).roundToInt() / 10.0, totalRatings = sellerReviews.size)
            } else s
        }

        return Result.success(review)
    }

    // -------------------------------------------------------------------------
    // Notifications & Admin Settings Integration
    // -------------------------------------------------------------------------
    fun addNotification(userId: String, title: String, message: String, type: String, orderId: String? = null) {
        val notif = AppNotification(
            id = "notif_" + System.currentTimeMillis() + "_" + (100..999).random(),
            userId = userId,
            title = title,
            message = message,
            type = type,
            orderId = orderId,
            timestamp = System.currentTimeMillis()
        )
        _notifications.value = listOf(notif) + _notifications.value
    }

    fun markNotificationsRead(userId: String) {
        _notifications.value = _notifications.value.map {
            if (it.userId == userId) it.copy(isRead = true) else it
        }
    }

    // Admin integration: toggle seller approval status directly
    fun adminSetSellerStatus(sellerId: String, status: SellerStatus) {
        _sellers.value = _sellers.value.map {
            if (it.id == sellerId) it.copy(status = status) else it
        }
        if (_currentSellerProfile.value?.id == sellerId) {
            _currentSellerProfile.value = _currentSellerProfile.value?.copy(status = status)
        }
    }

    fun adminSetDeliveryRadius(newRadiusKm: Double) {
        _appSettings.value = _appSettings.value.copy(deliveryRadiusKm = newRadiusKm)
    }
}
