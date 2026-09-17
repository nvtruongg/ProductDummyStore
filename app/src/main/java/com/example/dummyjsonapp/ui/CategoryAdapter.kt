package com.example.dummyjsonapp.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dummyjsonapp.databinding.ItemCategoryBinding
import com.example.dummyjsonapp.data.remote.dto.Category

class CategoryAdapter(
    private var categories: List<Category>,
    private val onCategoryClick: (Category) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var selectedPosition = 0
    class CategoryViewHolder(val binding: ItemCategoryBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val binding = ItemCategoryBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return CategoryViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val category = categories[position]
        holder.binding.tvCategoryName.text = category.name

        if (position == selectedPosition) {
            holder.binding.tvCategoryName.setBackgroundColor(android.graphics.Color.YELLOW)
            holder.binding.tvCategoryName.setTextColor(android.graphics.Color.RED)
            holder.binding.tvCategoryName.setTypeface(null, android.graphics.Typeface.BOLD) // In đậm nếu muốn
        } else {
            holder.binding.tvCategoryName.setBackgroundColor(android.graphics.Color.TRANSPARENT)
            holder.binding.tvCategoryName.setTextColor(android.graphics.Color.DKGRAY)
            holder.binding.tvCategoryName.setTypeface(null, android.graphics.Typeface.NORMAL)
        }

        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)

            onCategoryClick(category)
        }
    }

    // hàm này để MainActivity có thể bơm dữ liệu mới vào
    fun updateData(newCategories: List<Category>) {
        this.categories = newCategories
        notifyDataSetChanged() // Báo cho RecyclerView vẽ lại giao diện
    }

    override fun getItemCount(): Int = categories.size
}