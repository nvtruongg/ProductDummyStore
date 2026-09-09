package com.example.dummyjsonapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dummyjsonapp.R

class ImageSliderAdapter(private val imageUrls : List<String>) :
    RecyclerView.Adapter<ImageSliderAdapter.SliderViewHolder>(){
        inner class SliderViewHolder(view: View) : RecyclerView.ViewHolder(view){
            val imageView : ImageView = view.findViewById(R.id.ivSliderImage)
        }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SliderViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_slider_image, parent, false)
        return SliderViewHolder(view)
    }

    override fun onBindViewHolder(holder: SliderViewHolder, position: Int) {
        Glide.with(holder.itemView.context)
            .load(imageUrls[position])
            .into(holder.imageView)
    }

    override fun getItemCount(): Int = imageUrls.size
}