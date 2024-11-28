package com.example.trailforge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.trailforge.utils.LogoutHelper  // Import the LogoutHelper class

class HomeActivity : ComponentActivity() {

    private lateinit var welcomeText: TextView
    private lateinit var logoutButton: Button
    private lateinit var addRouteButton: Button
    private lateinit var takePhotoButton: Button
    private lateinit var profileInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize UI components
        welcomeText = findViewById(R.id.welcomeText)
        logoutButton = findViewById(R.id.logoutButton)
        takePhotoButton = findViewById(R.id.takePhotoButton)
        addRouteButton = findViewById(R.id.addRouteButton)
        profileInfo = findViewById(R.id.profileInfo)

        // Retrieve the username passed from the login/signup process
        val username = intent.getStringExtra("username") ?: "User"
        welcomeText.text = "Welcome, $username!"

        // Logout button click listener
        logoutButton.setOnClickListener {
            LogoutHelper.logoutUser(this)
        }

        // Set up the add route button click listener
        addRouteButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, AddRouteActivity::class.java)
            startActivity(intent)
        }

        takePhotoButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, TakePhotoActivity::class.java)
            startActivity(intent)
        }
    }
}
