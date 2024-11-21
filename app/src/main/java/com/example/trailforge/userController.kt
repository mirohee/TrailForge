package com.example.trailforge

class userController {
}

data class SignupRequest(
    val email: String,
    val password: String
)

// SignupResponse.kt
data class SignupResponse(
    val newUser: User,
    val token: String,
    val message: String
)

// User.kt
data class User(
    val id: String,
    val email: String
)