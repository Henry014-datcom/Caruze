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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.BottomAppBar
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
import com.henry.caruze.Data.CarViewModel
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
    val carViewModel: CarViewModel = viewModel()

    // Fetch cars when the screen is launched
    LaunchedEffect(Unit) {
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
                    IconButton(onClick = { navController.navigate(ROUTE_SEARCH) }) {
                        Icon(
                            Icons.Default.Search,
                            contentDescription = "Search",
                            tint = Color.White)
                    }
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = Color.White)
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
                    "Sell" to Icons.Default.ShoppingCart,
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
                SearchBar(modifier = Modifier.padding(16.dp))

                // Quick Categories
                QuickCategories(navController)

                // Featured Cars - Show cars from ALL categories
                Text(
                    text = "Featured Cars",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )

                FeaturedCarsList(navController, allCars)

            }
        }
    }
}

@Composable
fun SearchBar(modifier: Modifier = Modifier) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { /* Open search screen */ },
        elevation = CardDefaults.cardElevation(4.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                tint = Color.Gray
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "Search for cars...",
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
fun FeaturedCarsList(navController: NavHostController, cars: List<CarData>) {
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
                FeaturedCarCard(car, navController)
            }
        }
    }
}

@Composable
fun FeaturedCarCard(
    car: CarData,
    navController: NavHostController,
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

                    // Removed the duplicate seller info since we added it above
                }
            }
        }
    }
}
@Composable
fun FeaturedCarsSection(navController: NavHostController, featuredCars: List<CarData>) {
    LazyRow(
        modifier = Modifier.padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp)
    ) {
        items(featuredCars) { car ->
            FeaturedCarCard(
                car = car,
                navController = navController,
                modifier = Modifier.padding(bottom = 8.dp)
            )
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