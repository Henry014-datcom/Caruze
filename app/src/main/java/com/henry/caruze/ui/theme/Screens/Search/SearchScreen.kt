package com.henry.caruze.ui.theme.Screens.Search
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.henry.caruze.Data.CarViewModel
import com.henry.caruze.Navigation.ROUTE_LUXURY
import com.henry.caruze.Navigation.ROUTE_SEDAN
import com.henry.caruze.Navigation.ROUTE_SUV
import com.henry.caruze.Navigation.ROUTE_TRUCK
import com.henry.caruze.ui.theme.Screens.Home.getCategoryColor
import java.net.URLEncoder

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(navController: NavHostController) {
    var searchQuery by remember { mutableStateOf("") }
    val carViewModel: CarViewModel = viewModel()
    val focusManager = LocalFocusManager.current

    // Fetch cars when the screen is launched
    LaunchedEffect(Unit) {
        carViewModel.loadCars()
    }

    val searchResults = remember(searchQuery, carViewModel.cars) {
        if (searchQuery.isBlank()) {
            carViewModel.cars
        } else {
            carViewModel.searchCars(searchQuery)
        }
    }
    val isLoading = carViewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    TextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        placeholder = { Text("Search for cars...", color = Color.White.copy(alpha = 0.8f)) },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedContainerColor = Color.Transparent,
                            unfocusedContainerColor = Color.Transparent,
                            disabledContainerColor = Color.Transparent,
                            focusedIndicatorColor = Color.White,
                            unfocusedIndicatorColor = Color.White.copy(alpha = 0.7f),
                            focusedTextColor = Color.White,
                            unfocusedTextColor = Color.White,
                            cursorColor = Color.White
                        ),
                        textStyle = TextStyle(color = Color.White, fontSize = 16.sp),
                        singleLine = true,
                        leadingIcon = {
                            Icon(
                                Icons.Default.Search,
                                contentDescription = "Search",
                                tint = Color.White
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotBlank()) {
                                IconButton(
                                    onClick = { searchQuery = "" }
                                ) {
                                    Icon(
                                        Icons.Default.Close,
                                        contentDescription = "Clear",
                                        tint = Color.White
                                    )
                                }
                            }
                        },
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Text,
                            imeAction = ImeAction.Search
                        ),
                        keyboardActions = KeyboardActions(
                            onSearch = {
                                focusManager.clearFocus()
                                // Navigate to search results
                                navController.navigate("search_results/${URLEncoder.encode(searchQuery, "UTF-8")}")
                            }
                        )
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF2E7D32),
                    titleContentColor = Color.White
                ),
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = Color.White
                        )
                    }
                }
            )
        }
    ) { innerPadding ->
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
                    .background(Color(0xFFF5F5F5))
            ) {
                if (searchQuery.isBlank()) {
                    // Show categories and recent searches when no search query
                    Column {
                        Text(
                            text = "Browse Categories",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp),
                            fontWeight = FontWeight.Bold
                        )

                        QuickCategories(navController)

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "All Cars",
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.padding(16.dp, 0.dp, 16.dp, 8.dp),
                            fontWeight = FontWeight.Bold
                        )
                    }
                } else {
                    Text(
                        text = "${searchResults.size} result(s) found",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp),
                        color = Color.Gray
                    )
                }

                if (searchResults.isEmpty()) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Search,
                                contentDescription = "No results",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (searchQuery.isBlank()) {
                                    "Search for cars by name, category, or seller"
                                } else {
                                    "No results found for \"$searchQuery\""
                                },
                                color = Color.Gray,
                                fontSize = 16.sp,
                                textAlign = TextAlign.Center
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try different keywords or browse categories",
                                color = Color.Gray,
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(searchResults) { car ->
                            SearchResultCarCard(car, navController)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun QuickCategories(navController: NavHostController) {
    val categories = listOf(
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
                        navController.navigate(route)
                    },
                elevation = CardDefaults.cardElevation(2.dp),
                colors = CardDefaults.cardColors(containerColor = color)
            ) {
                Text(
                    text = title,
                    color = Color.White,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
fun SearchResultCarCard(car: CarData, navController: NavHostController) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                car.id?.let { carId ->
                    navController.navigate("car_details/$carId")
                }
            },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Car Image
            val imageUrl = car.imageUrl ?: ""
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = car.name ?: "Car image",
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = rememberAsyncImagePainter(model = "https://via.placeholder.com/80"),
                    error = rememberAsyncImagePainter(model = "https://via.placeholder.com/80")
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "No image",
                        tint = Color.Gray,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Car Details
            Column(
                modifier = Modifier.weight(1f)
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
                    text = car.category ?: "Other",
                    fontSize = 12.sp,
                    color = getCategoryColor(car.category ?: "Other"),
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Seller: ${car.sellerName ?: "Unknown"}",
                    fontSize = 12.sp,
                    color = Color.Gray
                )

                car.details?.takeIf { it.isNotBlank() }?.let { details ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = details.take(60) + if (details.length > 60) "..." else "",
                        fontSize = 11.sp,
                        color = Color.Gray,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }
    }
}

// Preview function
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SearchScreenPreview() {
    SearchScreen(navController = rememberNavController())
}