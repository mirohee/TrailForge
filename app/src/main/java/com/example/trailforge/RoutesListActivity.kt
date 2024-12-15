package com.example.trailforge

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.ui.graphics.Color
import androidx.core.view.WindowCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.osmdroid.util.GeoPoint

// Data class to represent a Route
data class Route(
    val id: Long,
    val name: String,
    val description: String,
    val distance: Double,
    val points: List<GeoPoint>
)

// RoutesListActivity displays a list of routes

class RoutesListActivity : AppCompatActivity() {

    private lateinit var rvRoutes: RecyclerView
    private lateinit var fabCreateRoute: FloatingActionButton
    private lateinit var routesAdapter: RoutesAdapter

    companion object {
        val routes = mutableListOf(
            Route(1, "Helsinki City Walk", "Explore downtown Helsinki", 5.6, listOf()),
            Route(2, "Waterfront Trail", "Scenic route along the coast", 8.2, listOf()),
            Route(3, "Forest Path", "Peaceful woodland route", 3.4, listOf())
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_routes_list)

        // Make the status bar transparent
        WindowCompat.setDecorFitsSystemWindows(window, false)
        window.statusBarColor = Color.Transparent.value.toInt() // Use android

        rvRoutes = findViewById(R.id.rvRoutes)
        fabCreateRoute = findViewById(R.id.fabCreateRoute)

        // Setup RecyclerView
        routesAdapter = RoutesAdapter(routes) { route ->
            // Handle route item click - open route details or preview
            val intent = Intent(this, RouteDetailsActivity::class.java).apply {
                putParcelableArrayListExtra("route_points", ArrayList(route.points))
                putExtra("route_name", route.name)
                putExtra("route_description", route.description)
                putExtra("total_distance", route.distance)
            }
            startActivity(intent)
        }

        rvRoutes.apply {
            layoutManager = LinearLayoutManager(this@RoutesListActivity)
            adapter = routesAdapter
        }

        // Setup FAB to create new route
        fabCreateRoute.setOnClickListener {
            val intent = Intent(this, MapActivity::class.java)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        routesAdapter.notifyDataSetChanged()
    }
}

// Adapter for the RecyclerView to display routes

class RoutesAdapter(
    private val routes: List<Route>,
    private val onItemClick: (Route) -> Unit
) : RecyclerView.Adapter<RoutesAdapter.RouteViewHolder>() {


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RouteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_route, parent, false)
        return RouteViewHolder(view, onItemClick)
    }

    override fun onBindViewHolder(holder: RouteViewHolder, position: Int) {
        holder.bind(routes[position])
    }

    override fun getItemCount() = routes.size

    // ViewHolder for the RecyclerView

    class RouteViewHolder(
        itemView: View,
        private val onItemClick: (Route) -> Unit
    ) : RecyclerView.ViewHolder(itemView) {
        private val tvRouteName: TextView = itemView.findViewById(R.id.tvRouteName)
        private val tvRouteDistance: TextView = itemView.findViewById(R.id.tvRouteDistance)

        // Bind data to the ViewHolder
        fun bind(route: Route) {
            tvRouteName.text = route.name
            tvRouteDistance.text = String.format("%.1f km", route.distance)

            itemView.setOnClickListener { onItemClick(route) }
        }
    }
}