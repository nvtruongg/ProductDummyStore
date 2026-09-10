package com.example.dummyjsonapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.dummyjsonapp.R

class CategoryAdapter(private val categories: List<String>) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    class CategoryViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val tvName: TextView = view.findViewById(R.id.tvCategoryName)
    }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_category, parent, false)
            return CategoryViewHolder(view)
        }
        override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
            holder.tvName.text = categories[position]

            // Thêm hiệu ứng nhạt đi khi bấm vào để UX tốt hơn
            holder.itemView.setOnClickListener {
                // Tạm thời để trống logic chọn danh mục
            }
        }

        override fun getItemCount(): Int = categories.size
}