package com.example.dummyjsonapp.presentation.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dummyjsonapp.R
import com.example.dummyjsonapp.databinding.ItemProductBinding
import com.example.dummyjsonapp.domain.model.ProductModel
import kotlin.math.roundToInt

class ProductAdapter(private val onItemClick: (ProductModel) -> Unit,
                     private val onFavoriteClick: (ProductModel, Boolean) -> Unit) :
    ListAdapter<ProductModel, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: ProductModel) {
            binding.tvTitle.text = product.title
            binding.tvPrice.text = "$${product.price}"
            binding.tvRating.text = "⭐ ${product.rating}"

            if (product.discountPercentage >= 1.0) {
                val discount = product.discountPercentage.roundToInt()
                binding.tvDiscount.text = "-$discount%"
                binding.tvDiscount.visibility = View.VISIBLE
            } else {
                binding.tvDiscount.visibility = View.GONE
            }

            Glide.with(binding.root.context)
                .load(product.thumbnail)
                .into(binding.ivThumbnail)

            binding.root.setOnClickListener {
                onItemClick(product)
            }

            if(product.isFavorite){
                binding.ivFavorite.setImageResource(R.drawable.ic_favorite_filled)
            }else{
                binding.ivFavorite.setImageResource(R.drawable.heart)
            }

            binding.ivFavorite.setOnClickListener {
                onFavoriteClick(product, !product.isFavorite)
            }
        }
    }

    //DiffUtil
    class ProductDiffCallback : DiffUtil.ItemCallback<ProductModel>() {
        override fun areItemsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ProductModel, newItem: ProductModel): Boolean {
            return oldItem == newItem
        }
    }
}