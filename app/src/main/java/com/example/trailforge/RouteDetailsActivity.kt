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

    private var routePoints: List<GeoPoint> = listOf()
    private var totalDistance: Double = 0.0
    private var isExistingRoute: Boolean = false
    private var existingRouteId: Long? = null

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

        // Check if this is an existing route or a new route
        val routeName = intent.getStringExtra("route_name")
        val routeDescription = intent.getStringExtra("route_description")

        if (routeName != null) {
            // Existing route
            isExistingRoute = true
            etRouteName.setText(routeName)
            etRouteDescription.setText(routeDescription)

            // Find the existing route to get its details
            val existingRoute = RoutesListActivity.routes.find { it.name == routeName }
            existingRouteId = existingRoute?.id

            routePoints = existingRoute?.points ?: listOf()
            totalDistance = existingRoute?.distance ?: 0.0

            // Change button for viewing existing route
            btnSaveRoute.text = "View Route"
            etRouteName.isEnabled = false
            etRouteDescription.isEnabled = false
        } else {
            // New route
            routePoints = intent.getParcelableArrayListExtra<GeoPoint>("route_points") ?: arrayListOf()
            totalDistance = intent.getDoubleExtra("total_distance", 0.0)
            btnSaveRoute.text = "Save Route"
        }

        setupRoutePreviewMap(routePoints)
        setupButtonListeners()
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

        if (routePoints.size > 1) {
            val polyline = Polyline().apply {
                color = ROUTE_COLOR
                width = 6f
                setPoints(routePoints)
            }
            routePreviewMap.overlays.add(polyline)
        }
    }

    private fun setupButtonListeners() {
        btnAddPictures.setOnClickListener {
            Toast.makeText(this, "Picture selection coming soon!", Toast.LENGTH_SHORT).show()
        }

        btnSaveRoute.setOnClickListener {
            if (isExistingRoute) {
                // Launch full-screen route view for existing route
                val intent = Intent(this, RouteViewActivity::class.java).apply {
                    putParcelableArrayListExtra("route_points", ArrayList(routePoints))
                    putExtra("route_name", etRouteName.text.toString())
                    putExtra("route_description", etRouteDescription.text.toString())
                    putExtra("total_distance", totalDistance)
                }
                startActivity(intent)
                return@setOnClickListener
            }

            val routeName = etRouteName.text.toString().trim()
            val routeDescription = etRouteDescription.text.toString().trim()

            if (routeName.isEmpty()) {
                etRouteName.error = "Route name is required"
                return@setOnClickListener
            }

            // Create a new route and add it to the static routes list
            val newRoute = Route(
                id = System.currentTimeMillis(), // Use timestamp as unique ID
                name = routeName,
                description = routeDescription,
                distance = totalDistance,
                points = routePoints
            )

            // Add the route to the static routes list
            RoutesListActivity.routes.add(newRoute)

            Toast.makeText(this, "Route saved: $routeName", Toast.LENGTH_SHORT).show()
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