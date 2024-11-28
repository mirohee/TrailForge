package com.example.trailforge

import android.content.ContentValues
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


class TakePhotoActivity : AppCompatActivity() {

    private val PERMISSION_CODE = 1000
    private var vFilename: String = ""

    // Define ActivityResultLauncher for camera intent
    private val takePictureLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            Log.e("TakePhoto", "Error during takephoto0")
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
            Log.e("TakePhoto", "Error during takephoto1")
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_take_photo)

        val btnTakePhoto: Button = findViewById(R.id.btn_takephoto)
        val myImageView: ImageView = findViewById(R.id.myImageView)

        btnTakePhoto.setOnClickListener {
            Log.e("TakePhoto", "Button clicked, checking permissions")

            // Check permissions first
            if (checkPermissions()) {
                openCamera()
            } else {
                requestPermissions()
            }
        }
    }

    private fun checkPermissions(): Boolean {
        val cameraPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        val readPermission = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
        val writePermission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)

        Log.e("TakePhoto", "Camera permission: $cameraPermission, Read permission: $readPermission, Write permission: $writePermission")

        return cameraPermission == PackageManager.PERMISSION_GRANTED &&
                readPermission == PackageManager.PERMISSION_GRANTED &&
                writePermission == PackageManager.PERMISSION_GRANTED
    }

    private fun requestPermissions() {
        Log.e("TakePhoto", "Requesting permissions")
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISSION_CODE
        )
    }

    private fun openCamera() {
        Log.e("TakePhoto", "Opening camera")

        val values = ContentValues().apply {
            put(MediaStore.Images.Media.TITLE, "New Picture")
            put(MediaStore.Images.Media.DESCRIPTION, "From the Camera")
        }

        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        // Generate a timestamp-based filename for the photo
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        vFilename = "FOTO_$timeStamp.jpg"

        try {
            val file = File(getExternalFilesDir(Environment.DIRECTORY_PICTURES), vFilename)
            val imageUri = FileProvider.getUriForFile(this, "${packageName}.provider", file)

            Log.e("TakePhoto", "File URI: $imageUri")
            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri)

            // Launch the camera intent using ActivityResultLauncher
            takePictureLauncher.launch(cameraIntent)
        } catch (e: Exception) {
            Log.e("TakePhoto", "Error while opening camera: ${e.message}")
        }
    }

    // Handle permissions request result
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
    private fun uploadPhotoToSupabase(file: File) {
        val storage = SupabaseClientProvider.supabase.storage
        val bucket = storage["photos"]

        // Fetch the current session to get the user UID
        val auth = SupabaseClientProvider.supabase.auth
        val userId = auth.currentSessionOrNull()?.user?.id

        if (userId == null) {
            Toast.makeText(this, "User not authenticated", Toast.LENGTH_SHORT).show()
            return
        }

        // Define the file path under the user's folder
        val filePath = "uploads/$userId/${file.name}"

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
