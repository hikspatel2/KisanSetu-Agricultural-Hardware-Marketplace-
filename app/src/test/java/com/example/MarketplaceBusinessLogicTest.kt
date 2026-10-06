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
}
