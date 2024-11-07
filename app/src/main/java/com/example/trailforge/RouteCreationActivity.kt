package com.example.trailforge

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.libraries.mapsplatform.transportation.consumer.model.Route


class RouteCreationActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var mapView: MapView
    private lateinit var nameEditText: EditText
    private lateinit var terrainSpinner: Spinner
    private lateinit var difficultySpinner: Spinner
    private lateinit var timeEditText: EditText
    private lateinit var saveButton: Button

    private lateinit var googleMap: GoogleMap
    private val routePoints = mutableListOf<LatLng>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_route_creation)

//Initialisation
        mapView = findViewById(R.id.map_view)
        nameEditText = findViewById(R.id.route_name)
        terrainSpinner = findViewById(R.id.terrain_spinner)
        difficultySpinner = findViewById(R.id.difficulty_spinner)
        timeEditText = findViewById(R.id.estimated_time)
        saveButton = findViewById(R.id.save_button)

        // Map setup (Google Maps API avain pitää muistaa)
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        // button click listener
        saveButton.setOnClickListener {
            saveRoute()
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map

        // Set up map interactions for route creation
        googleMap.setOnMapClickListener { latLng ->
            routePoints.add(latLng)
            googleMap.addMarker(MarkerOptions().position(latLng))
            googleMap.moveCamera(CameraUpdateFactory.newLatLng(latLng))
        }
    }

    private fun saveRoute() {
        // Get the user input
        val routeName = nameEditText.text.toString()
        val terrain = terrainSpinner.selectedItem.toString()
        val difficulty = difficultySpinner.selectedItem.toString()
        val estimatedTime = timeEditText.text.toString().toInt()

        // Create a new route object
        val route = Route(
            name = routeName,
            terrain = terrain,
            difficulty = difficulty,
            estimatedTime = estimatedTime,
            points = routePoints
        )

        // Save the route to the database ( Voitais ehkä käyttää supabasee)

        finish()
    }
}