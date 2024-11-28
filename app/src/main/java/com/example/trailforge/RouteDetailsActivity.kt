package com.example.trailforge

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputEditText
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Polyline

class RouteDetailsActivity : AppCompatActivity() {

    private lateinit var etRouteName: TextInputEditText
    private lateinit var etRouteDescription: TextInputEditText
    private lateinit var btnAddPictures: Button
    private lateinit var btnSaveRoute: Button
    private lateinit var routePreviewMap: MapView

    // Define the color here to avoid unresolved reference
    companion object {
        val ROUTE_COLOR = Color.parseColor("#FF6B6B")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osm_pref", MODE_PRIVATE))
        setContentView(R.layout.activity_route_details)

        // Initialize views
        etRouteName = findViewById(R.id.etRouteName)
        etRouteDescription = findViewById(R.id.etRouteDescription)
        btnAddPictures = findViewById(R.id.btnAddPictures)
        btnSaveRoute = findViewById(R.id.btnSaveRoute)
        routePreviewMap = findViewById(R.id.routePreviewMap)

        // Retrieve route points from intent
        val routePoints = intent.getParcelableArrayListExtra<GeoPoint>("route_points") ?: arrayListOf()

        // Setup map preview
        setupRoutePreviewMap(routePoints)

        // Setup button listeners
        setupButtonListeners(routePoints)
    }

    private fun setupRoutePreviewMap(routePoints: List<GeoPoint>) {
        routePreviewMap.setMultiTouchControls(true)
        routePreviewMap.controller.apply {
            setZoom(13.0)

            // Center the map on the route
            if (routePoints.isNotEmpty()) {
                val centerPoint = GeoPoint(
                    routePoints.map { it.latitude }.average(),
                    routePoints.map { it.longitude }.average()
                )
                setCenter(centerPoint)
            }
        }

        // Add route polyline to preview map
        if (routePoints.size > 1) {
            val polyline = Polyline().apply {
                color = ROUTE_COLOR
                width = 6f
                setPoints(routePoints)
            }
            routePreviewMap.overlays.add(polyline)
        }
    }

    private fun setupButtonListeners(routePoints: List<GeoPoint>) {
        btnAddPictures.setOnClickListener {
            // TODO: Implement picture selection
            Toast.makeText(this, "Picture selection coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnSaveRoute.setOnClickListener {
            val routeName = etRouteName.text.toString().trim()
            val routeDescription = etRouteDescription.text.toString().trim()

            if (routeName.isEmpty()) {
                etRouteName.error = "Route name is required"
                return@setOnClickListener
            }

            // TODO: Save route to database or persistent storage
            Toast.makeText(this, "Route saved: $routeName", Toast.LENGTH_SHORT).show()

            // Optional: Return to main activity or close
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        routePreviewMap.onResume()
    }

    override fun onPause() {
        super.onPause()
        routePreviewMap.onPause()
    }
}