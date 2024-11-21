package com.example.trailforge

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.Call
import retrofit2.Response

interface ApiService {

    @POST("signup") // Update the endpoint based on your server configuration
    suspend fun signup(@Body request: SignupRequest): Response<SignupResponse>


    @POST("login") // Assuming "login" is the endpoint for login
    suspend fun login(@Body request: SignupRequest): SignupResponse
}
