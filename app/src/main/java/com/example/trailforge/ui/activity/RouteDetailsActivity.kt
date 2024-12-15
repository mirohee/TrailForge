package com.example.trailforge.ui.activity

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
import android.os.Build
import com.example.trailforge.R

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

            val existingRoute = RoutesListActivity.routes.find { it.name == routeName }
            existingRouteId = existingRoute?.id

            routePoints = existingRoute?.points ?: listOf()
            totalDistance = existingRoute?.distance ?: 0.0

            btnSaveRoute.text = getString(R.string.view_route) // Updated to use resource string
            etRouteName.isEnabled = false
            etRouteDescription.isEnabled = false
        } else {
            // New route
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                routePoints = intent.getParcelableArrayListExtra("route_points", GeoPoint::class.java) ?: arrayListOf()
            } else {
                @Suppress("DEPRECATION")
                routePoints = intent.getParcelableArrayListExtra("route_points") ?: arrayListOf()
            }
            totalDistance = intent.getDoubleExtra("total_distance", 0.0)
            btnSaveRoute.text = getString(R.string.save_route) // Updated to use resource string
        }

        setupRoutePreviewMap(routePoints)
        setupButtonListeners()
    }

    private fun setupRoutePreviewMap(routePoints: List<GeoPoint>) {
        routePreviewMap.setMultiTouchControls(true)
        routePreviewMap.controller.apply {
            setZoom(13.0)
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
                outlinePaint.color = ROUTE_COLOR // Replaced deprecated color property
                outlinePaint.strokeWidth = 6f    // Replaced deprecated width property
                setPoints(routePoints)
            }
            routePreviewMap.overlays.add(polyline)
        }
    }

    private fun setupButtonListeners() {
        btnAddPictures.setOnClickListener {
            Toast.makeText(this, getString(R.string.picture_selection_coming_soon), Toast.LENGTH_SHORT).show()
        }

        btnSaveRoute.setOnClickListener {
            if (isExistingRoute) {
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
                etRouteName.error = getString(R.string.route_name_required) // Use resource string
                return@setOnClickListener
            }

            val newRoute = Route(
                id = System.currentTimeMillis(),
                name = routeName,
                description = routeDescription,
                distance = totalDistance,
                points = routePoints
            )

            RoutesListActivity.routes.add(newRoute)

            Toast.makeText(this, getString(R.string.route_saved, routeName), Toast.LENGTH_SHORT).show()
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