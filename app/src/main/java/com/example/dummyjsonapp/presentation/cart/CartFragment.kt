package com.example.dummyjsonapp.presentation.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dummyjsonapp.databinding.FragmentCartBinding
import com.example.dummyjsonapp.presentation.checkout.CheckoutActivity
import com.example.dummyjsonapp.presentation.product.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: CartAdapter
    private val viewModel: CartViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        adapter = CartAdapter(
            onIncreaseClick = { cartItem ->
                viewModel.updateCartQuantity(cartItem.product.id, cartItem.quantity + 1)
            },
            onDecreaseClick = { cartItem ->
                viewModel.updateCartQuantity(cartItem.product.id, cartItem.quantity - 1)
            },
            onDeleteClick = { cartItem ->
                viewModel.removeCartItem(cartItem.product.id)
            },
            onItemClick = { cartItem ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("product_id", cartItem.product.id)
                startActivity(intent)
            }
        )
        binding.rvCart.layoutManager = LinearLayoutManager(requireContext())
        binding.rvCart.adapter = adapter

        observeUiState()

        binding.btnCheckout.setOnClickListener {
            val currentItems = viewModel.uiState.value.items
            if (currentItems.isEmpty()) {
                Toast.makeText(requireContext(), "Giỏ hàng trống!", Toast.LENGTH_SHORT).show()
            } else {
                val intent = Intent(requireContext(), CheckoutActivity::class.java)
                startActivity(intent)
            }
        }
    }
    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->
                    adapter.submitList(state.items)

                    if (!state.isLoading && state.items.isEmpty()) {
                        binding.rvCart.visibility = View.GONE
                        binding.bottomCheckoutBar.visibility = View.GONE
                        binding.layoutEmptyCart.visibility = View.VISIBLE
                    } else {
                        binding.rvCart.visibility = View.VISIBLE
                        binding.bottomCheckoutBar.visibility = View.VISIBLE
                        binding.layoutEmptyCart.visibility = View.GONE
                    }

                    binding.tvTotalPrice.text = String.format("$%.2f", state.totalPrice)

                    state.errorMessage?.let{massage->
                        Toast.makeText(requireContext(), massage, Toast.LENGTH_SHORT).show()
                        viewModel.clearMessage()
                    }
                }
            }
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onResume() {
        super.onResume()
        viewModel.loadCart()
    }
}