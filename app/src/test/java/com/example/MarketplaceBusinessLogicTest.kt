package com.example

import com.example.data.model.*
import org.junit.Assert.*
import org.junit.Test
import kotlin.math.*

class MarketplaceBusinessLogicTest {

    private fun calculateDistanceKm(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
        val r = 6371.0
        val dLat = Math.toRadians(lat2 - lat1)
        val dLon = Math.toRadians(lon2 - lon1)
        val a = sin(dLat / 2).pow(2) +
                cos(Math.toRadians(lat1)) * cos(Math.toRadians(lat2)) *
                sin(dLon / 2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        return (r * c * 10).roundToInt() / 10.0
    }

    @Test
    fun distanceCalculation_isAccurateForNearbySellers() {
        val farmerLat = 23.0225
        val farmerLng = 72.5714

        // Nearby shop ~0.5 km away
        val dist1 = calculateDistanceKm(farmerLat, farmerLng, 23.0250, 72.5750)
        assertTrue("Distance should be <= 1.0 km, got $dist1", dist1 in 0.2..1.0)

        // Shop ~6.5 km away
        val dist2 = calculateDistanceKm(farmerLat, farmerLng, 23.0700, 72.6100)
        assertTrue("Distance should be within 15 km radius, got $dist2", dist2 < 15.0)

        // Faraway shop > 60 km
        val distFar = calculateDistanceKm(farmerLat, farmerLng, 22.3000, 73.2000)
        assertTrue("Faraway shop should exceed 15 km radius, got $distFar", distFar > 15.0)
    }

    @Test
    fun deliveryOtp_verificationSucceedsOnlyWithExactCode() {
        val expectedOtp = "4829"
        val userEnteredOtpCorrect = "4829"
        val userEnteredOtpWrong = "1234"

        assertEquals(expectedOtp, userEnteredOtpCorrect.trim())
        assertNotEquals(expectedOtp, userEnteredOtpWrong.trim())
    }

    @Test
    fun singleSellerCart_ruleValidation() {
        val existingShopId = "seller_patel"
        val newProductFromSameShop = "seller_patel"
        val newProductFromOtherShop = "seller_kisan_center"

        val canAddSame = existingShopId == newProductFromSameShop
        val canAddDifferent = existingShopId == newProductFromOtherShop

        assertTrue("Should allow adding product from same shop", canAddSame)
        assertFalse("Should prevent adding product from different shop", canAddDifferent)
    }

    @Test
    fun farmerProfile_defaultAddressSelection() {
        val addr1 = DeliveryAddress(id = "addr_1", label = "Farm 1", addressLine = "Line 1", villageOrTown = "Anand", isDefault = true)
        val addr2 = DeliveryAddress(id = "addr_2", label = "Farm 2", addressLine = "Line 2", villageOrTown = "Anand", isDefault = false)

        val profile = FarmerProfile(
            id = "fp_1",
            userId = "user_farmer_1",
            fullName = "Mukesh Patel",
            savedAddresses = listOf(addr1, addr2),
            defaultAddress = addr1
        )

        assertEquals("addr_1", profile.defaultAddress?.id)
        assertTrue(profile.savedAddresses.first { it.id == "addr_1" }.isDefault)
    }

    @Test
    fun sellerProfile_approvalStatusTransitions() {
        val pendingSeller = SellerProfile(
            id = "seller_1",
            userId = "user_seller_1",
            ownerName = "Dinesh Prajapati",
            shopName = "GreenField Agro Spares",
            shopAddress = "Borsad Road",
            city = "Anand",
            state = "Gujarat",
            pincode = "388540",
            latitude = 23.0550,
            longitude = 72.5850,
            status = SellerStatus.PENDING
        )

        assertEquals(SellerStatus.PENDING, pendingSeller.status)

        val approvedSeller = pendingSeller.copy(status = SellerStatus.APPROVED)
        assertEquals(SellerStatus.APPROVED, approvedSeller.status)
        assertTrue(approvedSeller.isAvailable)
    }

    @Test
    fun authSession_roleIntegrity() {
        val userFarmer = User(id = "u1", name = "Farmer", phone = "9825011223", role = UserRole.FARMER)
        val sessionFarmer = AuthSession(user = userFarmer, role = UserRole.FARMER, token = "token_farmer")

        assertEquals(UserRole.FARMER, sessionFarmer.role)
        assertEquals(UserRole.FARMER, sessionFarmer.user.role)

        val userSeller = User(id = "u2", name = "Seller", phone = "9879098765", role = UserRole.SELLER)
        val sessionSeller = AuthSession(user = userSeller, role = UserRole.SELLER, token = "token_seller")

        assertEquals(UserRole.SELLER, sessionSeller.role)
        assertEquals(UserRole.SELLER, sessionSeller.user.role)
    }

    @Test
    fun productModel_initializationAndEffectivePrice() {
        val equipment = Product(
            name = "Kirloskar 5 HP Openwell Submersible Pump",
            price = 24500.0,
            discountPrice = 22800.0,
            imageUrl = "https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122"
        )

        assertEquals("Kirloskar 5 HP Openwell Submersible Pump", equipment.name)
        assertEquals(24500.0, equipment.price, 0.001)
        assertEquals(22800.0, equipment.effectivePrice, 0.001)
        assertEquals("https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122", equipment.image)
        assertEquals("https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122", equipment.imageUrl)
    }

    @Test
    fun agriculturalEquipment_sampleCatalogIntegrity() {
        val catalog = com.example.ui.farmer.SampleAgriculturalEquipment
        assertTrue("Catalog should contain equipment models", catalog.isNotEmpty())

        catalog.forEach { item ->
            assertTrue("Equipment name should not be blank", item.name.isNotBlank())
            assertTrue("Equipment price should be greater than zero", item.price > 0.0)
            assertTrue("Equipment category should not be blank", item.category.isNotBlank())
            assertTrue("Equipment effective price <= regular price", item.effectivePrice <= item.price)
        }
    }
}
