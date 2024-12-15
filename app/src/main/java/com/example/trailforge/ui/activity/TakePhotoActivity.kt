package com.example.trailforge.ui.activity

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import android.Manifest
import android.util.Log
import com.example.trailforge.data.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.storage.storage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import androidx.core.view.WindowCompat
import com.example.trailforge.R

// Class for taking a photo and uploading it to Supabase

class TakePhotoActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private var vFilename: String = ""

    // Define ActivityResultLauncher for camera intent
    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                try {
                    val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), vFilename)

                    // Display photo in ImageView
                    val uri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
                    val myImageView: ImageView = findViewById(R.id.myImageView)
                    myImageView.setImageURI(uri)

                    // Upload the photo to Supabase
                    uploadPhotoToSupabase(file)

                } catch (e: Exception) {
                    Log.e("TakePhotoActivity", "Error while handling photo: ${e.message}")
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)

        // Make the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = androidx.compose.ui.graphics.Color.Transparent.value.toInt() // Use android

        val btnTakePhoto: Button = findViewById(R.id.btn_takephoto)
        val btnViewPhotos: Button = findViewById(R.id.btn_view_photos)

        // Set up the button to take a photo
        btnTakePhoto.setOnClickListener {
            if (checkPermissions()) {
                openCamera()
            } else {
                requestPermissions()
            }
        }

        // Set up the button to navigate to ViewPhotoActivity
        btnViewPhotos.setOnClickListener {
            val intent = Intent(this, ViewPhotoActivity::class.java)
            startActivity(intent)
        }
    }

    // Check if all necessary permissions are granted
    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                readPermission == PackageManager.PERMISSION_GRANTED &&
                writePermission == PackageManager.PERMISSION_GRANTED
    }

    // Request permissions if they are not granted
    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_CODE
        )
    }

    // Open the camera and save the photo to a file
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        vFilename = "FOTO_$timeStamp.jpg"

        try {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), vFilename)
            val imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", file)
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            takePictureLauncher.launch(cameraIntent)
        } catch (e: Exception) {
            Log.e("TakePhotoActivity", "Error while opening camera: ${e.message}")
        }
    }

    // Handle the result of the permission request
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openCamera()
                } else {
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    // Upload the photo to Supabase
    private fun uploadPhotoToSupabase(file: File) {
        val storage = SupabaseClientProvider.supabase.storage
        val bucket = storage["photos"]

        val auth = SupabaseClientProvider.supabase.auth
        val userId = auth.currentSessionOrNull()?.user?.id

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        val filePath = "uploads/$userId/${file.name}"

        // Use a coroutine to upload the photo in the background
        CoroutineScope(Dispatchers.IO).launch {
            try {
                bucket.upload(filePath, file.readBytes())
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TakePhotoActivity, "Photo uploaded successfully!", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Log.e("TakePhotoActivity", "Error uploading photo: ${e.message}")
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@TakePhotoActivity, "Upload failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}
