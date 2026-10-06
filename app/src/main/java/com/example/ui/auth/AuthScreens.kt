package com.example.ui.auth

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.UserRole
import com.example.ui.MainViewModel
import com.example.ui.ScreenDestination
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    viewModel: MainViewModel,
    onNavigateToOtp: (phone: String, role: UserRole) -> Unit,
    onRegisterSellerClick: () -> Unit
) {
    var selectedRole by remember { mutableStateOf(UserRole.FARMER) }
    var phoneNumber by remember { mutableStateOf("9825011223") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("KisanSetu Login", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White
                )
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Graphic & Title
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = AgriGreenContainer,
                modifier = Modifier.size(80.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (selectedRole == UserRole.FARMER) Icons.Default.Agriculture else Icons.Default.Storefront,
                        contentDescription = "Role icon",
                        tint = AgriGreenPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Agricultural Hardware Marketplace",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary
            )
            Text(
                text = "Connecting farmers with trusted local hardware dealers",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Role selection tabs
            Text(
                text = "SELECT YOUR ACCOUNT TYPE",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(AgriSurfaceVariant, RoundedCornerShape(12.dp))
                    .padding(4.dp)
            ) {
                Surface(
                    onClick = { selectedRole = UserRole.FARMER },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedRole == UserRole.FARMER) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Agriculture,
                            contentDescription = null,
                            tint = if (selectedRole == UserRole.FARMER) Color.White else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Farmer / Buyer",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == UserRole.FARMER) Color.White else TextSecondary
                        )
                    }
                }

                Surface(
                    onClick = { selectedRole = UserRole.SELLER },
                    shape = RoundedCornerShape(10.dp),
                    color = if (selectedRole == UserRole.SELLER) MaterialTheme.colorScheme.primary else Color.Transparent,
                    modifier = Modifier.weight(1f).fillMaxHeight()
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = if (selectedRole == UserRole.SELLER) Color.White else TextSecondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Hardware Shop",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (selectedRole == UserRole.SELLER) Color.White else TextSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = phoneNumber,
                onValueChange = { phoneNumber = it },
                label = { Text("Mobile Number") },
                prefix = { Text("+91 ", fontWeight = FontWeight.Bold) },
                leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("login_phone_input")
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = { onNavigateToOtp(phoneNumber, selectedRole) },
                enabled = phoneNumber.length >= 10,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("get_otp_button")
            ) {
                Text("Get Verification OTP", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }

            if (selectedRole == UserRole.SELLER) {
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(
                    onClick = onRegisterSellerClick,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Icon(Icons.Default.AddBusiness, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Register New Hardware Shop", fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
            HorizontalDivider()
            Spacer(modifier = Modifier.height(20.dp))

            // Quick Demo Switcher Cards
            Text(
                text = "⚡ QUICK DEMO LOGINS",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = TextSecondary,
                letterSpacing = 1.sp,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                onClick = { viewModel.switchRole(UserRole.FARMER) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Person, contentDescription = null, tint = AgriGreenPrimary)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Farmer: Mukesh Bhai Patel", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Nearby Anand (within 15 km of hardware shops)", fontSize = 12.sp, color = TextSecondary)
                    }
                    Text("Open →", color = AgriGreenPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            Card(
                onClick = { viewModel.switchRole(UserRole.SELLER) },
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AgriSurfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Store, contentDescription = null, tint = HarvestGold)
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Seller: Patel Krushi Hardware", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                        Text("Status: APPROVED • Anand APMC Market", fontSize = 12.sp, color = TextSecondary)
                    }
                    Text("Open →", color = HarvestGold, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OtpVerificationScreen(
    phone: String,
    role: UserRole,
    onVerifyOtp: (otp: String) -> Unit,
    onBack: () -> Unit
) {
    var otp by remember { mutableStateOf("1234") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Verify OTP") },
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
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = Icons.Default.MarkEmailRead,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(60.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "Enter 4-digit OTP",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Sent to +91 $phone for ${if (role == UserRole.FARMER) "Farmer" else "Seller"} account",
                fontSize = 13.sp,
                color = TextSecondary
            )

            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = otp,
                onValueChange = { if (it.length <= 6) otp = it },
                label = { Text("OTP Code") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("otp_input_field")
            )

            Text(
                text = "Default demo OTP is '1234'",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.padding(top = 6.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = { onVerifyOtp(otp) },
                enabled = otp.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .testTag("verify_otp_button")
            ) {
                Text("Verify & Continue", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SellerRegistrationScreen(
    onRegister: (
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
    ) -> Unit,
    onBack: () -> Unit
) {
    var ownerName by remember { mutableStateOf("Dinesh Prajapati") }
    var phone by remember { mutableStateOf("9879012345") }
    var shopName by remember { mutableStateOf("GreenField Agro Hardware") }
    var shopAddress by remember { mutableStateOf("Near Borsad Cross Road, APMC Sub-Yard") }
    var city by remember { mutableStateOf("Anand") }
    var state by remember { mutableStateOf("Gujarat") }
    var pincode by remember { mutableStateOf("388001") }
    var latitude by remember { mutableStateOf("23.0300") }
    var longitude by remember { mutableStateOf("72.5800") }
    var gstNumber by remember { mutableStateOf("24AAHPG1234K1Z0") }
    var bankAccount by remember { mutableStateOf("123456789012") }
    var ifscCode by remember { mutableStateOf("SBIN0000543") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Hardware Shop Registration") },
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
                .padding(20.dp)
        ) {
            // Approval notice card
            Card(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF9C4)),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFFF57F17))
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = "Once submitted, your shop status will be PENDING. An Admin must approve your shop before you can receive nearby farmer orders.",
                        fontSize = 12.sp,
                        color = Color(0xFF795548),
                        lineHeight = 16.sp
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Owner & Shop Details", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ownerName,
                onValueChange = { ownerName = it },
                label = { Text("Owner Full Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                label = { Text("Contact Phone") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = shopName,
                onValueChange = { shopName = it },
                label = { Text("Shop / Business Name") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = shopAddress,
                onValueChange = { shopAddress = it },
                label = { Text("Shop Address") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = city,
                    onValueChange = { city = it },
                    label = { Text("City") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = pincode,
                    onValueChange = { pincode = it },
                    label = { Text("Pincode") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("GPS Location (For 15 KM Delivery Radius)", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = latitude,
                    onValueChange = { latitude = it },
                    label = { Text("Latitude") },
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.width(8.dp))
                OutlinedTextField(
                    value = longitude,
                    onValueChange = { longitude = it },
                    label = { Text("Longitude") },
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Business & Bank Verification", fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = gstNumber,
                onValueChange = { gstNumber = it },
                label = { Text("GST Number (Optional)") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = bankAccount,
                onValueChange = { bankAccount = it },
                label = { Text("Bank Account Number") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(8.dp))

            OutlinedTextField(
                value = ifscCode,
                onValueChange = { ifscCode = it },
                label = { Text("Bank IFSC Code") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    onRegister(
                        ownerName,
                        phone,
                        shopName,
                        shopAddress,
                        city,
                        state,
                        pincode,
                        latitude.toDoubleOrNull() ?: 23.0300,
                        longitude.toDoubleOrNull() ?: 72.5800,
                        gstNumber,
                        bankAccount,
                        ifscCode
                    )
                },
                enabled = ownerName.isNotEmpty() && shopName.isNotEmpty() && shopAddress.isNotEmpty(),
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("submit_seller_registration")
            ) {
                Text("Submit Registration for Approval", fontSize = 15.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
