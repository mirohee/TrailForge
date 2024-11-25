package com.example.trailforge

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.trailforge.ui.theme.TrailForgeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrailForgeTheme {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Login Screen", modifier = Modifier.padding(bottom = 20.dp))

                    // Username input field (you can add other UI components here as needed)
                    Text(text = "Enter Username", modifier = Modifier.padding(bottom = 20.dp))

                    // Button for login functionality
                    Button(onClick = {
                        val intent = Intent(this@MainActivity, LoginActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text("Login")
                    }

                    // Button to go to SignupActivity
                    Button(onClick = {
                        val intent = Intent(this@MainActivity, SignupActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text("Go to Signup")
                    }

                    // Button to go to SignupActivity
                    Button(onClick = {
                        val intent = Intent(this@MainActivity, MapActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text("Go to Map")
                    }
                }
            }
        }
    }
}
