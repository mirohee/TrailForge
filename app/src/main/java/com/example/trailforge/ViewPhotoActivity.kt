package com.example.trailforge

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.trailforge.data.SupabaseClientProvider
import com.example.trailforge.utils.PhotoAdapter
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

// Class for viewing photos uploaded to Supabase

class ViewPhotoActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var photoAdapter: PhotoAdapter
    private val photoUris: MutableList<Uri> = mutableListOf()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_photo)

        // Make the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.value.toInt() // Use android

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Fetch uploaded photos from Supabase storage
        fetchUploadedPhotos()

        // Set up the adapter with the list of photo URIs
        photoAdapter = PhotoAdapter(photoUris)
        recyclerView.adapter = photoAdapter
    }

    private fun fetchUploadedPhotos() {
        val storage = SupabaseClientProvider.supabase.storage
        val bucket = storage["photos"]
        val auth = SupabaseClientProvider.supabase.auth
        val userId = auth.currentSessionOrNull()?.user?.id

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val filePath = "uploads/$userId/"

        val baseUrl = "${BuildConfig.SUPABASE_URL}/storage/v1/object/public/photos/"

        CoroutineScope(Dispatchers.IO).launch {
            try {
                // List the files in the "uploads/userId/" directory
                val photos = bucket.list(filePath)

                // Check if we found photos in this directory
                if (photos.isEmpty()) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@ViewPhotoActivity, "No photos found for this user", Toast.LENGTH_SHORT).show()
                    }
                    return@launch
                }

                // Map the list of photos to their public URLs
                val photoUrisList = photos.mapNotNull { photo ->
                    // Construct the public URL using the photo.name (file name) and userId
                    val publicUrl = "$baseUrl${
                        "uploads/$userId/${photo.name}" 
                    }"
                    Log.d("ViewPhotoActivity", "Generated URL: $publicUrl")
                    Uri.parse(publicUrl) // Convert the URL to a Uri
                }

                // Update the UI with the fetched photo URIs
                withContext(Dispatchers.Main) {
                    photoUris.clear()
                    photoUris.addAll(photoUrisList)
                    photoAdapter.notifyDataSetChanged()
                }
                Log.d("ViewPhotoActivity", "Photos found: ${photos.size}")
                photos.forEach { photo ->
                    Log.d("ViewPhotoActivity", "Photo: ${photo.name}")
                }

            } catch (e: Exception) {
                Log.e("ViewPhotoActivity", "Error fetching photos: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@ViewPhotoActivity, "Error fetching photos", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
