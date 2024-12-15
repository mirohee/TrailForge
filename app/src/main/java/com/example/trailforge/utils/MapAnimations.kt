package com.example.trailforge.utils

import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.graphics.drawable.GradientDrawable
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.LinearInterpolator
import org.osmdroid.views.MapView
import org.osmdroid.views.overlay.Marker

// Utility class for map animations

object MapAnimations {
    fun animateCompletionMarker(
        mapView: MapView,
        marker: Marker,
        onComplete: () -> Unit
    ) {
        ObjectAnimator.ofFloat(marker, "alpha", 0f, 1f).apply {
            duration = 200
            interpolator = AccelerateDecelerateInterpolator()
            addListener(object : android.animation.AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: android.animation.Animator) {
                    onComplete()
                }
            })
            start()
        }
    }

    fun createPulseAnimation(
        mapView: MapView,
        pulseMarker: Marker,
        pulseDrawable: GradientDrawable,
        initialSize: Int,
        maxSize: Int
    ) {
        val sizeAnimator = ValueAnimator.ofInt(initialSize, maxSize).apply {
            duration = MapStyling.PULSE_DURATION
            interpolator = LinearInterpolator()
            addUpdateListener { animator ->
                val size = animator.animatedValue as Int
                pulseDrawable.setSize(size, size)
                mapView.invalidate()
            }
        }

        val alphaAnimator = ObjectAnimator.ofFloat(pulseMarker, "alpha", 0.7f, 0f).apply {
            duration = MapStyling.PULSE_DURATION
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
}