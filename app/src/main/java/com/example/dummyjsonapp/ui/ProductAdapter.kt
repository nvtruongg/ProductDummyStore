package com.example.dummyjsonapp.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dummyjsonapp.databinding.ItemProductBinding
import com.example.dummyjsonapp.model.Product
import kotlin.math.roundToInt

class ProductAdapter(private val onItemClick: (Product) -> Unit) :
    ListAdapter<Product, ProductAdapter.ProductViewHolder>(ProductDiffCallback()) {

    // Tạo ViewHolder bằng ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding = ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding)
    }

    // Đổ dữ liệu vào UI
    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = getItem(position)
        holder.bind(product)
    }

    // Lớp nắm giữ giao diện của 1 item
    inner class ProductViewHolder(private val binding: ItemProductBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.tvTitle.text = product.title
            binding.tvPrice.text = "$${product.price}"
            binding.tvRating.text = "⭐ ${product.rating}"

            // Xử lý nhãn giảm giá: Chỉ hiển thị khi giảm từ 1% trở lên
            if (product.discountPercentage >= 1.0) {
                val discount = product.discountPercentage
                binding.tvDiscount.text = "-$discount%"
                binding.tvDiscount.visibility = View.VISIBLE
            } else {
                binding.tvDiscount.visibility = View.GONE
            }

            // Tải ảnh từ URL nhét vào ImageView
            Glide.with(binding.root.context)
                .load(product.thumbnail)
                .into(binding.ivThumbnail)

            // Khi người dùng bấm vào toàn bộ thẻ CardView (root)
            binding.root.setOnClickListener {
                onItemClick(product) // Bắn dữ liệu sản phẩm đó ra ngoài
            }
        }
    }

    // Bộ lọc DiffUtil
    class ProductDiffCallback : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
}