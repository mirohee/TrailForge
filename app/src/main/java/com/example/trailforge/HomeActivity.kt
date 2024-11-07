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

class HomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TrailForgeTheme {
                // Your Home UI content goes here
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Here is your Profile Home Screen", modifier = Modifier.padding(bottom = 20.dp))
                    Text(text = "Welcome to Home Screen!", modifier = Modifier.padding(bottom = 20.dp))

                    // Button to go back to MainActivity
                    Button(onClick = {
                        val intent = Intent(this@HomeActivity, MainActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text("Go back to MainActivity")
                    }

                    // Button to go to AddRouteActivity
                    Button(onClick = {
                        val intent = Intent(this@HomeActivity, AddRouteActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text("Go to Add Route Activity")
                    }
                }
            }
        }
    }
}

