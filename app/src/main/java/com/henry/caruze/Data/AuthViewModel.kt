package com.henry.caruze.Data

import android.content.Context
import android.widget.Toast
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.henry.caruze.Models.User
import com.henry.caruze.Navigation.ROUTE_HOME
import com.henry.caruze.Navigation.ROUTE_LOGIN
import com.henry.caruze.Navigation.ROUTE_REGISTER

class AuthViewModel(var navController: NavHostController, var context: Context) {

    var mAuth: FirebaseAuth
    init {
        mAuth = FirebaseAuth.getInstance()

    }
    fun signup(fullname: String, email: String, pass: String, confirmpass: String) {
        if (email.isBlank() || pass.isBlank() || confirmpass.isBlank()) {
            Toast.makeText(
                context, "Please email and password cannot be blank", Toast.LENGTH_LONG
            ).show()
            return
        } else if (pass != confirmpass) {
            Toast.makeText(
                context, "Password do not match", Toast.LENGTH_LONG
            ).show()
            return
        } else {
            mAuth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener {
                    if (it.isSuccessful) {
                        val userId = mAuth.currentUser!!.uid
                        val userdata = User(
                            fullname = fullname,
                            email = email,
                            phone = "",
                            profileImage = "",
                            userId = userId
                        )
                        val regRef = FirebaseDatabase.getInstance().getReference()
                            .child("Users/$userId")
                        regRef.setValue(userdata).addOnCompleteListener { dbTask ->
                            if (dbTask.isSuccessful) {
                                Toast.makeText(
                                    context,
                                    "Registered Successfully",
                                    Toast.LENGTH_LONG
                                ).show()
                                navController.navigate(ROUTE_HOME)
                            } else {
                                Toast.makeText(
                                    context,
                                    "Database error: ${dbTask.exception?.message}",
                                    Toast.LENGTH_LONG
                                ).show()
                            }
                        }
                    } else {
                        Toast.makeText(
                            context,
                            "Authentication error: ${it.exception?.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
        }
    }
    fun login(email: String,pass: String){
        mAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener {
            if (it.isSuccessful ){
                Toast.makeText(this.context, "Successfully logged in",
                    Toast.LENGTH_SHORT).show()
                navController.navigate(ROUTE_HOME)
            }else{
                Toast.makeText(this.context, "Error logging in",
                    Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun logout(){
        mAuth.signOut()
        navController.navigate(ROUTE_LOGIN){
            popUpTo(0)}
    }

    fun isLoggedIn(): Boolean = mAuth.currentUser != null
}