package com.example.dummyjsonapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dummyjsonapp.databinding.FragmentFavoriteBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class FavoriteFragment : Fragment() {
    private var _binding: FragmentFavoriteBinding? = null
    private val binding get() = _binding!!

    private lateinit var adapter: ProductAdapter
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View {
        _binding = FragmentFavoriteBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

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
        binding.rvFavorites.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rvFavorites.adapter = adapter

        viewModel.loadFavoritedProductsList()
        viewModel.loadFavorites()
        observeViewModel()
    }
    private fun observeViewModel() {
        // Lắng nghe danh sách sản phẩm yêu thích (có chứa ảnh, tên, giá)
        viewModel.favoritedProductsList.observe(viewLifecycleOwner) { products ->
            adapter.submitList(products)

            if (products.isNullOrEmpty()) {
                binding.rvFavorites.visibility = View.GONE
                binding.layoutEmpty.visibility = View.VISIBLE
            } else {
                binding.rvFavorites.visibility = View.VISIBLE
                binding.layoutEmpty.visibility = View.GONE
            }
        }

        // Lắng nghe danh sách ID để cập nhật trạng thái icon trái tim
        viewModel.favoriteIds.observe(viewLifecycleOwner) { ids ->
            adapter.updateFavorites(ids)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}