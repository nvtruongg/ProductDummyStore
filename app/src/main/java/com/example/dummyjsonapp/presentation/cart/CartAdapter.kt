package com.example.dummyjsonapp.presentation.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dummyjsonapp.databinding.ItemCartBinding
import com.example.dummyjsonapp.data.local.entity.CartItem

class CartAdapter (
    private val onIncreaseClick : (CartItem) -> Unit,
    private val onDecreaseClick : (CartItem) -> Unit,
    private val onDeleteClick : (CartItem) -> Unit,
    private val onItemClick : (CartItem) -> Unit
): ListAdapter<CartItem, CartAdapter.CartViewHolder>(CartDiffCallback) {
    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItem) {
            val product = cartItem.product

            // 1. Gán dữ liệu văn bản
            binding.tvCartTitle.text = product.title
            // Giả sử giá tiền là USD
            binding.tvCartPrice.text = "$${product.price}"
            binding.tvQuantity.text = cartItem.quantity.toString()

            // 2. Load ảnh Thumbnail (Dùng thư viện Glide, nếu bạn dùng Picasso/Coil thì thay đổi tương ứng)
            Glide.with(binding.root.context)
                .load(product.thumbnail)
                .into(binding.ivCartThumbnail)

            // 3. Xử lý các nút bấm tăng/giảm/xóa
            binding.btnIncrease.setOnClickListener {
                onIncreaseClick(cartItem)
            }

            binding.btnDecrease.setOnClickListener {
                onDecreaseClick(cartItem)
            }

            binding.btnDeleteCartItem.setOnClickListener {
                onDeleteClick(cartItem)
            }

            // 4. (Tùy chọn) Click vào cả item để xem lại chi tiết
            binding.root.setOnClickListener {
                onItemClick(cartItem)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding = ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}

object CartDiffCallback : DiffUtil.ItemCallback<CartItem>() {
    override fun areItemsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        return oldItem.product.id == newItem.product.id
    }

    override fun areContentsTheSame(oldItem: CartItem, newItem: CartItem): Boolean {
        // Cần kiểm tra cả số lượng để UI tự động đổi số khi bấm +/-
        return oldItem.product == newItem.product && oldItem.quantity == newItem.quantity
    }
}