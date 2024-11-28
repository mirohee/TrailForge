package com.example.trailforge
/*
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.core.view.isVisible
import com.bumptech.glide.Glide
import com.example.trailforge.data.SupabaseClientProvider
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class ViewPhotosActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_photos)

        val tvNoPhotos: TextView = findViewById(R.id.tv_no_photos)

        // Get user ID from intent
        val userId = intent.getStringExtra("user_id")

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Fetch photos from Supabase
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val storage = SupabaseClientProvider.supabase.storage
                val bucket = storage["photos"]
                val photos = bucket.list("uploads/$userId") // Get photos for the user

                // Create the transformed URLs for each photo
                val photoUrls = photos.mapNotNull { fileObject ->
                    val imageUrl = "https://your-project-id.supabase.co/storage/v1/object/public/photos/${fileObject.name}"
                    // Add transformation parameters (resize to 500x600)
                    "$imageUrl?width=500&height=600"
                }

                withContext(Dispatchers.Main) {
                    if (photoUrls.isEmpty()) {
                        tvNoPhotos.visibility = View.VISIBLE
                    } else {
                        tvNoPhotos.visibility = View.GONE
                        displayPhotos(photoUrls)
                    }
                }
            } catch (e: Exception) {
                Log.e("ViewPhotosActivity", "Error fetching photos: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewPhotosActivity, "Error fetching photos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun displayPhotos(photoUrls: List<String>) {
        // Dynamically create ImageView elements to display photos
        val photoContainer: ViewGroup = findViewById(R.id.photo_container) // Your layout should have a ViewGroup for the photos
        photoContainer.removeAllViews() // Clear any existing views

        for (photoUrl in photoUrls) {
            val imageView = ImageView(this)
            imageView.layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Glide to load the image into the ImageView
            Glide.with(this)
                .load(photoUrl) // Use the URL directly
                .placeholder(R.drawable.ic_placeholder) // Optional placeholder
                .into(imageView)

            // Add the ImageView to the layout
            photoContainer.addView(imageView)
        }
    }
}*/
