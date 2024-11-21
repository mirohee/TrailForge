package com.example.trailforge

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Paint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnCreateRoute: Button

    private val routePoints = mutableListOf<GeoPoint>()
    private var isCreatingRoute = false
    private var currentPolyline: Polyline? = null

    private val helsinkiLocation = GeoPoint(60.1699, 24.9384)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        Configuration.getInstance().load(this, getSharedPreferences("osm_pref", MODE_PRIVATE))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true)

        btnCreateRoute = findViewById(R.id.btnCreateRoute)

        setupMap()
        setupButtons()
        requestLocationPermissions()
    }

    private fun setupMap() {
        mapView.controller.apply {
            setZoom(15.0)
            setCenter(helsinkiLocation)
        }

        // Add the events overlay first
        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                if (isCreatingRoute) {
                    addRoutePoint(p)
                    return true
                }
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean = false
        })

        // Clear existing overlays and add the events overlay
        mapView.overlays.clear()
        mapView.overlays.add(mapEventsOverlay)

        // Add initial marker
        addMarker(helsinkiLocation, "Default Location: Helsinki")
    }

    private fun setupButtons() {
        findViewById<Button>(R.id.btnAddMarker).setOnClickListener {
            val currentCenter = mapView.mapCenter as GeoPoint
            showAddMarkerDialog(currentCenter)
        }

        btnCreateRoute.setOnClickListener {
            toggleRouteCreation()
        }
    }

    private fun toggleRouteCreation() {
        isCreatingRoute = !isCreatingRoute
        if (isCreatingRoute) {
            startRouteCreation()
        } else {
            finishRouteCreation()
        }
    }

    private fun startRouteCreation() {
        routePoints.clear()
        btnCreateRoute.text = "Finish Route"
        currentPolyline = Polyline().apply {
            outlinePaint.apply {
                color = Color.RED
                strokeWidth = 10f
                strokeCap = Paint.Cap.ROUND
            }
        }.also {
            mapView.overlays.add(it)
        }
        Toast.makeText(this, "Tap on the map to add route points", Toast.LENGTH_SHORT).show()
    }

    private fun finishRouteCreation() {
        btnCreateRoute.text = "Create Route"
        if (routePoints.isEmpty()) {
            Toast.makeText(this, "No points added to route!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Route saved with ${routePoints.size} points!", Toast.LENGTH_SHORT).show()
            // Here you would save the route to your database
        }
    }

    private fun addRoutePoint(point: GeoPoint) {
        routePoints.add(point)

        // Add a point marker
        val pointMarker = Marker(mapView).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = resources.getDrawable(org.osmdroid.library.R.drawable.marker_default, theme)
            title = "Point ${routePoints.size}"
        }
        mapView.overlays.add(pointMarker)

        // Update polyline
        currentPolyline?.setPoints(routePoints)
        mapView.invalidate()
    }

    private fun addMarker(location: GeoPoint, title: String) {
        val marker = Marker(mapView).apply {
            position = location
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
            this.title = title
        }
        mapView.overlays.add(marker)
        mapView.invalidate()
    }

    private fun showAddMarkerDialog(location: GeoPoint) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Enter Marker Name")

        val input = EditText(this).apply {
            hint = "Enter marker name"
        }
        builder.setView(input)

        builder.setPositiveButton("Add") { dialog, _ ->
            val markerName = input.text.toString().trim()
            if (markerName.isNotEmpty()) {
                addMarker(location, markerName)
            } else {
                Toast.makeText(this, "Please enter a name for the marker", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }

        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun requestLocationPermissions() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                1
            )
        } else {
            enableUserLocation()
        }
    }

    private fun enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                location?.let {
                    val userLocation = GeoPoint(it.latitude, it.longitude)
                    mapView.controller.setCenter(userLocation)
                    addMarker(userLocation, "You are here")
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 1 && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation()
        }
    }

    override fun onResume() {
        super.onResume()
        mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }
}