package com.example.trailforge.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.GradientDrawable
import org.osmdroid.views.overlay.Marker
import org.osmdroid.views.overlay.Polyline
import org.osmdroid.util.GeoPoint

object MapStyling {
    // Colors
    const val ROUTE_COLOR = "#FF6B6B"
    const val POINT_COLOR = "#4A4E69"
    const val POINT_BORDER_COLOR = "#FFFFFF"
    const val SUCCESS_COLOR = "#00C853"

    // Dimensions
    const val POINT_SIZE_DP = 10f
    const val STROKE_WIDTH = 6f
    const val PULSE_DURATION = 400L
    const val PULSE_MAX_SIZE_MULTIPLIER = 4

    fun createRoutePolyline(): Polyline {
        return Polyline().apply {
            outlinePaint.apply {
                color = Color.parseColor(ROUTE_COLOR)
                strokeWidth = STROKE_WIDTH
                strokeCap = Paint.Cap.ROUND
                strokeJoin = Paint.Join.ROUND
                isAntiAlias = true
                setShadowLayer(6f, 0f, 0f, Color.parseColor("#40$ROUTE_COLOR"))
            }
        }
    }

    fun createPointMarker(context: Context, point: GeoPoint): Marker {
        return Marker(null).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createPointDrawable(context, POINT_COLOR)
            setInfoWindow(null)
        }
    }

    fun createCompletionMarker(context: Context, point: GeoPoint): Marker {
        return Marker(null).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createPointDrawable(context, SUCCESS_COLOR)
            alpha = 0f
        }
    }

    fun createPulseMarker(context: Context, point: GeoPoint, initialSize: Int): Marker {
        return Marker(null).apply {
            position = point
            setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_CENTER)
            icon = createPulseDrawable(context, initialSize)
            alpha = 0.7f
        }
    }

    private fun createPointDrawable(context: Context, color: String): GradientDrawable {
        val size = (POINT_SIZE_DP * context.resources.displayMetrics.density).toInt()
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.parseColor(color))
            setStroke(3, Color.parseColor(POINT_BORDER_COLOR))
            setSize(size, size)
        }
    }

    private fun createPulseDrawable(context: Context, size: Int): GradientDrawable {
        return GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(Color.TRANSPARENT)
            setStroke(2, Color.parseColor(ROUTE_COLOR))
            setSize(size, size)
        }
    }
}