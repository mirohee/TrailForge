package com.example.trailforge

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.lifecycle.lifecycleScope
import com.example.trailforge.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

class SignupActivity : ComponentActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Make the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.value.toInt() // Use android

        // Initialize UI components
        usernameEditText = findViewById(R.id.editTextUsername)
        emailEditText = findViewById(R.id.editTextEmail)
        passwordEditText = findViewById(R.id.editTextPassword)
        signupButton = findViewById(R.id.buttonSignup)
        loginText = findViewById(R.id.loginText)

        // Handle signup button click
        signupButton.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (username.isNotEmpty() && email.isNotEmpty() && password.isNotEmpty()) {
                if (isValidEmail(email)) {
                    signupUser(username, email, password)
                } else {
                    Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }

        // Navigate to login screen
        loginText.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }

    // Function to validate email
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    // Function to handle the signup process
    private fun signupUser(username: String, email: String, password: String) {
        val auth = SupabaseClientProvider.supabase.auth

        lifecycleScope.launch {
            try {
                // Create JsonObject for user metadata
                val userMetadata = buildJsonObject {
                    put("username", username)
                }

                // Sign up with email, password, and metadata
                val user = auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                    this.data = userMetadata
                }

                // Fetch the logged-in user's details
                val session = auth.currentSessionOrNull()
                val loggedInUsername = session?.user?.userMetadata?.get("username")?.toString() ?: username

                Toast.makeText(this@SignupActivity, "Signup successful: ${user?.email}", Toast.LENGTH_SHORT).show()

                // Navigate to HomeActivity with logged-in username
                navigateToHome(loggedInUsername)
            } catch (e: Exception) {
                Log.e("SignupActivity", "Error signing up: ${e.message}")
                Toast.makeText(this@SignupActivity, "Signup failed: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Function to navigate to HomeActivity
    private fun navigateToHome(username: String) {
        val intent = Intent(this, HomeActivity::class.java).apply {
            putExtra("username", username) // Pass username as an extra
        }
        startActivity(intent)
        finish()
    }

}
