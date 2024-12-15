package com.example.trailforge.ui.activity

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import android.graphics.Color
import androidx.core.view.WindowCompat
import androidx.core.graphics.ColorUtils
import com.example.trailforge.R

// RouteViewActivity displays a full-screen view of a route

class RouteViewActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var tvRouteName: TextView
    private lateinit var tvRouteDescription: TextView
    private lateinit var tvRouteDistance: TextView
    private lateinit var btnClose: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Configuration.getInstance().load(this, getSharedPreferences("osm_pref", MODE_PRIVATE))
        setContentView(R.layout.activity_route_view)

        // Make the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.TRANSPARENT

        // Initialize views
        mapView = findViewById(R.id.mapViewFullScreen)
        tvRouteName = findViewById(R.id.tvRouteNameFullScreen)
        tvRouteDescription = findViewById(R.id.tvRouteDescriptionFullScreen)
        tvRouteDistance = findViewById(R.id.tvRouteDistanceFullScreen)
        btnClose = findViewById(R.id.btnCloseRouteView)

        // Retrieve route details from intent
        val routeName = intent.getStringExtra("route_name") ?: "Unnamed Route"
        val routeDescription = intent.getStringExtra("route_description") ?: "No description"
        val routeDistance = intent.getDoubleExtra("total_distance", 0.0)
        val routePoints = intent.getParcelableArrayListExtra<GeoPoint>("route_points") ?: arrayListOf()

        // Set text views
        tvRouteName.text = routeName
        tvRouteDescription.text = routeDescription
        tvRouteDistance.text = String.format("%.1f km", routeDistance)

        // Setup map
        setupMap(routePoints)

        // Close button
        btnClose.setOnClickListener { finish() }
    }

    private fun setupMap(routePoints: List<GeoPoint>) {
        mapView.setMultiTouchControls(true)

        // Configure map view
        mapView.controller.apply {
            setZoom(15.0)

            // Center the map on the route
            if (routePoints.isNotEmpty()) {
                val centerPoint = GeoPoint(
                    routePoints.map { it.latitude }.average(),
                    routePoints.map { it.longitude }.average()
                )
                setCenter(centerPoint)
            }
        }

        // Add route polyline
        if (routePoints.size > 1) {
            val polyline = Polyline().apply {
                // Use a slightly modified route color for better visibility
                color = ColorUtils.blendARGB(Color.parseColor("#FF6B6B"), Color.BLACK, 0.2f)
                width = 8f
                setPoints(routePoints)
            }
            mapView.overlays.add(polyline)
        }

        // Add markers for start and end points
        if (routePoints.isNotEmpty()) {
            // Start marker
            val startMarker = Marker(mapView).apply {
                position = routePoints.first()
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "Start Point"
                icon = resources.getDrawable(R.drawable.ic_marker_start, null)
            }
            mapView.overlays.add(startMarker)

            // End marker
            val endMarker = Marker(mapView).apply {
                position = routePoints.last()
                setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM)
                title = "End Point"
                icon = resources.getDrawable(R.drawable.ic_marker_end, null)
            }
            mapView.overlays.add(endMarker)
        }

        mapView.invalidate()
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