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

    init {
        loadCars()
    }

    // Load a specific car by ID
    fun loadCarById(carId: String) {
        _isLoading.value = true
        databaseReference.child(carId).addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val car = snapshot.getValue(CarData::class.java)
                CoroutineScope(Dispatchers.Main).launch {
                    _selectedCar.value = car
                    _isLoading.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
        })
    }

    // Clear selected car
    fun clearSelectedCar() {
        _selectedCar.value = null
    }

    // ... rest of your existing ViewModel methods (uploadCar, loadCars, etc.)
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
            val carId = databaseReference.push().key ?: System.currentTimeMillis().toString()
            val currentUser = auth.currentUser
            val userId = currentUser?.uid ?: ""

            // Upload image to Cloudinary if provided
            val imageUrl = if (imageUri != null) {
                uploadToCloudinary(context, imageUri)
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
            true
        } catch (e: Exception) {
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Fetch all cars from Firebase
    fun loadCars() {
        _isLoading.value = true
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val tempList = mutableListOf<CarData>()
                for (snap in snapshot.children) {
                    val car = snap.getValue(CarData::class.java)
                    if (car != null) {
                        tempList.add(car)
                    }
                }
                // Update on main thread to avoid Compose issues
                CoroutineScope(Dispatchers.Main).launch {
                    _cars.value = tempList
                    _isLoading.value = false
                }
            }

            override fun onCancelled(error: DatabaseError) {
                _isLoading.value = false
            }
        })
    }

    // Get cars by category
    fun getCarsByCategory(category: String): List<CarData> {
        return _cars.value.filter { it.category.equals(category, ignoreCase = true) }
    }

    // Get car by ID
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
            databaseReference.child(carId).removeValue().await()
            // Remove from local list as well
            _cars.value = _cars.value.filter { it.id != carId }
            true
        } catch (e: Exception) {
            false
        }
    }

    // Update a car with Cloudinary support
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
            val currentUser = auth.currentUser
            val userId = currentUser?.uid ?: ""

            // Upload new image only if provided
            val newImageUrl = if (imageUri != null) {
                uploadToCloudinary(context, imageUri)
            } else {
                // Keep existing image if no new image is provided
                _cars.value.find { it.id == carId }?.imageUrl ?: ""
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

            databaseReference.child(carId).setValue(carData).await()

            // Update local list
            val updatedList = _cars.value.map { if (it.id == carId) carData else it }
            _cars.value = updatedList

            true
        } catch (e: Exception) {
            false
        } finally {
            _isLoading.value = false
        }
    }

    // Cloudinary upload function
    private fun uploadToCloudinary(context: Context, uri: Uri): String {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val fileBytes = inputStream?.readBytes() ?: throw Exception("Image read failed")

        val requestBody = MultipartBody.Builder()
            .setType(MultipartBody.FORM)
            .addFormDataPart(
                "file",
                "image.jpg",
                RequestBody.create("image/*".toMediaTypeOrNull(), fileBytes)
            )
            .addFormDataPart("upload_preset", uploadPreset)
            .build()

        val request = Request.Builder()
            .url(cloudinaryUrl)
            .post(requestBody)
            .build()

        val response = OkHttpClient().newCall(request).execute()

        if (!response.isSuccessful) throw Exception("Upload failed: ${response.code}")

        val responseBody = response.body?.string()
        val secureUrl = Regex("\"secure_url\":\"(.*?)\"")
            .find(responseBody ?: "")?.groupValues?.get(1)

        return secureUrl ?: throw Exception("Failed to get image URL from Cloudinary response")
    }
}