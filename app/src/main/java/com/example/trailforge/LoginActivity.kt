package com.example.trailforge

import android.content.Intent
import android.os.Bundle
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

// LoginActivity for users to log in to the app

class LoginActivity : ComponentActivity() {

    private lateinit var emailEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loginButton: Button
    private lateinit var signupText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Making the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.value.toInt()

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
                // Perform the login
                auth.signInWith(Email) {
                    this.email = email
                    this.password = password
                }

                // Retrieve user data after login
                val session = auth.currentSessionOrNull()
                val username = session?.user?.userMetadata?.get("username")?.toString() ?: "User"

                // Show success message
                Toast.makeText(this@LoginActivity, "Login successful", Toast.LENGTH_SHORT).show()

                // Navigate to HomeActivity with the username
                navigateToHome(username)
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@LoginActivity, "Login failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
            }
        }
    }


    // Function to navigate to HomeActivity
    private fun navigateToHome(username: String) {
        val intent = Intent(this@LoginActivity, HomeActivity::class.java).apply {
            putExtra("username", username) // Pass the username to HomeActivity
        }
        startActivity(intent)
        finish() // Close LoginActivity so the user cannot return to it
    }

}
