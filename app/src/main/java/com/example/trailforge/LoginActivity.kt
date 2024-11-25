package com.example.trailforge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.trailforge.SignupActivity
import com.example.trailforge.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import kotlinx.coroutines.launch

class LoginActivity : ComponentActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        emailEditText = findViewById(R.id.emailEditText)
        passwordEditText = findViewById(R.id.passwordEditText)
        loginButton = findViewById(R.id.loginButton)
        signupText = findViewById(R.id.signupText)

        // Login button action
        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                loginUser(email, password)
            } else {
                Toast.makeText(this, "Please enter both email and password", Toast.LENGTH_SHORT).show()
            }
        }

        // Redirect to signup activity
        signupText.setOnClickListener {
            val intent = Intent(this@LoginActivity, SignupActivity::class.java)
            startActivity(intent)

        }



    }

    // Function to handle login with Supabase
    private fun loginUser(email: String, password: String) {
        val auth = SupabaseClientProvider.supabase.auth

        lifecycleScope.launch {
            try {
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Show success message
                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                // Navigate to HomeActivity
                navigateToHome()
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Login failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to navigate to HomeActivity
    private fun navigateToHome() {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java)
        startActivity(intent)
        finish() // Close LoginActivity so user cannot return with back button
    }
}
