package com.example.pricecomparable.ui

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.pricecomparable.model.storeOwner.StoreOwnerProfileUiState
import com.example.pricecomparable.model.storeOwner.StoreProductUi
import com.example.pricecomparable.viewmodel.StoreOwnerProfileViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoreOwnerProfilePage(
    onLogout: () -> Unit = {},
    viewModel: StoreOwnerProfileViewModel = viewModel()
) {
    val state = viewModel.uiState.collectAsStateWithLifecycle().value

    StoreOwnerProfileContent(
        state = state,
        onSearchQueryChange = viewModel::onSearchQueryChange,
        onAddProductClick = viewModel::onAddProductClick,
        onEditProductClick = viewModel::onEditProductClick,
        onDeleteProductClick = viewModel::onDeleteProductClick,
        onDismissDialog = viewModel::onDismissProductDialog,
        onLogout = onLogout,
        onSaveProduct = viewModel::onSaveProduct
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StoreOwnerProfileContent(
    state: StoreOwnerProfileUiState,
    onSearchQueryChange: (String) -> Unit,
    onAddProductClick: () -> Unit,
    onEditProductClick: (StoreProductUi) -> Unit,
    onDeleteProductClick: (StoreProductUi) -> Unit,
    onDismissDialog: () -> Unit,
    onLogout: () -> Unit,
    onSaveProduct: (
        name: String,
        amount: String,
        unit: String,
        price: String,
        discount: String,
        discountStart: String,
        discountEnd: String
    ) -> Unit
) {
    Scaffold(
        topBar = {}
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(16.dp))

            // ---------------- PROFILE PHOTO ----------------
            Box(
                modifier = Modifier
                    .size(95.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(" Profile Pic", fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(14.dp))

            // --------- TAKE PICTURE / UPLOAD BUTTONS ---------
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Button(
                    onClick = {
                        // TODO: open camera
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Take Picture")
                }

                Spacer(modifier = Modifier.width(12.dp))

                Button(
                    onClick = {
                        // TODO: open gallery
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    shape = RoundedCornerShape(50)
                ) {
                    Text("Upload")
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            // ---------------- STORE INFO ----------------
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(min = 90.dp),
                shape = RoundedCornerShape(14.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 22.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        state.storeName,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Text(
                        state.storeAddress,
                        style = MaterialTheme.typography.bodyMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Spacer(modifier = Modifier.height(28.dp))

            // ---------------- ADD PRODUCT BUTTON ----------------
            Button(
                onClick = onAddProductClick,
                modifier = Modifier.fillMaxWidth(0.6f),
                shape = RoundedCornerShape(50)
            ) {
                Text("Add product")
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------- SEARCH BAR ----------------
            OutlinedTextField(
                value = state.searchQuery,
                onValueChange = onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                leadingIcon = { Icon(Icons.Default.Search, "Search") },
                placeholder = { Text("Search product") },
                singleLine = true,
                shape = RoundedCornerShape(14.dp)
            )

            Spacer(modifier = Modifier.height(12.dp))

            // ---------------- PRODUCT LIST ----------------
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(state.filteredProducts, key = { it.id ?: it.hashCode() }) { product ->
                    ProductRowCard(
                        product = product,
                        onEditClick = onEditProductClick,
                        onDeleteClick = onDeleteProductClick
                    )
                }
            }

             // LOGOUT BUTTON:
             Spacer(modifier = Modifier.height(16.dp))
            
             OutlinedButton(
                 onClick = onLogout,
                 modifier = Modifier
                     .fillMaxWidth()
                     .padding(vertical = 16.dp),
                 shape = RoundedCornerShape(14.dp),
                 colors = ButtonDefaults.outlinedButtonColors(
                     contentColor = MaterialTheme.colorScheme.error
                 )
             ) {
                 Text(
                     text = "Log out",
                     style = MaterialTheme.typography.labelLarge,
                     fontWeight = FontWeight.SemiBold
                 )
             }
         
        }

        // --------------- ADD / EDIT PRODUCT DIALOG ----------------
        if (state.isProductDialogOpen) {
            AddEditProductDialog(
                product = state.editingProduct,
                onDismiss = onDismissDialog,
                onTakePictureClick = {
                    // TODO open camera for product image
                },
                onUploadClick = {
                    // TODO open gallery for product image
                },
                onSave = onSaveProduct
            )
        }

    }
}

// ---------------- PRODUCT ROW (COMPACT) ----------------
@Composable
private fun ProductRowCard(
    product: StoreProductUi,
    onEditClick: (StoreProductUi) -> Unit,
    onDeleteClick: (StoreProductUi) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 96.dp),
        shape = RoundedCornerShape(18.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // LEFT SIDE: Photo circle
            Box(
                modifier = Modifier
                    .size(64.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text("Photo", style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.width(16.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "${product.name}  •  ${product.amount} ${product.unit}",
                    fontWeight = FontWeight.SemiBold,
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(formatPrice(product.price), style = MaterialTheme.typography.bodyMedium)
            }

            Spacer(modifier = Modifier.width(12.dp))

            Button(
                onClick = { onEditClick(product) },
                modifier = Modifier.height(40.dp),
                shape = RoundedCornerShape(50),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                Text("Edit product", style = MaterialTheme.typography.bodyMedium)
            }

            IconButton(onClick = { onDeleteClick(product) }) {
                Icon(Icons.Default.Delete, "Delete")
            }
        }
    }
}

// ---------------- ADD / EDIT PRODUCT POPUP ----------------
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditProductDialog(
    product: StoreProductUi?,
    onDismiss: () -> Unit,
    onTakePictureClick: () -> Unit,
    onUploadClick: () -> Unit,
    onSave: (
        name: String,
        amount: String,
        unit: String,
        price: String,
        discount: String,
        discountStart: String,
        discountEnd: String
    ) -> Unit
) {
    val isEditing = product != null

    // Product name
    var name by remember(product) { mutableStateOf(product?.name ?: "") }
    // Amount + unit
    var amount by remember(product) { mutableStateOf(product?.amount ?: "1") }
    var unit by remember(product) { mutableStateOf(product?.unit ?: "kg") }

    // Price: keep raw digits, convert to int and multiply by 100 to get cents
    var priceRaw by remember(product) {
        mutableStateOf(product?.price?.let { (it * 100).toInt().toString() } ?: "")
    }
    val priceFormatted = remember(priceRaw) { formatEuro(priceRaw) }

    // Optional discount fields
    var discount by remember(product) { mutableStateOf("") }
    var discountStart by remember(product) { mutableStateOf("") }
    var discountEnd by remember(product) { mutableStateOf("") }

    val unitOptions = listOf("kg", "gram", "piece", "l", "ml")
    val discountOptions = listOf("No Discount", "10%", "20%", "30%", "40%", "50%", "60%", "70%", "80%", "90%")

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {},
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isEditing) "Edit product" else "Add product",
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onDismiss) {
                    Icon(Icons.Default.Close, contentDescription = "Close dialog")
                }
            }
        },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {

                // -------- PRODUCT PHOTO PLACEHOLDER --------
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .size(120.dp)
                            .clip(CircleShape)
                            .border(1.dp, MaterialTheme.colorScheme.outline, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            "Product photo",
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center
                        )
                    }
                }


                // -------- TAKE PICTURE / UPLOAD BUTTONS --------
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = onTakePictureClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Take Picture")
                    }

                    Button(
                        onClick = onUploadClick,
                        modifier = Modifier
                            .weight(1f)
                            .height(40.dp),
                        shape = RoundedCornerShape(50)
                    ) {
                        Text("Upload")
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Product name * (required)
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product name *") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Amount + Unit * (required)
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    OutlinedTextField(
                        value = amount,
                        onValueChange = { amount = it },
                        modifier = Modifier.weight(1f),
                        label = { Text("Amount *") },
                        singleLine = true
                    )

                    var unitMenuExpanded by remember { mutableStateOf(false) }

                    ExposedDropdownMenuBox(
                        expanded = unitMenuExpanded,
                        onExpandedChange = { unitMenuExpanded = !unitMenuExpanded },
                        modifier = Modifier.weight(1f)
                    ) {
                        OutlinedTextField(
                            value = unit,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Unit *") },
                            trailingIcon = {
                                ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitMenuExpanded)
                            },
                            modifier = Modifier.menuAnchor()
                        )

                        ExposedDropdownMenu(
                            expanded = unitMenuExpanded,
                            onDismissRequest = { unitMenuExpanded = false }
                        ) {
                            unitOptions.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(option) },
                                    onClick = {
                                        unit = option
                                        unitMenuExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }

                // Price * (required)
                OutlinedTextField(
                    value = priceFormatted,
                    onValueChange = { newValue ->
                        priceRaw = newValue.filter { it.isDigit() }
                    },
                    label = { Text("Price *") },
                    placeholder = { Text("€ --,--") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Discount (optional)
                var discountMenuExpanded by remember { mutableStateOf(false) }

                ExposedDropdownMenuBox(
                    expanded = discountMenuExpanded,
                    onExpandedChange = { discountMenuExpanded = !discountMenuExpanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = discount,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Discount (optional)") },
                        placeholder = { Text("No discount") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = discountMenuExpanded)
                        },
                        modifier = Modifier.menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = discountMenuExpanded,
                        onDismissRequest = { discountMenuExpanded = false }
                    ) {
                        discountOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    discount = option
                                    discountMenuExpanded = false
                                }
                            )
                        }
                    }
                }

                // Discount dates only when selected
                if (discount.isNotBlank()) {
                    OutlinedTextField(
                        value = discountStart,
                        onValueChange = { discountStart = it },
                        label = { Text("Discount start date") },
                        placeholder = { Text("dd/mm/yyyy") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = discountEnd,
                        onValueChange = { discountEnd = it },
                        label = { Text("Discount end date") },
                        placeholder = { Text("dd/mm/yyyy") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Button(
                    onClick = {
                        onSave(
                            name,
                            amount,
                            unit,
                            priceFormatted,
                            discount,
                            discountStart,
                            discountEnd
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50)
                ) {
                    Text(if (isEditing) "Save changes" else "Add product")
                }
            }
        }
    )
}

// ---------------- PRICE FORMAT HELPER ----------------
private fun formatPrice(price: Double): String {
    return "€ %.2f".format(price).replace('.', ',')
}

private fun formatEuro(rawDigits: String): String {
    if (rawDigits.isBlank()) return ""
    val digits = rawDigits.trimStart('0').ifBlank { "0" }
    val padded = if (digits.length < 3) digits.padStart(3, '0') else digits
    val whole = padded.dropLast(2)
    val cents = padded.takeLast(2)
    return "€ $whole,$cents"
}