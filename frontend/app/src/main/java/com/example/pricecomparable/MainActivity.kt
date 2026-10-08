package com.example.pricecomparable

import android.os.Bundle
import android.os.StrictMode
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.pricecomparable.auth.TokenManager
import com.example.pricecomparable.network.ApiClient
import com.example.pricecomparable.ui.AccountPage
import com.example.pricecomparable.ui.LoginScreen
import com.example.pricecomparable.ui.MapScreen
import com.example.pricecomparable.ui.SearchProductsScreen
import com.example.pricecomparable.ui.WelcomeScreen
import com.example.pricecomparable.ui.theme.PriceComparableTheme
import androidx.compose.foundation.layout.padding
import com.example.pricecomparable.ui.StoreOwnerProfilePage // ← ADDED TEMPORARY


// ---------- ROUTES ----------
object Routes {
    const val LOGIN = "login"
    const val MAIN = "main"
    const val SEARCH = "search"
    const val ACCOUNT = "account"
    const val MAP = "Map"
    const val WELCOME = "welcome"
    const val STORE_OWNER_PROFILE = "storeOwnerProfile"  
    const val ROLE_STORE_OWNER = "store_owner"
    const val ROLE_USER = "user"
}

// ---------- MAIN SCREEN (deleted the log in button) ----------


// ---------- NAV HOST (you already had this) ----------
@Composable
fun AppNavigation(
    navController: NavHostController,
    startDestination: String,
    tokenManager: TokenManager
) {
    NavHost(navController = navController, startDestination = startDestination) {

        composable(Routes.WELCOME) {
            WelcomeScreen(
                onSignUpClick = {
                    navController.navigate(Routes.LOGIN)
                }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(navController = navController)
        }

        composable(Routes.MAIN) {
            SearchProductsScreen()
        }



        composable(Routes.SEARCH) {
            SearchProductsScreen()
        }

        // Added to make the log out work
        composable(Routes.ACCOUNT) {
            AccountPage(
                onLogout = {
                    // Clear token
                    tokenManager.clear()

                    // Navigate to login and clear back stack
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }

        composable(Routes.MAP) {
            MapScreen()
        }

        // PERMANENT: Store Owner Profile route (needed for when auth is implemented)
        composable(Routes.STORE_OWNER_PROFILE) {
            StoreOwnerProfilePage(
                onLogout = {
                    tokenManager.clear()
                    navController.navigate(Routes.LOGIN) {
                        popUpTo(navController.graph.startDestinationId) {
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}

// ---------- BOTTOM NAV BAR (NEW --> used in homepage, maps and account page) ----------
@Composable
fun AppBottomBar(
    currentRoute: String?,
    onSearch: () -> Unit,
    onMap: () -> Unit,
    onAccount: () -> Unit
) {
    NavigationBar {

        // LEFT: MAPS
        NavigationBarItem(
            selected = currentRoute == Routes.MAP,
            onClick = onMap,
            icon = { Icon(Icons.Default.LocationOn, contentDescription = "Maps") },
            label = { Text("Maps") }
        )

        // MIDDLE: HOME (SEARCH SCREEN)
        NavigationBarItem(
            selected = currentRoute in setOf(Routes.MAIN, Routes.SEARCH),
            onClick = onSearch,
            icon = { Icon(Icons.Default.Home, contentDescription = "Home page") },
            label = { Text("Home page") }
        )

        // RIGHT: ACCOUNT
        NavigationBarItem(
            selected = currentRoute == Routes.ACCOUNT,
            onClick = onAccount,
            icon = { Icon(Icons.Default.Person, contentDescription = "Account") },
            label = { Text("Account") }
        )
    }
}

// ---------- MAIN ACTIVITY (WITH BOTTOM BAR) ----------
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        // Initialize ApiClient at application startup
        val tokenManager = TokenManager(this)
        ApiClient.createApi(tokenManager)

        // START DESTINATION: CHECK THE ROLE OF THE USER AND NAVIGATE TO THE CORRECT PAGE:
        // IF THE USER IS A STORE OWNER, NAVIGATE TO THE STORE OWNER PROFILE PAGE, OTHERWISE NAVIGATE TO THE MAIN PAGE
        // IF THE USER IS NOT LOGGED IN, NAVIGATE TO THE WELCOME PAGE
        val startDestination = if (tokenManager.getToken() != null) {
            val role = tokenManager.getRole()
            if (role == Routes.ROLE_STORE_OWNER) {
                Routes.STORE_OWNER_PROFILE
            } else {
                Routes.MAIN
            }
        } else {
            Routes.WELCOME
        }

        setContent {
            PriceComparableTheme {
                val navController = rememberNavController()
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = backStackEntry?.destination?.route

                // Hide bottom bar on welcome + login + store owner profile
                val showBottomBar = currentRoute !in setOf(Routes.WELCOME, Routes.LOGIN, Routes.STORE_OWNER_PROFILE)

                Scaffold(
                    bottomBar = {
                        if (showBottomBar) {
                            AppBottomBar(
                                currentRoute = currentRoute,
                                onSearch = {
                                    navController.navigate(Routes.SEARCH) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onMap = {
                                    navController.navigate(Routes.MAP) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                // HANDLE ACCOUNT NAVIGATION:
                                // IF THE USER IS A STORE OWNER, NAVIGATE TO THE STORE OWNER PROFILE PAGE, OTHERWISE NAVIGATE TO THE ACCOUNT PAGE
                                onAccount = {
                                    val role = tokenManager.getRole()
                                    val destination = if (role == Routes.ROLE_STORE_OWNER) {
                                        Routes.STORE_OWNER_PROFILE
                                    } else {
                                        Routes.ACCOUNT
                                    }
                                    navController.navigate(destination) {
                                        popUpTo(navController.graph.startDestinationId) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                }
                            )
                        }
                    }
                ) { innerPadding ->
                    Surface(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AppNavigation(
                            navController = navController,
                            startDestination = startDestination,
                            tokenManager = tokenManager
                        )
                    }
                }
            }
        }
    }
}
