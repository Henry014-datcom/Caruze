package com.henry.caruze.ui.theme.Screens.Home

import CarData
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Sell
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.google.firebase.auth.FirebaseAuth
import com.henry.caruze.Data.CarViewModel
import com.henry.caruze.Data.UserViewModel
import com.henry.caruze.Navigation.ROUTE_LOGIN
import com.henry.caruze.Navigation.ROUTE_LUXURY
import com.henry.caruze.Navigation.ROUTE_PROFILE
import com.henry.caruze.Navigation.ROUTE_SEARCH
import com.henry.caruze.Navigation.ROUTE_SEDAN
import com.henry.caruze.Navigation.ROUTE_SELL
import com.henry.caruze.Navigation.ROUTE_SUV
import com.henry.caruze.Navigation.ROUTE_TRUCK

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(navController: NavHostController) {
    var selectedTab by remember { mutableStateOf(0) }
    var showLogoutDialog by remember { mutableStateOf(false) }
    val carViewModel: CarViewModel = viewModel()
    val userViewModel: UserViewModel = viewModel()

    // Fetch current user and cars when the screen is launched
    LaunchedEffect(Unit) {
        userViewModel.fetchCurrentUser()
        carViewModel.loadCars()
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text("Caruze",
                        fontWeight = FontWeight.Bold,
                        color = Color.White)
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                ),
                actions = {
                    // Show user role in top bar if available
                    userViewModel.currentUser.value?.let { user ->
                        if (user.role == "Admin") {
                            Text(
                                text = "Admin",
                                color = Color.White,
                                fontSize = 14.sp,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }

                    IconButton(onClick = { navController.navigate(ROUTE_SEARCH) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White)
                    }
                    IconButton(onClick = { showLogoutDialog = true }) {
                        Icon(
                            Icons.Default.ExitToApp,
                            contentDescription = "Logout",
                            tint = Color.White
                        )
                    }
                }
            )
        },
        bottomBar = {
            BottomAppBar(
                containerColor = Color(0xFF2E7D32)
            ) {
                val bottomItems = listOf(
                    "Home" to Icons.Default.Home,
                    "Sell" to Icons.Default.Sell,
                    "Profile" to Icons.Default.Person
                )

                bottomItems.forEachIndexed { index, (title, icon) ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier
                            .weight(1f)
                            .clickable {
                                selectedTab = index
                                when (title) {
                                    "Home" -> {}
                                    "Sell" -> navController.navigate(ROUTE_SELL)
                                    "Profile" -> navController.navigate(ROUTE_PROFILE)
                                }
                            }
                    ) {
                        Icon(
                            imageVector = icon,
                            contentDescription = title,
                            tint = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = title,
                            color = if (selectedTab == index) Color.White else Color.White.copy(alpha = 0.7f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        // Logout Confirmation Dialog
        if (showLogoutDialog) {
            LogoutConfirmationDialog(
                onConfirm = {
                    // Perform logout
                    FirebaseAuth.getInstance().signOut()
                    // Clear car data
                    carViewModel.clearAllData()
                    userViewModel.currentUser.value = null
                    // Navigate to login screen
                    navController.navigate(ROUTE_LOGIN) {
                        popUpTo(0) // Clear back stack
                    }
                    showLogoutDialog = false
                },
                onDismiss = { showLogoutDialog = false }
            )
        }

        // Use the observable cars list directly instead of getAllCars()
        val allCars = carViewModel.cars
        val isLoading = carViewModel.isLoading

        if (isLoading) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF5F5F5))
            ) {
                // Search Bar
                SearchBar(modifier = Modifier.padding(16.dp), navController = navController)

                // Quick Categories
                QuickCategories(navController)

                // Featured Cars - Show cars from ALL categories
                Text(
                    text = "Featured Cars",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                FeaturedCarsList(navController, allCars, userViewModel)
            }
        }
    }
}

@Composable
fun LogoutConfirmationDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text("Logout", fontWeight = FontWeight.Bold)
        },
        text = {
            Text("Are you sure you want to logout?")
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
            ) {
                Text("Yes, Logout", color = Color.White)
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray)
            ) {
                Text("Cancel", color = Color.White)
            }
        }
    )
}

