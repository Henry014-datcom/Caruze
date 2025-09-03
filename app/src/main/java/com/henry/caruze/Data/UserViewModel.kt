package com.henry.caruze.Data
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.henry.caruze.Models.User

class UserViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val databaseReference = FirebaseDatabase.getInstance().getReference("Users")

    val currentUser = mutableStateOf<User?>(null)
    val isLoading = mutableStateOf(false)
    val errorMessage = mutableStateOf<String?>(null)

    fun fetchCurrentUser() {
        val firebaseUser = auth.currentUser
        if (firebaseUser == null) {
            errorMessage.value = "No user logged in"
            return
        }

        isLoading.value = true
        databaseReference.child(firebaseUser.uid).addListenerForSingleValueEvent(
            object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    isLoading.value = false
                    if (snapshot.exists()) {
                        currentUser.value = snapshot.getValue(User::class.java)
                    } else {
                        // Create a basic user profile if it doesn't exist
                        createDefaultUserProfile(firebaseUser.uid, firebaseUser.email ?: "")
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    isLoading.value = false
                    errorMessage.value = "Failed to fetch user: ${error.message}"
                }
            }
        )
    }

    private fun createDefaultUserProfile(userId: String, email: String) {
        val newUser = User(
            fullname = "User",
            email = email,
            phone = "",
            profileImage = "",
            userId = userId
        )

        databaseReference.child(userId).setValue(newUser)
            .addOnSuccessListener {
                currentUser.value = newUser
            }
            .addOnFailureListener {
                errorMessage.value = "Failed to create user profile: ${it.message}"
            }
    }

    fun updateUserProfile(updatedUser: User) {
        val userId = auth.currentUser?.uid ?: return

        databaseReference.child(userId).setValue(updatedUser)
            .addOnSuccessListener {
                currentUser.value = updatedUser
            }
            .addOnFailureListener {
                errorMessage.value = "Failed to update profile: ${it.message}"
            }
    }

    fun clearError() {
        errorMessage.value = null
    }
}