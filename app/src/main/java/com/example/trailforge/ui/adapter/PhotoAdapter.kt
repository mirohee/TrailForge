package com.example.trailforge.ui.adapter

import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.trailforge.R


// Adapter class for displaying photos in a RecyclerView
class PhotoAdapter(private val photoUris: List<Uri>) : RecyclerView.Adapter<PhotoAdapter.PhotoViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhotoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_photo, parent, false)
        return PhotoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PhotoViewHolder, position: Int) {
        val photoUri = photoUris[position]

        // Load the image using Glide
        Glide.with(holder.imageView.context)
            .load(photoUri)
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = photoUris.size

    class PhotoViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.photoImageView)
    }
}