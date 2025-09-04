package com.henry.caruze.ui.theme.Screens.Update

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.rememberAsyncImagePainter
import com.henry.caruze.Data.CarViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UpdateScreen(navController: NavHostController, carId: String?) {
    val carViewModel: CarViewModel = viewModel()
    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    // Form state variables
    var name by remember { mutableStateOf("") }
    var price by remember { mutableStateOf("") }
    var details by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var sellerName by remember { mutableStateOf("") }
    var sellerPhone by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var showCategoryMenu by remember { mutableStateOf(false) }
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showErrorDialog by remember { mutableStateOf(false) }
    var isLoading by remember { mutableStateOf(false) }

    // Categories for dropdown
    val categories = listOf("Sedan", "SUV", "Truck", "Luxury", "Other")

    // Image picker launcher
    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        selectedImageUri = uri
    }

    // Load car data when screen is launched
    LaunchedEffect(carId) {
        if (carId != null) {
            isLoading = true
            carViewModel.loadCarByIdWithCallback(carId)

            val car = carViewModel.selectedCar
            if (car != null) {
                name = car.name ?: ""
                price = car.price ?: ""
                details = car.details ?: ""
                category = car.category ?: ""
                sellerName = car.sellerName ?: ""
                sellerPhone = car.sellerPhone ?: ""
            }
            isLoading = false
        }
    }

    // Also observe changes to selectedCar in case it loads asynchronously
    LaunchedEffect(carViewModel.selectedCar) {
        val car = carViewModel.selectedCar
        if (car != null && carId == car.id) {
            name = car.name ?: ""
            price = car.price ?: ""
            details = car.details ?: ""
            category = car.category ?: ""
            sellerName = car.sellerName ?: ""
            sellerPhone = car.sellerPhone ?: ""
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Update Car Listing",
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
                }
            )
        }
    ) { innerPadding ->
        if (isLoading && carViewModel.selectedCar == null) {
            Box(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color(0xFF2E7D32))
            }
        } else {
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .background(Color(0xFFF5F5F5))
                    .padding(16.dp)
            ) {
                // Image Upload Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (selectedImageUri != null) {
                            Image(
                                painter = rememberAsyncImagePainter(model = selectedImageUri),
                                contentDescription = "Selected image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        } else {
                            val currentImageUrl = carViewModel.selectedCar?.imageUrl
                            if (!currentImageUrl.isNullOrEmpty()) {
                                Image(
                                    painter = rememberAsyncImagePainter(model = currentImageUrl),
                                    contentDescription = "Current car image",
                                    modifier = Modifier.fillMaxSize(),
                                    contentScale = ContentScale.Crop
                                )
                            } else {
                                Text(
                                    text = "Tap to add car image",
                                    color = Color.Gray,
                                    modifier = Modifier.padding(16.dp)
                                )
                            }
                        }

                        IconButton(
                            onClick = {
                                imagePicker.launch(
                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                                )
                            },
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp)
                                .background(Color(0xFF2E7D32), RoundedCornerShape(8.dp))
                                .size(40.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.ShoppingCart,
                                contentDescription = "Change image",
                                tint = Color.White
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Car Details Form
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Car Name *") },
                    leadingIcon = {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Car name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price *") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = showCategoryMenu,
                    onExpandedChange = { showCategoryMenu = it }
                ) {
                    OutlinedTextField(
                        value = category,
                        onValueChange = { category = it },
                        label = { Text("Category *") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showCategoryMenu)
                        },
                        readOnly = true,
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor()
                    )

                    ExposedDropdownMenu(
                        expanded = showCategoryMenu,
                        onDismissRequest = { showCategoryMenu = false }
                    ) {
                        categories.forEach { selectionOption ->
                            DropdownMenuItem(
                                text = { Text(selectionOption) },
                                onClick = {
                                    category = selectionOption
                                    showCategoryMenu = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = details,
                    onValueChange = { details = it },
                    label = { Text("Description") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp),
                    maxLines = 3
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Seller Information Section
                Text(
                    text = "Seller Information",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color(0xFF2E7D32),
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = sellerName,
                    onValueChange = { sellerName = it },
                    label = { Text("Seller Name *") },
                    leadingIcon = {
                        Icon(Icons.Default.Person, contentDescription = "Seller name")
                    },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = sellerPhone,
                    onValueChange = { sellerPhone = it },
                    label = { Text("Seller Phone *") },
                    leadingIcon = {
                        Icon(Icons.Default.Phone, contentDescription = "Seller phone")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Update Button
                Button(
                    onClick = {
                        if (validateForm(name, price, sellerName, sellerPhone)) {
                            isLoading = true
                            scope.launch {
                                val success = carViewModel.updateCar(
                                    context = context,
                                    carId = carId ?: "",
                                    name = name,
                                    price = price,
                                    details = details,
                                    category = category,
                                    sellerName = sellerName,
                                    sellerPhone = sellerPhone,
                                    imageUri = selectedImageUri
                                )

                                isLoading = false
                                if (success) {
                                    showSuccessDialog = true
                                } else {
                                    showErrorDialog = true
                                }
                            }
                        } else {
                            showErrorDialog = true
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Text(
                        text = "Update Car Listing",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Cancel Button
                Button(
                    onClick = { navController.popBackStack() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.LightGray
                    )
                ) {
                    Text(
                        text = "Cancel",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.DarkGray
                    )
                }
            }
        }
    }

    // Success Dialog
    if (showSuccessDialog) {
        AlertDialog(
            onDismissRequest = { showSuccessDialog = false },
            title = { Text("Success") },
            text = { Text("Car listing updated successfully!") },
            confirmButton = {
                Button(
                    onClick = {
                        showSuccessDialog = false
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }

    // Error Dialog
    if (showErrorDialog) {
        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text("Error") },
            text = {
                Text(
                    carViewModel.errorMessage ?: "Please fill in all required fields"
                )
            },
            confirmButton = {
                Button(
                    onClick = { showErrorDialog = false },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF2E7D32)
                    )
                ) {
                    Text("OK")
                }
            }
        )
    }
}

// Form validation function
private fun validateForm(
    name: String,
    price: String,
    sellerName: String,
    sellerPhone: String
): Boolean {
    return name.isNotBlank() &&
            price.isNotBlank() &&
            sellerName.isNotBlank() &&
            sellerPhone.isNotBlank()
}

@Preview(showBackground = true)
@Composable
fun UpdateScreenPreview() {
    UpdateScreen(navController = rememberNavController(), carId = "test-car-id")
}