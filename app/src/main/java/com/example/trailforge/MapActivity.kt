package com.example.trailforge

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // Default coordinates for Helsinki
    private val helsinkiLocation = GeoPoint(60.1699, 24.9384)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        // Initialize osmdroid configuration
        Configuration.getInstance().load(this, getSharedPreferences("osm_pref", MODE_PRIVATE))

        // Initialize FusedLocationProviderClient
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // Reference to MapView
        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true) // Enable pinch-to-zoom

        // Set initial map location to Helsinki
        mapView.controller.setZoom(15.0)
        mapView.controller.setCenter(helsinkiLocation) // Start in Helsinki

        // Add a marker at Helsinki initially
        addMarker(helsinkiLocation, "Default Location: Helsinki")

        // Check permissions and request if not granted
        requestLocationPermissions()
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request permissions if not granted
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), 1)
        } else {
            enableUserLocation()
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation()
        }
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    val userLocation = GeoPoint(location.latitude, location.longitude)

                    // Move the map to the user's location
                    mapView.controller.setCenter(userLocation)

                    // Clear any existing markers to avoid multiple pins
                    mapView.overlays.clear()

                    // Add a marker to indicate user's location
                    val userMarker = Marker(mapView)
                    userMarker.position = userLocation
                    userMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                    userMarker.title = "You are here"
                    mapView.overlays.add(userMarker)

                    // Refresh the map
                    mapView.invalidate()
                }
            }
        }
    }

    private fun addMarker(location: GeoPoint, title: String) {
        val marker = Marker(mapView)
        marker.position = location
        marker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
        marker.title = title
        mapView.overlays.add(marker)
        mapView.invalidate() // Refresh the map
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume() // Needed for osmdroid
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause() // Needed for osmdroid
    }
}
