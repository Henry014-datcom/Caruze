package com.henry.caruze.Models

data class User(
    val fullname: String = "",
    val email: String = "",
    val phone: String = "",
    val profileImage: String = "",
    val userId: String = ""
) {
    // Empty constructor for Firebase
    constructor() : this("", "", "", "", "")
}