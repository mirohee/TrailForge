package com.example.trailforge.utils;


import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import com.example.trailforge.LoginActivity
import com.example.trailforge.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import kotlinx.coroutines.launch

object LogoutHelper {

// Function to handle user logout
@SuppressLint("SuspiciousIndentation")
fun logoutUser(context: Context) {
    val auth = SupabaseClientProvider.supabase.auth
                    (context as? androidx.activity.ComponentActivity)?.lifecycleScope?.launch {
        try {
            // Perform logout
            auth.signOut()
            // Show success message
            Toast.makeText(context, "Logout successful", Toast.LENGTH_SHORT).show()
            // Redirect to LoginActivity
            val intent = Intent(context, LoginActivity::class.java)
            context.startActivity(intent)
            context.finish() // Close current activity
        } catch (e: Exception) {
            // Handle any error that occurs during logout
            e.printStackTrace()
            Toast.makeText(context, "Logout failed: ${e.localizedMessage}", Toast.LENGTH_SHORT).show()
        }
    }
}
}

