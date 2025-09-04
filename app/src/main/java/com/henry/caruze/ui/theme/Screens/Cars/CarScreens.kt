package com.henry.caruze.ui.theme.Screens.Cars

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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.compose.rememberAsyncImagePainter
import com.henry.caruze.Data.CarViewModel
import com.henry.caruze.Navigation.ROUTE_UPDATE_CAR
import com.google.firebase.auth.FirebaseAuth
import com.henry.caruze.ui.theme.Screens.Home.getCategoryColor
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

// Sedan Category Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SedanScreen(navController: NavHostController) {
    val carViewModel: CarViewModel = viewModel()
    val sedanCars = carViewModel.getCarsByCategory("Sedan")
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        carViewModel.loadCars()
    }

    CategoryScreen(
        navController = navController,
        categoryName = "Sedan",
        categoryColor = Color(0xFF2196F3),
        cars = sedanCars,
        isLoading = carViewModel.isLoading,
        currentUserId = currentUserId
    )
}

// SUV Category Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SuvScreen(navController: NavHostController) {
    val carViewModel: CarViewModel = viewModel()
    val suvCars = carViewModel.getCarsByCategory("SUV")
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        carViewModel.loadCars()
    }

    CategoryScreen(
        navController = navController,
        categoryName = "SUV",
        categoryColor = Color(0xFFFF9800),
        cars = suvCars,
        isLoading = carViewModel.isLoading,
        currentUserId = currentUserId
    )
}

// Truck Category Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TruckScreen(navController: NavHostController) {
    val carViewModel: CarViewModel = viewModel()
    val truckCars = carViewModel.getCarsByCategory("Truck")
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        carViewModel.loadCars()
    }

    CategoryScreen(
        navController = navController,
        categoryName = "Truck",
        categoryColor = Color(0xFFF44336),
        cars = truckCars,
        isLoading = carViewModel.isLoading,
        currentUserId = currentUserId
    )
}

// Luxury Category Screen
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LuxuryScreen(navController: NavHostController) {
    val carViewModel: CarViewModel = viewModel()
    val luxuryCars = carViewModel.getCarsByCategory("Luxury")
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    LaunchedEffect(Unit) {
        carViewModel.loadCars()
    }

    CategoryScreen(
        navController = navController,
        categoryName = "Luxury",
        categoryColor = Color(0xFF9C27B0),
        cars = luxuryCars,
        isLoading = carViewModel.isLoading,
        currentUserId = currentUserId
    )
}

// Reusable Category Screen Template
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryScreen(
    navController: NavHostController,
    categoryName: String,
    categoryColor: Color,
    cars: List<CarData>,
    isLoading: Boolean,
    currentUserId: String
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "$categoryName Cars",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = categoryColor,
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
        } else if (cars.isEmpty()) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "No $categoryName cars available yet",
                    fontSize = 18.sp,
                    color = Color.Gray
                )
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5)),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                items(cars) { car ->
                    CategoryCarCard(
                        car = car,
                        navController = navController,
                        categoryColor = categoryColor,
                        currentUserId = currentUserId,
                        onDelete = { carId ->
                            // Handle delete in parent if needed
                        }
                    )
                }
            }
        }
    }
}

// Car Card for Category Screens with Edit/Delete options
// Car Card for Category Screens with Edit/Delete options and click functionality
@Composable
fun CategoryCarCard(
    car: CarData,
    navController: NavHostController,
    categoryColor: Color,
    currentUserId: String,
    onDelete: (String) -> Unit
) {
    val carViewModel: CarViewModel = viewModel()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Car") },
            text = { Text("Are you sure you want to delete this car listing? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        car.id?.let { carId ->
                            // Delete the car
                            CoroutineScope(Dispatchers.IO).launch {
                                carViewModel.deleteCar(carId)
                                onDelete(carId)
                            }
                        }
                        showDeleteDialog = false
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showDeleteDialog = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                // Navigate to car details when card is clicked
                car.id?.let { carId ->
                    navController.navigate("car_details/$carId")
                }
            },
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Action buttons (only show if user owns the car)
            if (car.userId == currentUserId) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    // Edit Button
                    IconButton(
                        onClick = {
                            car.id?.let { carId ->
                                navController.navigate("$ROUTE_UPDATE_CAR/$carId")
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Edit,
                            contentDescription = "Edit",
                            tint = Color.Blue
                        )
                    }

                    // Delete Button
                    IconButton(
                        onClick = { showDeleteDialog = true }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete",
                            tint = Color.Red
                        )
                    }
                }
            }

            // Display actual image from Cloudinary with null safety
            val imageUrl = car.imageUrl ?: ""
            if (imageUrl.isNotEmpty()) {
                AsyncImage(
                    model = imageUrl,
                    contentDescription = car.name ?: "Car image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .clip(RoundedCornerShape(8.dp)),
                    contentScale = ContentScale.Crop,
                    placeholder = rememberAsyncImagePainter(model = "https://via.placeholder.com/150"),
                    error = rememberAsyncImagePainter(model = "https://via.placeholder.com/150")
                )
            } else {
                // Fallback placeholder
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(180.dp)
                        .background(Color.LightGray, RoundedCornerShape(8.dp)),
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

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = car.name ?: "Unnamed Car",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                color = categoryColor
            )
            Text(
                text = car.price ?: "Price not set",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Text(
                text = (car.details?.take(100) ?: "No description") + if ((car.details?.length ?: 0) > 100) "..." else "",
                color = Color.Gray,
                fontSize = 14.sp,
                modifier = Modifier.padding(bottom = 4.dp)
            )

            // Show seller info with null safety
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Seller: ${car.sellerName ?: "Unknown"}",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                    Text(
                        text = "Contact: ${car.sellerPhone ?: "Not provided"}",
                        color = Color.DarkGray,
                        fontSize = 12.sp
                    )
                }
                Text(
                    text = car.category ?: "Other",
                    color = getCategoryColor(car.category ?: "Other"),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
