package com.example.dummyjsonapp.presentation.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.core.widget.addTextChangedListener
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dummyjsonapp.databinding.FragmentHomeBinding
import com.example.dummyjsonapp.presentation.product.DetailActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private val viewModel: HomeViewModel by viewModels()
    private var searchJob: Job? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val categoryAdapter = CategoryAdapter(emptyList()) { clickedCategory ->
            binding.recyclerView.scrollToPosition(0)
            viewModel.filterByCategory(clickedCategory.slug)
        }
        binding.rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = categoryAdapter

        binding.edtSearch.addTextChangedListener {
            searchJob?.cancel()
            searchJob = lifecycleScope.launch {
                delay(500)
                val keyword = binding.edtSearch.text.toString().trim()
                if (keyword.isNotEmpty()) {
                    viewModel.searchProducts(keyword)
                } else {
                    viewModel.loadData()
                }
                binding.recyclerView.scrollToPosition(0)
            }
        }

        binding.edtSearch.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                val imm = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
                return@setOnEditorActionListener true
            }
            binding.edtSearch.clearFocus()
            false
        }

        adapter = ProductAdapter(
            onItemClick = { clickedProduct ->
                val intent = Intent(requireContext(), DetailActivity::class.java)
                intent.putExtra("product_id", clickedProduct.id)
                startActivity(intent)
            },
            onFavoriteClick = { product, isFavorite ->
                viewModel.toggleFavorite(product.id, isFavorite)
            }
        )
        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.recyclerView.adapter = adapter

        observeUiState()
    }

    private fun observeUiState() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.uiState.collect { state ->

                    binding.progressBar.visibility = if (state.isLoading) View.VISIBLE else View.GONE

                    adapter.submitList(state.products) {
                        if (state.products.isNotEmpty()) {
                            binding.recyclerView.scrollToPosition(0)
                        }
                    }

                    if (!state.isLoading && state.products.isEmpty()) {
                        binding.recyclerView.visibility = View.GONE
                        binding.tvEmptyMessage.visibility = View.VISIBLE
                    } else {
                        binding.recyclerView.visibility = View.VISIBLE
                        binding.tvEmptyMessage.visibility = View.GONE
                    }

                    (binding.rvCategories.adapter as? CategoryAdapter)?.updateData(state.categories)

                    state.errorMessage?.let{message ->
                        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
                        viewModel.clearError()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}