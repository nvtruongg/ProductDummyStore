package com.example.dummyjsonapp.presentation.home

import android.graphics.Color
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.dummyjsonapp.databinding.ItemCategoryBinding
import com.example.dummyjsonapp.domain.model.CategoryModel

class CategoryAdapter(
    private var categories: List<CategoryModel>,
    private val onCategoryClick: (CategoryModel) -> Unit
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
            holder.binding.tvCategoryName.setBackgroundColor(Color.YELLOW)
            holder.binding.tvCategoryName.setTextColor(Color.RED)
            holder.binding.tvCategoryName.setTypeface(null, Typeface.BOLD) // In đậm nếu muốn
        } else {
            holder.binding.tvCategoryName.setBackgroundColor(Color.TRANSPARENT)
            holder.binding.tvCategoryName.setTextColor(Color.DKGRAY)
            holder.binding.tvCategoryName.setTypeface(null, Typeface.NORMAL)
        }

        holder.itemView.setOnClickListener {
            val oldPosition = selectedPosition
            selectedPosition = holder.adapterPosition
            notifyItemChanged(oldPosition)
            notifyItemChanged(selectedPosition)

            onCategoryClick(category)
        }
    }

    fun updateData(newCategories: List<CategoryModel>) {
        this.categories = newCategories
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = categories.size
}