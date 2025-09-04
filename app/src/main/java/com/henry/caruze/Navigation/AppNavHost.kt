// Navigation/AppNavHost.kt
package com.henry.caruze.Navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.henry.caruze.ui.theme.Screens.Cars.CarDetailsScreen
import com.henry.caruze.ui.theme.Screens.Cars.LuxuryScreen
import com.henry.caruze.ui.theme.Screens.Cars.SedanScreen
import com.henry.caruze.ui.theme.Screens.Cars.SuvScreen
import com.henry.caruze.ui.theme.Screens.Cars.TruckScreen
import com.henry.caruze.ui.theme.Screens.Home.HomeScreen
import com.henry.caruze.ui.theme.Screens.Login.LoginScreen
import com.henry.caruze.ui.theme.Screens.Profile.ProfileScreen
import com.henry.caruze.ui.theme.Screens.Register.RegisterScreen
import com.henry.caruze.ui.theme.Screens.Search.SearchResultsScreen
import com.henry.caruze.ui.theme.Screens.Search.SearchScreen
import com.henry.caruze.ui.theme.Screens.Sell.SellScreen
import com.henry.caruze.ui.theme.Screens.Splash.SplashScreen
import com.henry.caruze.ui.theme.Screens.Update.UpdateScreen

@Composable
fun AppNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    startDestination: String = ROUTE_SPLASH
) {
    NavHost(
        navController = navController,
        modifier = modifier,
        startDestination = startDestination
    ) {
        composable(ROUTE_SPLASH) {
            SplashScreen(navController)
        }
        composable(ROUTE_REGISTER) {
            RegisterScreen(navController)
        }
        composable(ROUTE_LOGIN) {
            LoginScreen(navController)
        }
        composable(ROUTE_HOME) {
            HomeScreen(navController)
        }
        composable(ROUTE_SELL) {
            SellScreen(navController)
        }
        composable(ROUTE_SEDAN) {
            SedanScreen(navController)
        }
        composable(ROUTE_SUV) {
            SuvScreen(navController)
        }
        composable(ROUTE_TRUCK) {
            TruckScreen(navController)
        }
        composable(ROUTE_LUXURY) {
            LuxuryScreen(navController)
        }
        composable(ROUTE_PROFILE) {
            ProfileScreen(navController)
        }
        composable("update_car/{carId}") { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            UpdateScreen(navController = navController, carId = carId)
        }
        composable(
            route = ROUTE_CAR_DETAILS,
            arguments = listOf(
                navArgument("carId") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val carId = backStackEntry.arguments?.getString("carId") ?: ""
            CarDetailsScreen(navController, carId)
        }
        composable(ROUTE_SEARCH) {
            SearchScreen(navController)
        }
        composable("search_results/{query}") { backStackEntry ->
            val query = backStackEntry.arguments?.getString("query") ?: ""
            SearchResultsScreen(navController, query)
        }
    }
}