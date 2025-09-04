package com.henry.caruze.Models

data class User(
    val fullname: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImage: String = "",
    val userId: String = "",
    val role: String = "Seller" // Default role is Seller
) {
    // Empty constructor for Firebase
    constructor() : this("", "", "", "", "")
}