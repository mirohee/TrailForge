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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.view.WindowCompat
import com.example.trailforge.ui.theme.TrailForgeTheme


class AddRouteActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Make the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.value.toInt() // Use android
        setContent {
            TrailForgeTheme {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Plan your next route!", modifier = Modifier.padding(bottom = 20.dp))

                    Text(text = "Welcome to the AddRoute Page!", modifier = Modifier.padding(bottom = 20.dp))

                    // Button to go back to MainActivity
                    Button(onClick = {
                        val intent = Intent(this@AddRouteActivity, MainActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text("Back to MainActivity")
                    }

                    // Button to go to HomeActivity
                    Button(onClick = {
                        val intent = Intent(this@AddRouteActivity, HomeActivity::class.java)
                        startActivity(intent)
                    }) {
                        Text("Go to Home")
                    }
                }

            }
        }
    }
}