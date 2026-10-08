package com.example.pricecomparable.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import com.example.pricecomparable.R
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.pricecomparable.Routes
import com.example.pricecomparable.auth.TokenManager
import com.example.pricecomparable.viewmodel.LoginViewModel

// Import your theme colors
import com.example.pricecomparable.ui.theme.*

@Composable
fun LoginScreen(
    navController: NavHostController,
    viewModel: LoginViewModel = viewModel()
) {
    val context = LocalContext.current
    val tokenManager = remember { TokenManager(context) }

    // Collect state from ViewModel
    val uiState by viewModel.uiState.collectAsState()

    // THIS CODE BLOCK HANDLES NAVIGATION WHEN LOGIN SUCCEEDS AND CHECKS THE ROLE OF THE USER:
    LaunchedEffect(uiState.loginSuccess) {
        if (uiState.loginSuccess) {
            val role = tokenManager.getRole()
            if (role == Routes.ROLE_STORE_OWNER) {
                navController.navigate(Routes.STORE_OWNER_PROFILE) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            } else {
                navController.navigate(Routes.MAIN) {
                    popUpTo(Routes.LOGIN) { inclusive = true }
                }
            }
            
        }
    }

    // Success dialog
    if (uiState.showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { },
            title = { Text("Success") },
            text = { Text("You have successfully signed up! Proceed to Login") },
            confirmButton = {
                Button(
                    onClick = { viewModel.onDismissSuccessDialog() },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryMint)
                ) {
                    Text("OK", color = Color.White)
                }
            }
        )
    }

    // Background gradient (same as Search screen)
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(BgTop, BgBottom)
                )
            ),
        contentAlignment = Alignment.TopCenter
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp)
                .padding(top = 48.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            // ---------- LOGO CIRCLE (Same style as WelcomeScreen) ----------
            Box(
                modifier = Modifier
                    .size(160.dp)
                    .background(
                        color = Color.White.copy(alpha = 0.6f),
                        shape = CircleShape
                    )
                    .border(
                        width = 3.dp,
                        color = Color(0xFF4CD7C6),  // PrimaryMint equivalent
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_pricelens_logo),
                    contentDescription = "PriceLens logo",
                    modifier = Modifier
                        .size(160.dp)
                        .padding(4.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = if (uiState.signUpMode) "Create your account!" else "Welcome back!",
                style = MaterialTheme.typography.headlineSmall,
                color = HeadingText
            )

            Spacer(Modifier.height(32.dp))

            // ---------------- FORM CARD (updated to glass design) ----------------
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        GlassWhite,
                        shape = RoundedCornerShape(20.dp)
                    )
                    .padding(20.dp)
            ) {

                // ACCOUNT TYPE TOGGLE (only in signup mode) ---------------------------------
                if (uiState.signUpMode) {
                    TabRow(
                        selectedTabIndex = if (uiState.isStoreOwner) 1 else 0,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp)),
                        containerColor = Color.White.copy(alpha = 0.5f),
                        contentColor = PrimaryMintDark,
                        indicator = {},
                        divider = {}
                    ) {
                        Tab(
                            selected = !uiState.isStoreOwner,
                            onClick = { viewModel.onStoreOwnerChange(false) },
                            modifier = Modifier
                                .background(
                                    if (!uiState.isStoreOwner) PrimaryMint else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                "Customer",
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = if (!uiState.isStoreOwner) Color.White else HeadingText
                            )
                        }
                        Tab(
                            selected = uiState.isStoreOwner,
                            onClick = { viewModel.onStoreOwnerChange(true) },
                            modifier = Modifier
                                .background(
                                    if (uiState.isStoreOwner) PrimaryMint else Color.Transparent,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            Text(
                                "Store Owner",
                                modifier = Modifier.padding(vertical = 12.dp),
                                color = if (uiState.isStoreOwner) Color.White else HeadingText
                            )
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // FULL NAME (only in signup mode) ---------------------------------
                if (uiState.signUpMode) {
                    Text("Full Name *", color = HeadingText)
                    TextField(
                        value = uiState.fullName,
                        onValueChange = { viewModel.onFullNameChange(it) },
                        placeholder = { Text(if (uiState.isStoreOwner) "Enter your store name" else "Enter your full name", color = BodyText) },
                        leadingIcon = {
                            Icon(
                                Icons.Default.Person,
                                contentDescription = null,
                                tint = PrimaryMintDark
                            )
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PrimaryMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = PrimaryMint
                        )
                    )
                    Spacer(Modifier.height(16.dp))
                }

                // EMAIL ---------------------------------
                Text("Email *", color = HeadingText)
                TextField(
                    value = uiState.email,
                    onValueChange = { viewModel.onEmailChange(it) },
                    placeholder = { Text("Enter your email", color = BodyText) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Email,
                            contentDescription = null,
                            tint = PrimaryMintDark
                        )
                    },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PrimaryMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = PrimaryMint
                    )
                )

                Spacer(Modifier.height(16.dp))

                // PASSWORD ---------------------------------
                Text("Password *", color = HeadingText)
                TextField(
                    value = uiState.password,
                    onValueChange = { viewModel.onPasswordChange(it) },
                    placeholder = { Text("Enter your password", color = BodyText) },
                    leadingIcon = {
                        Icon(
                            Icons.Default.Lock,
                            contentDescription = null,
                            tint = PrimaryMintDark
                        )
                    },
                    trailingIcon = {
                        TextButton(onClick = { viewModel.onTogglePasswordVisibility() }) {
                            Text(
                                if (uiState.showPassword) "HIDE" else "SHOW",
                                color = PrimaryMintDark
                            )
                        }
                    },
                    singleLine = true,
                    visualTransformation =
                        if (uiState.showPassword) VisualTransformation.None else PasswordVisualTransformation(),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, PrimaryMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                    colors = TextFieldDefaults.colors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        cursorColor = PrimaryMint
                    )
                )

                // STORE OWNER ADDRESS FIELDS (only in signup mode when store owner selected) ---------------------------------
                if (uiState.signUpMode && uiState.isStoreOwner) {
                    Spacer(Modifier.height(20.dp))
                    Text("Store Address (Optional)", color = HeadingText, style = MaterialTheme.typography.titleSmall)
                    Spacer(Modifier.height(12.dp))

                    TextField(
                        value = uiState.country,
                        onValueChange = { viewModel.onCountryChange(it) },
                        placeholder = { Text("Country", color = BodyText) },
                        leadingIcon = {
                            Icon(Icons.Default.Place, contentDescription = null, tint = PrimaryMintDark)
                        },
                        singleLine = true,
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(1.dp, PrimaryMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                        colors = TextFieldDefaults.colors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = Color.White,
                            unfocusedContainerColor = Color.White,
                            cursorColor = PrimaryMint
                        )
                    )
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = uiState.city,
                            onValueChange = { viewModel.onCityChange(it) },
                            placeholder = { Text("City", color = BodyText) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(3f)
                                .border(1.dp, PrimaryMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = PrimaryMint
                            )
                        )
                        TextField(
                            value = uiState.postalCode,
                            onValueChange = { viewModel.onPostalCodeChange(it) },
                            placeholder = { Text("Post Code", color = BodyText) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(2f)
                                .border(1.dp, PrimaryMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = PrimaryMint
                            )
                        )
                    }
                    Spacer(Modifier.height(12.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        TextField(
                            value = uiState.streetName,
                            onValueChange = { viewModel.onStreetNameChange(it) },
                            placeholder = { Text("Street Name", color = BodyText) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(3f)
                                .border(1.dp, PrimaryMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = PrimaryMint
                            )
                        )
                        TextField(
                            value = uiState.streetNumber,
                            onValueChange = { viewModel.onStreetNumberChange(it) },
                            placeholder = { Text("Number", color = BodyText) },
                            singleLine = true,
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier
                                .weight(2f)
                                .border(1.dp, PrimaryMint.copy(alpha = 0.3f), RoundedCornerShape(12.dp)),
                            colors = TextFieldDefaults.colors(
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                                focusedContainerColor = Color.White,
                                unfocusedContainerColor = Color.White,
                                cursorColor = PrimaryMint
                            )
                        )
                    }
                }

                Spacer(Modifier.height(20.dp))

                // Error message
                uiState.errorMessage?.let {
                    Text(
                        text = it,
                        color = Color.Red,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }

                // SIGN IN / SIGN UP BUTTON ------------------------------------
                val isFormValid = if (uiState.signUpMode) {
                    uiState.email.isNotBlank() && uiState.password.isNotBlank() && uiState.fullName.isNotBlank()
                } else {
                    uiState.email.isNotBlank() && uiState.password.isNotBlank()
                }

                Button(
                    onClick = { viewModel.submitForm(tokenManager) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    enabled = !uiState.isLoading && isFormValid,
                    colors = ButtonDefaults.buttonColors(containerColor = PrimaryMint)
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(22.dp),
                            color = Color.White
                        )
                    } else {
                        Text(
                            if (uiState.signUpMode) "Sign Up" else "Login",
                            color = Color.White
                        )
                    }
                }
            }

            Spacer(Modifier.height(28.dp))

            Text(
                if (uiState.signUpMode) "Already have an account?" else "Don't have an account?",
                color = HeadingText
            )

            Spacer(Modifier.height(12.dp))

            Button(
                onClick = { viewModel.onToggleSignUpMode() },
                modifier = Modifier
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(containerColor = PrimaryMintDark)
            ) {
                Text(
                    if (uiState.signUpMode) "Login" else "Sign Up",
                    color = Color.White
                )
            }
        }
    }

    // Bottom navigation unchanged
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {

    }
}

@Composable
fun NavigationBar() {
    NavigationBar(
        containerColor = Color.LightGray,
        contentColor = Color.Black
    ) {
        NavigationBarItem(
            icon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            selected = false,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Home, contentDescription = "Home") },
            selected = true,
            onClick = { }
        )
        NavigationBarItem(
            icon = { Icon(Icons.Default.Person, contentDescription = "Profile") },
            selected = false,
            onClick = { }
        )
    }
}
