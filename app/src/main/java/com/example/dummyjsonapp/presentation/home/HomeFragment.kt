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
import androidx.lifecycle.lifecycleScope
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

        // 1. Categories
        val categoryAdapter = CategoryAdapter(emptyList()) { clickedCategory ->
            viewModel.filterByCategory(clickedCategory.slug)
        }
        binding.rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        binding.rvCategories.adapter = categoryAdapter

        // 2. Ô tìm kiếm
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

        // 3. RecyclerView Sản phẩm
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

        // 4. Lắng nghe dữ liệu
        observeViewModel()
    }

    private fun observeViewModel() {
        // CHÚ Ý: Dùng viewLifecycleOwner thay vì this trong Fragment
        viewModel.products.observe(viewLifecycleOwner) { productList ->
            if (productList != null) {
                adapter.submitList(productList)
                if (productList.isEmpty()) {
                    binding.recyclerView.visibility = View.GONE
                    binding.tvEmptyMessage.visibility = View.VISIBLE
                } else {
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvEmptyMessage.visibility = View.GONE
                }
            }
        }

        viewModel.categories.observe(viewLifecycleOwner) { categoryList ->
            if (categoryList != null) {
                (binding.rvCategories.adapter as? CategoryAdapter)?.updateData(categoryList)
            }
        }

        viewModel.isLoading.observe(viewLifecycleOwner) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        viewModel.errorMessage.observe(viewLifecycleOwner) { message ->
            if (message != null) {
                Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.favoriteIds.observe(viewLifecycleOwner) { favoriteIds ->
            adapter.updateFavorites(favoriteIds)
        }
    }

    // Xóa binding khi Fragment bị hủy để giải phóng bộ nhớ
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}