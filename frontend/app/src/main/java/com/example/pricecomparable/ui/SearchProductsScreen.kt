package com.example.pricecomparable.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import android.util.Base64
import android.graphics.BitmapFactory
import com.example.pricecomparable.R
import com.example.pricecomparable.model.Product
import com.example.pricecomparable.viewmodel.SearchViewModel
import com.example.pricecomparable.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchProductsScreen() {

    val viewModel: SearchViewModel = viewModel()
    val searchQuery by viewModel.searchQuery
    val products by viewModel.products
    val keyboardController = LocalSoftwareKeyboardController.current

    val stores = listOf(
        StoreCardData("Albert Heijn", R.drawable.ah_logo),
        StoreCardData("Jumbo", R.drawable.jumbo_logo),
        StoreCardData("Lidl", R.drawable.lidl_logo)
    )

    Scaffold(

    ) { padding ->

        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(
                    Brush.verticalGradient(
                        listOf(BgTop, BgBottom)
                    )
                )
        ) {

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 75.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {

                Spacer(Modifier.height(28.dp))

                // ------------------- PERFECT LOGO FRAME -------------------
                Box(
                    modifier = Modifier
                        .size(130.dp)
                        .shadow(16.dp, CircleShape)
                        .clip(CircleShape)   // IMPORTANT
                        .background(GlassWhite.copy(alpha = 0.55f))
                        .border(3.dp, PrimaryMint, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.ic_pricelens_logo),
                        contentDescription = "PriceLens Logo",
                        modifier = Modifier
                            .fillMaxSize(),   // FILL THE WHOLE CIRCLE
                        contentScale = ContentScale.Crop  // FORCE IMAGE TO FILL
                    )
                }


                Spacer(Modifier.height(20.dp))

                // ------------------- TITLE -------------------
                Text(
                    text = "Search Products",
                    fontSize = 26.sp,
                    color = HeadingText,
                    fontWeight = FontWeight.Bold
                )

                Spacer(Modifier.height(16.dp))

                // ------------------- IMPROVED SEARCH BAR -------------------
                TextField(
                    value = searchQuery,
                    onValueChange = { viewModel.updateSearchQuery(it) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                        .height(58.dp)
                        .shadow(
                            elevation = 6.dp,
                            shape = RoundedCornerShape(20.dp)
                        ),
                    shape = RoundedCornerShape(20.dp),
                    placeholder = {
                        Text(
                            "Search for products…",
                            color = BodyText.copy(alpha = 0.65f)
                        )
                    },
                    singleLine = true,
                    leadingIcon = {
                        Icon(
                            painterResource(id = R.drawable.ic_search),
                            contentDescription = null,
                            tint = PrimaryMintDark,
                            modifier = Modifier.size(22.dp)
                        )
                    },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                    keyboardActions = KeyboardActions(
                        onSearch = { keyboardController?.hide() }
                    ),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = GlassWhite,
                        unfocusedContainerColor = GlassWhite,
                        focusedIndicatorColor = PrimaryMint,
                        unfocusedIndicatorColor = Color.Transparent,
                        cursorColor = PrimaryMint
                    )
                )

                Spacer(Modifier.height(22.dp))

                // ------------------- CONTENT SECTION -------------------
                if (searchQuery.isBlank()) {

                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 18.dp),
                        verticalArrangement = Arrangement.spacedBy(18.dp),
                        horizontalArrangement = Arrangement.spacedBy(18.dp)
                    ) {
                        items(stores) { store ->
                            StoreCard(store)
                        }
                    }

                } else {

                    if (products.isEmpty()) {
                        Text(
                            "No products found",
                            color = BodyText,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(top = 26.dp)
                        )
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(2),
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(horizontal = 18.dp),
                            verticalArrangement = Arrangement.spacedBy(18.dp),
                            horizontalArrangement = Arrangement.spacedBy(18.dp)
                        ) {
                            items(products) { product ->
                                ProductCard(product)
                            }
                        }
                    }
                }
            }
        }
    }
}

// ------------------- STORE CARD (PERFECT LOGO SIZING) -------------------

@Composable
fun StoreCard(data: StoreCardData) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
            .shadow(8.dp, RoundedCornerShape(22.dp))
            .background(GlassWhite.copy(alpha = 0.90f), RoundedCornerShape(22.dp))
            .border(1.dp, GlassBorder, RoundedCornerShape(22.dp)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(12.dp)
        ) {

            // --- BIGGER LOGO, SCALED, CENTERED PERFECTLY ---
            Image(
                painter = painterResource(id = data.imageRes),
                contentDescription = data.name,
                modifier = Modifier
                    .fillMaxWidth(0.8f)    // 80% of card width → BIG
                    .aspectRatio(1f),      // Perfect square area for logos
                contentScale = ContentScale.Fit
            )

            Spacer(Modifier.height(12.dp))

            Text(
                text = data.name,
                color = HeadingText,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}


data class StoreCardData(
    val name: String,
    val imageRes: Int
)

// ------------------- PRODUCT CARD -------------------

@Composable
fun ProductCard(product: Product) {

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.78f),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = GlassWhite.copy(alpha = 0.92f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {

        Column {

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .background(
                        GlassWhite.copy(alpha = 0.55f),
                        RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {
                if (!product.image.isNullOrEmpty()) {
                    val bitmap = remember(product.image) {
                        try {
                            val base64String = if (product.image.startsWith("data:image")) {
                                product.image.substringAfter("base64,")
                            } else {
                                product.image
                            }
                            val imageBytes = Base64.decode(base64String, Base64.DEFAULT)
                            BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                        } catch (e: Exception) {
                            null
                        }
                    }
                    
                    if (bitmap != null) {
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = product.name,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    } else {
                        Text("Image Error", color = BodyText)
                    }
                } else {
                    Text("No Image", color = BodyText)
                }
            }

            Column(
                modifier = Modifier.padding(12.dp)
            ) {

                Text(
                    product.name,
                    color = HeadingText,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(Modifier.height(4.dp))

                Text(
                    "€${product.price} • ${product.storeName}",
                    color = BodyText,
                    fontSize = 13.sp
                )
            }
        }
    }
}

// ------------------- NAV BAR -------------------


