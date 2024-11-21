package com.example.trailforge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch

class SignupActivity : ComponentActivity() {

    private lateinit var database: AppDatabase
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var LoginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize the Room database
        //database = AppDatabase.getDatabase(this)
        Log.e("SignupActivity", "Error during signup3")
        // Initialize UI components
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        signupButton = findViewById(R.id.buttonSignup)
        LoginText = findViewById(R.id.loginText)

        // Handle signup button click
        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && password.isNotEmpty()) {
                signupUser(username, password)
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        LoginText.setOnClickListener {
            val intent = Intent(this@SignupActivity, LoginActivity::class.java)
            startActivity(intent)

        }
    }

    // Function to handle the signup process
    private fun signupUser(email: String, password: String) {
        val signupRequest = SignupRequest(email, password)

        lifecycleScope.launch {
            try {
                val response = RetrofitInstance.api.signup(signupRequest) // API call for signup
                if (response.isSuccessful) {
                    Toast.makeText(this@SignupActivity, response.body()?.message ?: "Signup successful", Toast.LENGTH_SHORT).show()
                    // Navigate to HomeActivity on successful signup
                    navigateToHome()
                } else {
                    val errorMessage = response.errorBody()?.string() ?: "Signup failed"
                    Toast.makeText(this@SignupActivity, errorMessage, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@SignupActivity, "Signup failed: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to navigate to HomeActivity
    private fun navigateToHome() {
        // Start HomeActivity after successful signup
        val intent = Intent(this@SignupActivity, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
