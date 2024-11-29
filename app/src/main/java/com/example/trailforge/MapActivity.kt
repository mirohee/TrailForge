package com.example.trailforge

import android.Manifest
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import org.osmdroid.config.Configuration
import org.osmdroid.util.GeoPoint
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker
import android.graphics.Color
import android.graphics.Paint
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.events.MapEventsReceiver
import org.osmdroid.views.overlay.MapEventsOverlay
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.animation.LinearInterpolator
import android.widget.TextView

class MapActivity : AppCompatActivity() {

    private lateinit var mapView: MapView
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var btnCreateRoute: Button
    private lateinit var tvDistance: TextView
    private lateinit var btnRoutesList: Button

    private val routePoints = mutableListOf<GeoPoint>()
    private var totalDistance = 0.0  // Variable to hold the total distance of the route

    private var isCreatingRoute = false
    private var currentPolyline: Polyline? = null
    private val helsinkiLocation = GeoPoint(60.1699, 24.9384)

    private val ROUTE_COLOR = Color.parseColor("#FF6B6B")
    private val POINT_COLOR = Color.parseColor("#4A4E69")
    private val POINT_BORDER_COLOR = Color.WHITE
    private val POINT_SIZE_DP = 10f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)
        Configuration.getInstance().load(this, getSharedPreferences("osm_pref", MODE_PRIVATE))
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        mapView = findViewById(R.id.mapView)
        mapView.setMultiTouchControls(true)
        btnCreateRoute = findViewById(R.id.btnCreateRoute)
        tvDistance = findViewById(R.id.tvDistance)  // Initialize the TextView for distance

        btnRoutesList = findViewById(R.id.btnRoutesList)

        setupMap()
        setupButtons()
        requestLocationPermissions()
    }

    private fun setupMap() {
        mapView.controller.apply {
            setZoom(15.0)
            setCenter(helsinkiLocation)
        }

        val mapEventsOverlay = MapEventsOverlay(object : MapEventsReceiver {
            override fun singleTapConfirmedHelper(p: GeoPoint): Boolean {
                if (isCreatingRoute) {
                    // Simply use the tapped point directly
                    Log.d("RouteDebug", "Raw tap coordinates: Lat=${p.latitude}, Lon=${p.longitude}")
                    addRoutePoint(p)
                    return true
                }
                return false
            }

            override fun longPressHelper(p: GeoPoint): Boolean = false
        })

        mapView.overlays.clear()
        mapView.overlays.add(mapEventsOverlay)
        addMarker(helsinkiLocation, "Default Location: Helsinki")
    }

    private fun setupButtons() {
        btnCreateRoute.setOnClickListener {
            toggleRouteCreation()
        }

        // Add click listener to open routes list
        btnRoutesList.setOnClickListener {
            val intent = Intent(this, RoutesListActivity::class.java)
            startActivity(intent)
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
        totalDistance = 0.0
        updateDistanceDisplay()
        btnCreateRoute.text = "Finish Route"
        currentPolyline = Polyline().apply {
            outlinePaint.apply {
                color = ROUTE_COLOR
                strokeWidth = 6f
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                isAntiAlias = true
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
            return
        }

        // Create an intent to launch RouteDetailsActivity
        val intent = Intent(this, RouteDetailsActivity::class.java).apply {
            // Convert GeoPoint list to ArrayList for parcelable passing
            putParcelableArrayListExtra("route_points", ArrayList(routePoints))
            putExtra("total_distance", totalDistance)
        }
        startActivity(intent)

        // Reset route creation state
        routePoints.clear()
        totalDistance = 0.0
        currentPolyline?.setPoints(emptyList())
        mapView.overlays.removeAll { it is Marker }
        mapView.invalidate()
    }

    private fun addRoutePoint(point: GeoPoint) {
        // Add validation to ensure point is within reasonable bounds of Helsinki
        val helsinkiBounds = object {
            val minLat = 60.1  // Approximate Helsinki boundaries
            val maxLat = 60.3
            val minLon = 24.8
            val maxLon = 25.0
        }

        if (point.latitude < helsinkiBounds.minLat ||
            point.latitude > helsinkiBounds.maxLat ||
            point.longitude < helsinkiBounds.minLon ||
            point.longitude > helsinkiBounds.maxLon) {
            Log.w("RouteDebug", "Point outside Helsinki bounds: Lat=${point.latitude}, Lon=${point.longitude}")
            Toast.makeText(this, "Selected point is outside Helsinki area", Toast.LENGTH_SHORT).show()
            return
        }

        Log.d("RouteDebug", "Adding point: Lat=${point.latitude}, Lon=${point.longitude}")
        routePoints.add(point)
        recalculateTotalDistance()
        updateDistanceDisplay()

        // Add visual marker
        val pointMarker = Marker(mapView).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            val markerDrawable = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(POINT_COLOR)
                setStroke(3, POINT_BORDER_COLOR)
                val size = (POINT_SIZE_DP * resources.displayMetrics.density).toInt()
                setSize(size, size)
            }
            icon = markerDrawable
            setInfoWindow(null)
        }
        mapView.overlays.add(pointMarker)
        currentPolyline?.setPoints(routePoints)
        addPulseEffect(point)
        mapView.invalidate()
    }

    private fun recalculateTotalDistance() {
        totalDistance = 0.0
        if (routePoints.size >= 2) {
            for (i in 0 until routePoints.size - 1) {
                val start = routePoints[i]
                val end = routePoints[i + 1]
                val segmentDistance = calculateDistance(start, end)
                Log.d("RouteDebug", "Segment $i distance: $segmentDistance km")
                totalDistance += segmentDistance
            }
        }
        // Ensure we're working with properly rounded numbers
        totalDistance = Math.round(totalDistance * 1000) / 1000.0
        Log.d("RouteDebug", "Total distance: $totalDistance km")
    }

    private fun calculateDistance(start: GeoPoint, end: GeoPoint): Double {
        val results = FloatArray(1)
        Location.distanceBetween(
            start.latitude, start.longitude,
            end.latitude, end.longitude,
            results
        )
        val distance = results[0].toDouble() / 1000.0  // Convert meters to kilometers

        // Log the calculation details
        Log.d("RouteDebug", """
            Distance calculation:
            Start: Lat=${start.latitude}, Lon=${start.longitude}
            End: Lat=${end.latitude}, Lon=${end.longitude}
            Distance: $distance km
        """.trimIndent())

        return distance
    }


    private fun updateDistanceDisplay() {
        // Convert to meters only if less than 1 km
        if (totalDistance < 1.0) {
            val meters = Math.round(totalDistance * 1000)  // Convert km to meters correctly
            tvDistance.text = "Distance: $meters m"
        } else {
            // Display in kilometers with 2 decimal places
            tvDistance.text = "Distance: %.2f km".format(totalDistance)
        }

        // Add debug log to verify conversion
        Log.d("RouteDebug", "Display update - Raw distance: $totalDistance km")
    }
    // validate GeoPoints
    private fun isValidGeoPoint(point: GeoPoint): Boolean {
        return point.latitude in -90.0..90.0 &&
                point.longitude in -180.0..180.0
    }
    // check distance validity
    private fun isReasonableDistance(distance: Double): Boolean {
        return distance in 0.0..100.0
    }

    private fun addPulseEffect(point: GeoPoint) {
        val initialSize = (POINT_SIZE_DP * resources.displayMetrics.density).toInt()
        val maxSize = initialSize * 4
        val pulseDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.TRANSPARENT)
            setStroke(2, ROUTE_COLOR)
            setSize(initialSize, initialSize)
        }

        val pulseMarker = Marker(mapView).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = pulseDrawable
            alpha = 0.7f
        }
        mapView.overlays.add(pulseMarker)

        val sizeAnimator = ValueAnimator.ofInt(initialSize, maxSize).apply {
            duration = 400
            interpolator = LinearInterpolator()
            addUpdateListener { animator ->
                val size = animator.animatedValue as Int
                pulseDrawable.setSize(size, size)
                mapView.invalidate()
            }
        }

        val alphaAnimator = ObjectAnimator.ofFloat(pulseMarker, "alpha", 0.7f, 0f).apply {
            duration = 400
        }

        AnimatorSet().apply {
            playTogether(sizeAnimator, alphaAnimator)
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    mapView.overlays.remove(pulseMarker)
                    mapView.invalidate()
                }
            })
            start()
        }
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
                    // Only add marker, don't center the map
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