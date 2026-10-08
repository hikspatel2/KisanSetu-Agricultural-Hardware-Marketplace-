package com.example.ui.preview

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import androidx.compose.ui.unit.dp
import com.example.data.model.*
import com.example.ui.components.*
import com.example.ui.farmer.*
import com.example.ui.theme.MyApplicationTheme

/**
 * PreviewParameterProvider for agricultural hardware products.
 * Provides sample products for Android Studio Compose previews.
 */
class ProductPreviewProvider : PreviewParameterProvider<Product> {
    override val values: Sequence<Product> = sequenceOf(
        Product(
            id = "prev_p1",
            sellerId = "seller_patel",
            sellerName = "Patel Krushi Hardware",
            name = "Jain Inline 16mm Drip Pipe (400m Roll)",
            category = "Irrigation",
            subcategory = "Drip Pipes",
            brand = "Jain Irrigation",
            sku = "IRR-JAIN-16-400",
            description = "High UV resistant 16mm inline drip lateral pipe with 40cm dripper spacing.",
            imageUrl = "https://images.unsplash.com/photo-1592417817098-8f3d69106093?w=500",
            price = 3200.0,
            discountPrice = 2850.0,
            unit = "Roll",
            stockQuantity = 24,
            minOrderQuantity = 1,
            rating = 4.8
        ),
        Product(
            id = "prev_p2",
            sellerId = "seller_kisan",
            sellerName = "Kisan Agro Hardware",
            name = "Kirloskar 5 HP Openwell Submersible Pump",
            category = "Farm Equipment",
            subcategory = "Water Pumps",
            brand = "Kirloskar",
            sku = "EQP-KIRL-5HP",
            description = "Heavy duty 3-phase openwell submersible water pump.",
            imageUrl = "https://images.unsplash.com/photo-1504917599217-d4dc5ebe6122?w=500",
            price = 24500.0,
            discountPrice = 22800.0,
            unit = "Set",
            stockQuantity = 4,
            minOrderQuantity = 1,
            rating = 4.9
        )
    )
}

/**
 * PreviewParameterProvider for hardware shop sellers.
 */
class SellerProfilePreviewProvider : PreviewParameterProvider<SellerProfile> {
    override val values: Sequence<SellerProfile> = sequenceOf(
        SellerProfile(
            id = "prev_s1",
            userId = "user_1",
            ownerName = "Ramesh Patel",
            shopName = "Patel Krushi Hardware",
            shopAddress = "Station Road, Near APMC Market",
            city = "Anand",
            state = "Gujarat",
            pincode = "388001",
            latitude = 23.0250,
            longitude = 72.5750,
            shopImageUrl = "https://images.unsplash.com/photo-1589939705384-5185137a7f0f?w=400",
            gstNumber = "24AAECP1234F1Z5",
            status = SellerStatus.APPROVED,
            rating = 4.8,
            totalRatings = 34
        ),
        SellerProfile(
            id = "prev_s2",
            userId = "user_2",
            ownerName = "Dinesh Prajapati",
            shopName = "GreenField Agro Spares",
            shopAddress = "Village Gate, Borsad Road",
            city = "Anand",
            state = "Gujarat",
            pincode = "388540",
            latitude = 23.0550,
            longitude = 72.5850,
            status = SellerStatus.PENDING,
            rating = 5.0,
            totalRatings = 0
        )
    )
}

/**
 * PreviewParameterProvider for order statuses.
 */
class OrderStatusPreviewProvider : PreviewParameterProvider<OrderStatus> {
    override val values: Sequence<OrderStatus> = sequenceOf(
        OrderStatus.PLACED,
        OrderStatus.SEARCHING_SELLER,
        OrderStatus.ACCEPTED,
        OrderStatus.PREPARING,
        OrderStatus.OUT_FOR_DELIVERY,
        OrderStatus.DELIVERED
    )
}

@Preview(name = "Product Card Preview", showBackground = true)
@Composable
fun ProductCardPreview(
    @PreviewParameter(ProductPreviewProvider::class) product: Product
) {
    MyApplicationTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            ProductCard(
                product = product,
                onProductClick = {},
                onAddToCart = {}
            )
        }
    }
}

@Preview(name = "Shop Card Preview", showBackground = true)
@Composable
fun ShopCardPreview(
    @PreviewParameter(SellerProfilePreviewProvider::class) seller: SellerProfile
) {
    MyApplicationTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            ShopCard(
                seller = seller,
                distanceKm = 2.4,
                onShopClick = {}
            )
        }
    }
}

@Preview(name = "Order Status Badge Preview", showBackground = true)
@Composable
fun OrderStatusBadgePreview(
    @PreviewParameter(OrderStatusPreviewProvider::class) status: OrderStatus
) {
    MyApplicationTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            OrderStatusBadge(status = status)
        }
    }
}

@Preview(name = "Delivery OTP Card Preview", showBackground = true)
@Composable
fun DeliveryOtpCardPreview() {
    MyApplicationTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            DeliveryOtpCard(otp = "4829")
        }
    }
}

@Preview(name = "Rating Bar Preview", showBackground = true)
@Composable
fun RatingBarPreview() {
    MyApplicationTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            RatingBar(rating = 4, onRatingChange = {})
        }
    }
}

@Preview(name = "Agricultural Equipment Card Preview", showBackground = true)
@Composable
fun AgriculturalEquipmentCardPreview(
    @PreviewParameter(ProductPreviewProvider::class) product: Product
) {
    MyApplicationTheme {
        Surface(modifier = Modifier.padding(16.dp)) {
            AgriculturalEquipmentCard(
                product = product,
                onProductClick = {},
                onAddToCart = {}
            )
        }
    }
}

@Preview(name = "Agricultural Equipment Screen Preview", showBackground = true)
@Composable
fun AgriculturalEquipmentScreenPreview() {
    MyApplicationTheme {
        AgriculturalEquipmentScreen(
            products = SampleAgriculturalEquipment,
            cartItemCount = 3,
            onProductClick = {},
            onAddToCart = {}
        )
    }
}
