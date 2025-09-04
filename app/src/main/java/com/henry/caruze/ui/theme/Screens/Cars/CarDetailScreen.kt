package com.henry.caruze.ui.theme.Screens.Cars

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Call
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
import com.henry.caruze.Navigation.ROUTE_UPDATE_CAR
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CarDetailsScreen(navController: NavHostController, carId: String) {
    val carViewModel: CarViewModel = viewModel()
    val context = LocalContext.current
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    var showDeleteDialog by remember { mutableStateOf(false) }

    // Load the specific car when screen is launched or carId changes
    LaunchedEffect(carId) {
        carViewModel.loadCarById(carId)
    }

    val car = carViewModel.selectedCar
    val isLoading = carViewModel.isLoading

    // Delete Confirmation Dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Car Listing") },
            text = { Text("Are you sure you want to delete this car listing? This action cannot be undone.") },
            confirmButton = {
                Button(
                    onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            carViewModel.deleteCar(carId)
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.padding(end = 8.dp)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Car Details",
                        fontWeight = FontWeight.Bold,
                        color = Color.White
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
                    // Show edit/delete buttons only if user owns the car
                    if (car?.userId == currentUserId) {
                        IconButton(
                            onClick = {
                                navController.navigate("$ROUTE_UPDATE_CAR/$carId")
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit",
                                tint = Color.White
                            )
                        }
                        IconButton(
                            onClick = { showDeleteDialog = true }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color.White
                            )
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        if (isLoading && car == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2E7D32))
            }
        } else if (car == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Not found",
                        tint = Color.Gray,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Car not found",
                        fontSize = 18.sp,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = { navController.popBackStack() }
                    ) {
                        Text("Go Back")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF5F5F5))
            ) {
                // Car Image from Cloudinary
                val imageUrl = car.imageUrl ?: ""
                if (imageUrl.isNotEmpty()) {
                    AsyncImage(
                        model = imageUrl,
                        contentDescription = car.name ?: "Car image",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .clip(RoundedCornerShape(bottomStart = 16.dp, bottomEnd = 16.dp)),
                        contentScale = ContentScale.Crop,
                        placeholder = rememberAsyncImagePainter(model = "https://via.placeholder.com/400x300"),
                        error = rememberAsyncImagePainter(model = "https://via.placeholder.com/400x300")
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .background(Color.LightGray),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "No image",
                                tint = Color.Gray,
                                modifier = Modifier.size(48.dp)
                            )
                            Text(
                                text = "No image available",
                                color = Color.Gray,
                                modifier = Modifier.padding(top = 8.dp)
                            )
                        }
                    }
                }

                // Car Details
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .fillMaxWidth()
                ) {
                    // Car Name and Price
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = car.name ?: "Unnamed Car",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Text(
                            text = car.price ?: "Price not set",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF2E7D32)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // Category Badge
                    Card(
                        modifier = Modifier,
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors( // FIXED: Using proper CardDefaults import
                            containerColor = getCategoryColor(car.category ?: "Other")
                        )
                    ) {
                        Text(
                            text = car.category ?: "Other",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                            fontSize = 14.sp
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Description Section
                    Text(
                        text = "Description",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = car.details ?: "No description available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color.DarkGray,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // Seller Information Section
                    Text(
                        text = "Seller Information",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors( // FIXED: Using proper CardDefaults import
                            containerColor = Color.White
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Person,
                                    contentDescription = "Seller",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = car.sellerName ?: "Unknown Seller",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 12.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Call,
                                    contentDescription = "Phone",
                                    tint = Color(0xFF2E7D32),
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = car.sellerPhone ?: "Phone not provided",
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }

                            // Contact Button
                            Button(
                                onClick = {
                                    // Handle contact action
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text(
                                    text = "Contact Seller",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Additional Information
                    Text(
                        text = "Additional Details",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Listed by: ${car.sellerName ?: "Unknown seller"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                    Text(
                        text = "Category: ${car.category ?: "Not specified"}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }
}

// Helper function to get category color
fun getCategoryColor(category: String): Color {
    return when (category.lowercase()) {
        "sedan" -> Color(0xFF2196F3)
        "suv" -> Color(0xFFFF9800)
        "truck" -> Color(0xFFF44336)
        "luxury" -> Color(0xFF9C27B0)
        else -> Color(0xFF2E7D32)
    }
}
@Preview(showBackground = true)
@Composable
fun CarDetailsPreview() {
    CarDetailsScreen(
        navController = rememberNavController(),
        carId = "test-car-id"
    )
}