@Composable
fun SearchBar(modifier: Modifier = Modifier, navController: NavHostController) {
    val carViewModel: CarViewModel = viewModel()
    var isLoading by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable {
                isLoading = true
                // Fetch data from Firebase when search is clicked
                carViewModel.loadCars()
                // Navigate to search screen
                navController.navigate(ROUTE_SEARCH)
                isLoading = false
            },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp,
                    color = Color.Gray
                )
            } else {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = Color.Gray
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isLoading) "Loading cars..." else "Search for cars...",
                color = Color.Gray
            )
        }
    }
}

@Composable
fun QuickCategories(navController: NavHostController) {
    val categories = listOf(
        "All" to Pair(Color(0xFF2E7D32), null),
        "Sedan" to Pair(Color(0xFF2196F3), ROUTE_SEDAN),
        "SUV" to Pair(Color(0xFFFF9800), ROUTE_SUV),
        "Truck" to Pair(Color(0xFFF44336), ROUTE_TRUCK),
        "Luxury" to Pair(Color(0xFF9C27B0), ROUTE_LUXURY)
    )

    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(categories) { category ->
            val (title, colorAndRoute) = category
            val (color, route) = colorAndRoute
            Card(
                modifier = Modifier
                    .padding(4.dp)
                    .clickable {
                        route?.let { navController.navigate(it) }
                    },
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = color)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}

@Composable
fun FeaturedCarsList(navController: NavHostController, cars: List<CarData>, userViewModel: UserViewModel) {
    // Get featured cars (first 5 cars) with null safety
    val featuredCars = cars.take(5)

    if (featuredCars.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "No featured cars available",
                color = Color.Gray
            )
        }
    } else {
        LazyRow(
            modifier = Modifier.padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 16.dp)
        ) {
            items(featuredCars) { car ->
                FeaturedCarCard(car, navController, userViewModel)
            }
        }
    }
}

@Composable
fun FeaturedCarCard(
    car: CarData,
    navController: NavHostController,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .width(280.dp)
            .clickable {
                // Navigate to car details when card is clicked
                car.id?.let { carId ->
                    navController.navigate("car_details/$carId")
                }
            },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(8.dp)
    ) {
        Column {
            // Car Image
            val imageUrl = car.imageUrl ?: ""
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = car.name ?: "Featured car",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = rememberAsyncImagePainter(model = "https://via.placeholder.com/280x160"),
                    error = rememberAsyncImagePainter(model = "https://via.placeholder.com/280x160")
                )
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(160.dp)
                        .background(Color.LightGray, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "No image",
                        tint = Color.Gray,
                        modifier = Modifier.size(48.dp)
                    )
                }
            }

            // Car Details
            Column(
                modifier = Modifier
                    .padding(12.dp)
                    .fillMaxWidth()
            ) {
                Text(
                    text = car.name ?: "Unnamed Car",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = car.price ?: "Price not set",
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = Color(0xFF2E7D32)
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = car.details?.take(60) ?: "No description available",
                    fontSize = 12.sp,
                    color = Color.Gray,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Seller Information Section
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                        .padding(8.dp)
                ) {
                    Text(
                        text = "Seller: ${car.sellerName ?: "Unknown"}",
                        fontSize = 12.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = "Contact: ${car.sellerPhone ?: "Not provided"}",
                        fontSize = 11.sp,
                        color = Color.White
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = car.category ?: "Other",
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = getCategoryColor(car.category ?: "Other")
                    )

                    // Show admin badge if user is admin
                    userViewModel.currentUser.value?.let { user ->
                        if (user.role == "Admin") {
                            Text(
                                text = "Admin View",
                                fontSize = 10.sp,
                                color = Color.Red,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }
    }
}

// Helper function to get category color with safe default
fun getCategoryColor(category: String): Color {
    return when (category) {
        "Sedan" -> Color(0xFF2196F3)
        "SUV" -> Color(0xFFFF9800)
        "Truck" -> Color(0xFFF44336)
        "Luxury" -> Color(0xFF9C27B0)
        else -> Color.Gray
    }
}

@Preview(showBackground = true)
@Composable
fun HomePreview() {
    HomeScreen(rememberNavController())
}