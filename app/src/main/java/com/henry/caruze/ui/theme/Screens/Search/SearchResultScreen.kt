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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.henry.caruze.Data.CarViewModel
import com.henry.caruze.Navigation.ROUTE_SEARCH
import com.henry.caruze.ui.theme.Screens.Home.getCategoryColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchResultsScreen(navController: NavHostController, query: String?) {
    val carViewModel: CarViewModel = viewModel()

    // Fetch cars when the screen is launched
    LaunchedEffect(Unit) {
        carViewModel.loadCars()
    }

    val searchResults = remember(query, carViewModel.cars) {
        if (query.isNullOrBlank()) {
            carViewModel.cars
        } else {
            carViewModel.searchCars(query)
        }
    }
    val isLoading = carViewModel.isLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        if (query.isNullOrBlank()) "All Cars" else "Results: \"$query\"",
                        fontWeight = FontWeight.Bold,
                        color = Color.White,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
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
                },
                actions = {
                    IconButton(onClick = {
                        navController.popBackStack()
                        navController.navigate(ROUTE_SEARCH)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "New Search",
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
                // Results count
                Text(
                    text = "${searchResults.size} car(s) found" +
                            if (!query.isNullOrBlank()) " for \"$query\"" else "",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp),
                    color = Color.Gray
                )

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
                            Text(
                                text = if (query.isNullOrBlank()) {
                                    "No cars available"
                                } else {
                                    "No results found for \"$query\""
                                },
                                color = Color.Gray,
                                fontSize = 18.sp
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Try different keywords or browse all categories",
                                color = Color.Gray,
                                fontSize = 14.sp
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

//@Composable
//fun SearchResultCarCard(car: CarData, navController: NavHostController) { // FIXED: Using simple class name
//    Card(
//        modifier = Modifier
//            .fillMaxWidth()
//            .clickable {
//                car.id?.let { carId ->
//                    navController.navigate("car_details/$carId")
//                }
//            },
//        shape = RoundedCornerShape(12.dp),
//        elevation = CardDefaults.cardElevation(4.dp)
//    ) {
//        Row(
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(12.dp),
//            verticalAlignment = Alignment.CenterVertically
//        ) {
//            // Car Image
//            val imageUrl = car.imageUrl ?: ""
//            if (imageUrl.isNotEmpty()) {
//                AsyncImage(
//                    model = imageUrl,
//                    contentDescription = car.name ?: "Car image",
//                    modifier = Modifier
//                        .size(80.dp)
//                        .clip(RoundedCornerShape(8.dp)),
//                    contentScale = ContentScale.Crop,
//                    placeholder = rememberAsyncImagePainter(model = "https://via.placeholder.com/80"),
//                    error = rememberAsyncImagePainter(model = "https://via.placeholder.com/80")
//                )
//            } else {
//                Box(
//                    modifier = Modifier
//                        .size(80.dp)
//                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
//                    contentAlignment = Alignment.Center
//                ) {
//                    Icon(
//                        imageVector = Icons.Default.ArrowBack,
//                        contentDescription = "No image",
//                        tint = Color.Gray,
//                        modifier = Modifier.size(32.dp)
//                    )
//                }
//            }
//
//            Spacer(modifier = Modifier.width(12.dp))
//
//            // Car Details
//            Column(
//                modifier = Modifier.weight(1f)
//            ) {
//                Text(
//                    text = car.name ?: "Unnamed Car",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 16.sp,
//                    maxLines = 1,
//                    overflow = TextOverflow.Ellipsis
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = car.price ?: "Price not set",
//                    fontWeight = FontWeight.Bold,
//                    fontSize = 14.sp,
//                    color = Color(0xFF2E7D32)
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = car.category ?: "Other",
//                    fontSize = 12.sp,
//                    color = getCategoryColor(car.category ?: "Other"),
//                    fontWeight = FontWeight.Medium
//                )
//
//                Spacer(modifier = Modifier.height(4.dp))
//
//                Text(
//                    text = "Seller: ${car.sellerName ?: "Unknown"}",
//                    fontSize = 12.sp,
//                    color = Color.Gray
//                )
//
//                car.details?.takeIf { it.isNotBlank() }?.let { details ->
//                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = details.take(60) + if (details.length > 60) "..." else "",
//                        fontSize = 11.sp,
//                        color = Color.Gray,
//                        maxLines = 2,
//                        overflow = TextOverflow.Ellipsis
//                    )
//                }
//            }
//        }
//    }
//}

// Preview function
@androidx.compose.ui.tooling.preview.Preview(showBackground = true)
@Composable
fun SearchResultsScreenPreview() {
    SearchResultsScreen(navController = rememberNavController(), query = "Toyota")
}