package com.example.trailforge

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.activity.ComponentActivity
import com.example.trailforge.utils.LogoutHelper

class HomeActivity : ComponentActivity() {

    private lateinit var appNameText: TextView
    private lateinit var welcomeText: TextView
    private lateinit var logoutButton: Button
    private lateinit var addRouteButton: Button
    private lateinit var takePhotoButton: Button
    private lateinit var profileInfo: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        // Initialize UI components
        appNameText = findViewById(R.id.appName)
        welcomeText = findViewById(R.id.welcomeText)
        logoutButton = findViewById(R.id.logoutButton)
        addRouteButton = findViewById(R.id.addRouteButton)
        takePhotoButton = findViewById(R.id.takePhotoButton)

        // Retrieve the username passed from the login/signup process
        val username = intent.getStringExtra("username")?.replace("\"", "")?.trim() ?: "User"
        welcomeText.text = "Welcome, $username!"

        // Logout button click listener
        logoutButton.setOnClickListener {
            LogoutHelper.logoutUser(this)
        }

        // Add route button click listener
        addRouteButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, MapActivity::class.java)
            startActivity(intent)
        }

        // Take photo button click listener
        takePhotoButton.setOnClickListener {
            val intent = Intent(this@HomeActivity, TakePhotoActivity::class.java)
            startActivity(intent)
        }
    }
}
