package com.henry.caruze.Data

import CarData
import android.content.Context
import android.net.Uri
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody
import java.io.InputStream

class CarViewModel : ViewModel() {

    private val databaseReference = FirebaseDatabase.getInstance().getReference("Cars")
    private val auth = FirebaseAuth.getInstance()

    // Cloudinary configuration
    private val cloudinaryUrl = "https://api.cloudinary.com/v1_1/dzq7yq5ml/image/upload"
    private val uploadPreset = "Caruze"

    // State for all cars
    private val _cars = mutableStateOf<List<CarData>>(emptyList())
    val cars: List<CarData> get() = _cars.value

    // State for selected car details
    private val _selectedCar = mutableStateOf<CarData?>(null)
    val selectedCar: CarData? get() = _selectedCar.value

    private val _isLoading = mutableStateOf(false)
    val isLoading: Boolean get() = _isLoading.value

    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: String? get() = _errorMessage.value

    init {
        loadCars()
    }

    // Clear error message
    fun clearError() {
        _errorMessage.value = null
    }

    // Load a specific car by ID - SUSPEND VERSION for UpdateScreen
    suspend fun loadCarById(carId: String): CarData? {
        return try {
            _isLoading.value = true
            _errorMessage.value = null

            // Use await() to make this a suspend function
            val snapshot = databaseReference.child(carId).get().await()

            if (snapshot.exists()) {
                val car = snapshot.getValue(CarData::class.java)
                _selectedCar.value = car
                car
            } else {
                _errorMessage.value = "Car not found"
                _selectedCar.value = null
                null
            }
        } catch (e: Exception) {
            _errorMessage.value = "Failed to load car: ${e.message}"
            null
        } finally {
            _isLoading.value = false
        }
    }

