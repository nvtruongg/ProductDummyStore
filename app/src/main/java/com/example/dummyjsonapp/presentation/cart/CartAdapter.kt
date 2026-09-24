package com.example.dummyjsonapp.presentation.cart

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.dummyjsonapp.databinding.ItemCartBinding
import com.example.dummyjsonapp.data.local.entity.CartItem
import com.example.dummyjsonapp.domain.model.CartItemModel

class CartAdapter (
    private val onIncreaseClick : (CartItemModel) -> Unit,
    private val onDecreaseClick : (CartItemModel) -> Unit,
    private val onDeleteClick : (CartItemModel) -> Unit,
    private val onItemClick : (CartItemModel) -> Unit
): ListAdapter<CartItemModel, CartAdapter.CartViewHolder>(CartDiffCallback) {
    inner class CartViewHolder(private val binding: ItemCartBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(cartItem: CartItemModel) {
            val product = cartItem.product

            binding.tvCartTitle.text = product.title
            binding.tvCartPrice.text = "$${product.price}"
            binding.tvQuantity.text = cartItem.quantity.toString()

            Glide.with(binding.root.context)
                .load(product.thumbnail)
                .into(binding.ivCartThumbnail)

            binding.btnIncrease.setOnClickListener {
                onIncreaseClick(cartItem)
            }

            binding.btnDecrease.setOnClickListener {
                onDecreaseClick(cartItem)
            }

            binding.btnDeleteCartItem.setOnClickListener {
                onDeleteClick(cartItem)
            }

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

object CartDiffCallback : DiffUtil.ItemCallback<CartItemModel>() {
    override fun areItemsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
        return oldItem.product.id == newItem.product.id
    }

    override fun areContentsTheSame(oldItem: CartItemModel, newItem: CartItemModel): Boolean {
        return oldItem.product == newItem.product && oldItem.quantity == newItem.quantity
    }
}