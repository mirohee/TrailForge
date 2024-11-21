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
import com.example.trailforge.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class SignupActivity : ComponentActivity() {

    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var signupButton: Button
    private lateinit var loginText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize UI components
        usernameEditText = findViewById(R.id.editTextUsername)
        passwordEditText = findViewById(R.id.editTextPassword)
        signupButton = findViewById(R.id.buttonSignup)
        loginText = findViewById(R.id.loginText)

        // Handle signup button click
        signupButton.setOnClickListener {
            val email = usernameEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                if (isValidEmail(email)) {
                    signupUser(email, password)
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
    private fun signupUser(email: String, password: String) {
        val auth = SupabaseClientProvider.supabase.auth // Access auth plugin correctly

        lifecycleScope.launch {
            try {
                // Call signup with email and password
                val user = auth.signUpWith(Email) {
                    this.email = email
                    this.password = password
                }
                Toast.makeText(this@SignupActivity, "Signup successful: ${user?.email}", Toast.LENGTH_SHORT).show()
                navigateToHome()
            } catch (e: Exception) {
                Log.e("SignupActivity", "Error signing up: ${e.message}")
                Toast.makeText(this@SignupActivity, "Signup failed: ${e.localizedMessage ?: "Unknown error"}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to navigate to HomeActivity
    private fun navigateToHome() {
        val intent = Intent(this, HomeActivity::class.java)
        startActivity(intent)
        finish()
    }
}
