package com.example.data.repository

import com.example.data.api.SellerRegistrationRequest
import com.example.data.model.*
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for role-based authentication and session management.
 * Manages dual-role authentication (FARMER vs SELLER), OTP generation/verification,
 * active session state, and secure tokens.
 */
interface IAuthRepository {

    /**
     * Observable stream of the currently authenticated user.
     * Emits null when unauthenticated.
     */
    val currentUserFlow: Flow<User?>

    /**
     * Observable stream of the full authenticated session, including role-specific profile.
     */
    val currentSessionFlow: Flow<AuthSession?>

    /**
     * Sends a 4- or 6-digit OTP to the provided mobile number.
     * @param phone 10-digit mobile number
     * @param requestedRole Intended role (FARMER or SELLER)
     * @return Result containing request transaction ID or status message
     */
    suspend fun requestOtp(phone: String, requestedRole: UserRole): Result<String>

    /**
     * Verifies the mobile OTP and authenticates the user into the specified role.
     * Enforces role-based session establishment.
     * @param phone Mobile number
     * @param otp OTP entered by user
     * @param role UserRole.FARMER or UserRole.SELLER
     * @return Result containing the authenticated AuthSession
     */
    suspend fun verifyOtp(phone: String, otp: String, role: UserRole): Result<AuthSession>

    /**
     * Dynamically switches the active role between FARMER and SELLER
     * without requiring re-entering credentials if authorized.
     */
    suspend fun switchActiveRole(targetRole: UserRole): Result<AuthSession>

    /**
     * Retrieves the current user synchronously or from cache.
     */
    suspend fun getCurrentUser(): User?

    /**
     * Checks if a valid session exists.
     */
    suspend fun isAuthenticated(): Boolean

    /**
     * Terminates the current session, clearing local tokens and cached user data.
     */
    suspend fun logout(): Result<Unit>

    /**
     * Refreshes the authentication JWT/token from backend.
     */
    suspend fun refreshToken(): Result<String>
}

/**
 * Repository interface for Farmer profile management.
 * Manages agricultural details (farm size, crops, irrigation setup),
 * preferred language, and multiple farm delivery addresses with GPS coordinates.
 */
interface IFarmerProfileRepository {

    /**
     * Stream of the current farmer's profile.
     */
    fun getFarmerProfileFlow(userId: String): Flow<FarmerProfile?>

    /**
     * Fetches the farmer profile by user ID.
     */
    suspend fun getFarmerProfile(userId: String): Result<FarmerProfile>

    /**
     * Updates farmer personal & agricultural profile details
     * (e.g. farm name, acreage, primary crops, irrigation methods).
     */
    suspend fun updateFarmerProfile(profile: FarmerProfile): Result<FarmerProfile>

    /**
     * Updates preferred app language (English, Gujarati, Hindi).
     */
    suspend fun updatePreferredLanguage(userId: String, languageCode: String): Result<Unit>

    /**
     * Retrieves all saved delivery addresses for a farmer.
     */
    suspend fun getSavedAddresses(farmerId: String): Flow<List<DeliveryAddress>>

    /**
     * Adds a new farm delivery location with GPS coordinates.
     */
    suspend fun addDeliveryAddress(farmerId: String, address: DeliveryAddress): Result<DeliveryAddress>

    /**
     * Sets a specific delivery address as the primary/default for hardware orders.
     */
    suspend fun setDefaultAddress(farmerId: String, addressId: String): Result<Unit>

    /**
     * Deletes a saved delivery location.
     */
    suspend fun deleteDeliveryAddress(farmerId: String, addressId: String): Result<Unit>
}

/**
 * Repository interface for Hardware Shop Owner / Seller profile management.
 * Manages shop registration, GPS coordinates for 15 KM delivery matching,
 * shop verification details (GST, Bank info), and Admin approval status monitoring.
 */
interface ISellerProfileRepository {

    /**
     * Observable stream of the active seller profile.
     */
    val currentSellerProfileFlow: Flow<SellerProfile?>

    /**
     * Registers a new hardware shop.
     * The backend initializes the shop status to PENDING until Admin approval.
     */
    suspend fun registerSeller(request: SellerRegistrationRequest): Result<SellerProfile>

    /**
     * Fetches the seller profile by seller ID.
     */
    suspend fun getSellerProfile(sellerId: String): Result<SellerProfile>

    /**
     * Fetches the seller profile associated with a user ID.
     */
    suspend fun getSellerProfileByUserId(userId: String): Result<SellerProfile?>

    /**
     * Updates shop contact details, operating hours, categories supported, or image.
     */
    suspend fun updateSellerProfile(profile: SellerProfile): Result<SellerProfile>

    /**
     * Updates exact shop latitude and longitude for nearby radius discovery (default 15 km).
     */
    suspend fun updateShopLocation(sellerId: String, latitude: Double, longitude: Double): Result<Unit>

    /**
     * Toggles whether the shop is open/closed and accepting new farmer orders.
     */
    suspend fun updateShopAvailability(sellerId: String, isAvailable: Boolean): Result<Unit>

    /**
     * Observes real-time approval status from the shared backend (PENDING, APPROVED, REJECTED, SUSPENDED).
     */
    fun observeSellerStatus(sellerId: String): Flow<SellerStatus>
}