    // Load car by ID with callback (for compatibility)
    fun loadCarByIdWithCallback(carId: String) {
        _isLoading.value = true
        _errorMessage.value = null

        databaseReference.child(carId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val car = snapshot.getValue(CarData::class.java)
                    CoroutineScope(Dispatchers.Main).launch {
                        _selectedCar.value = car
                        _isLoading.value = false
                    }
                } else {
                    CoroutineScope(Dispatchers.Main).launch {
                        _errorMessage.value = "Car not found"
                        _isLoading.value = false
                        _selectedCar.value = null
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                CoroutineScope(Dispatchers.Main).launch {
                    _errorMessage.value = "Failed to load car: ${error.message}"
                    _isLoading.value = false
                }
            }
        })
    }

    // Clear selected car
    fun clearSelectedCar() {
        _selectedCar.value = null
    }

    // Upload a new car
    suspend fun uploadCar(
        context: Context,
        imageUri: Uri?,
        name: String,
        price: String,
        details: String,
        category: String,
        sellerName: String,
        sellerPhone: String
    ): Boolean {
        return try {
            _isLoading.value = true
            _errorMessage.value = null

            val carId = databaseReference.push().key ?: System.currentTimeMillis().toString()
            val currentUser = auth.currentUser
            val userId = currentUser?.uid ?: ""

            // Validate required fields
            if (name.isBlank() || price.isBlank() || sellerName.isBlank()) {
                _errorMessage.value = "Please fill in all required fields"
                return false
            }

            // Upload image to Cloudinary if provided
            val imageUrl = if (imageUri != null) {
                try {
                    uploadToCloudinary(context, imageUri)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to upload image: ${e.message}"
                    ""
                }
            } else {
                ""
            }

            val carData = CarData(
                id = carId,
                name = name,
                price = price,
                details = details,
                category = category,
                sellerName = sellerName,
                sellerPhone = sellerPhone,
                imageUrl = imageUrl,
                userId = userId
            )

            databaseReference.child(carId).setValue(carData).await()

            // Refresh the cars list to include the new car
            loadCars()

            true
        } catch (e: Exception) {
            _errorMessage.value = "Failed to upload car: ${e.message}"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Fetch all cars from Firebase
    fun loadCars() {
        _isLoading.value = true
        _errorMessage.value = null

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<CarData>()
                for (snap in snapshot.children) {
                    val car = snap.getValue(CarData::class.java)
                    if (car != null) {
                        tempList.add(car)
                    }
                }
                // Sort by newest first (assuming ID is timestamp-based)
                val sortedList = tempList.sortedByDescending { it.id }

                CoroutineScope(Dispatchers.Main).launch {
                    _cars.value = sortedList
                    _isLoading.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                CoroutineScope(Dispatchers.Main).launch {
                    _errorMessage.value = "Failed to load cars: ${error.message}"
                    _isLoading.value = false
                }
            }
        })
    }

    // Get cars by category
    fun getCarsByCategory(category: String): List<CarData> {
        return _cars.value.filter {
            it.category?.equals(category, ignoreCase = true) ?: false
        }
    }

    // Get car by ID from local cache
    fun getCarById(carId: String): CarData? {
        return _cars.value.find { it.id == carId }
    }

    // Get cars by user ID
    fun getCarsByUserId(userId: String): List<CarData> {
        return _cars.value.filter { it.userId == userId }
    }

    // Delete a car
    suspend fun deleteCar(carId: String): Boolean {
        return try {
            _isLoading.value = true
            _errorMessage.value = null

            databaseReference.child(carId).removeValue().await()

            // Remove from local list as well
            _cars.value = _cars.value.filter { it.id != carId }

            true
        } catch (e: Exception) {
            _errorMessage.value = "Failed to delete car: ${e.message}"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Update a car with Cloudinary support - IMPROVED VERSION
    suspend fun updateCar(
        context: Context,
        carId: String,
        name: String,
        price: String,
        details: String,
        category: String,
        sellerName: String,
        sellerPhone: String,
        imageUri: Uri?
    ): Boolean {
        return try {
            _isLoading.value = true
            _errorMessage.value = null

            val currentUser = auth.currentUser
            val userId = currentUser?.uid ?: ""

            // Validate required fields
            if (name.isBlank() || price.isBlank() || sellerName.isBlank()) {
                _errorMessage.value = "Please fill in all required fields"
                return false
            }

            // Get existing car data from Firebase to ensure we have the latest
            val existingCarSnapshot = databaseReference.child(carId).get().await()
            val existingCar = existingCarSnapshot.getValue(CarData::class.java)

            if (existingCar == null) {
                _errorMessage.value = "Car not found"
                return false
            }

            // Upload new image only if provided, otherwise keep existing
            val newImageUrl = if (imageUri != null) {
                try {
                    uploadToCloudinary(context, imageUri)
                } catch (e: Exception) {
                    _errorMessage.value = "Failed to upload image: ${e.message}"
                    existingCar.imageUrl ?: ""
                }
            } else {
                existingCar.imageUrl ?: ""
            }

            val carData = CarData(
                id = carId,
                name = name,
                price = price,
                details = details,
                category = category,
                sellerName = sellerName,
                sellerPhone = sellerPhone,
                imageUrl = newImageUrl,
                userId = userId
            )

            // Update in Firebase
            databaseReference.child(carId).setValue(carData).await()

            // Update local lists
            val updatedList = _cars.value.map { if (it.id == carId) carData else it }
            _cars.value = updatedList

            // Update selected car if it's the one being edited
            if (_selectedCar.value?.id == carId) {
                _selectedCar.value = carData
            }

            true
        } catch (e: Exception) {
            _errorMessage.value = "Failed to update car: ${e.message}"
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Search cars by name, category, or details
    fun searchCars(query: String): List<CarData> {
        if (query.isBlank()) return _cars.value

        val lowercaseQuery = query.lowercase()

        return _cars.value.filter { car ->
            car.name?.lowercase()?.contains(lowercaseQuery) == true ||
                    car.category?.lowercase()?.contains(lowercaseQuery) == true ||
                    car.details?.lowercase()?.contains(lowercaseQuery) == true ||
                    car.sellerName?.lowercase()?.contains(lowercaseQuery) == true ||
                    car.price?.lowercase()?.contains(lowercaseQuery) == true
        }
    }

    // Get featured cars (first 5 newest cars)
    fun getFeaturedCars(): List<CarData> {
        return _cars.value.take(5)
    }

    // Cloudinary upload function
    private suspend fun uploadToCloudinary(context: Context, uri: Uri): String {
        return try {
            val contentResolver = context.contentResolver
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            val fileBytes = inputStream?.readBytes() ?: throw Exception("Failed to read image file")

            val requestBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    "file",
                    "car_image.jpg",
                    RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
                )
                .addFormDataPart("upload_preset", uploadPreset)
                .build()

            val request = Request.Builder()
                .url(cloudinaryUrl)
                .post(requestBody)
                .build()

            val response = OkHttpClient().newCall(request).execute()

            if (!response.isSuccessful) {
                throw Exception("Upload failed: ${response.code} - ${response.message}")
            }

            val responseBody = response.body?.string()
            val secureUrl = Regex("\"secure_url\":\"(.*?)\"")
                .find(responseBody ?: "")?.groupValues?.get(1)

            secureUrl ?: throw Exception("Failed to get image URL from Cloudinary response")
        } catch (e: Exception) {
            throw Exception("Cloudinary upload error: ${e.message}")
        }
    }

    // Refresh cars data
    fun refreshCars() {
        loadCars()
    }

    // Clear all data (useful for logout)
    fun clearAllData() {
        _cars.value = emptyList()
        _selectedCar.value = null
        _errorMessage.value = null
    }
}