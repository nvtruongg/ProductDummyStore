package com.example.dummyjsonapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dummyjsonapp.R
import com.example.dummyjsonapp.databinding.ActivityMainBinding
import com.google.gson.Gson
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ProductAdapter
    private val viewModel: ProductViewModel by viewModels()
    private var searchJob : Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Khởi tạo ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Lấy controller để điều khiển system bars
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        // Ẩn navigation bar
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        // Tuỳ chọn: cho phép người dùng vuốt để hiện lại
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        //2. Categories
        val categoryAdapter = CategoryAdapter(emptyList()){ clickedCategory ->
            // Khi bấm vào 1 danh mục -> Gọi ViewModel lọc danh sách
            viewModel.filterByCategory(clickedCategory.slug)
        }

        //2.1 Cài đặt lướt ngang
        binding.rvCategories.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvCategories.adapter = categoryAdapter

        //3. sự kiện ô tìm kiếm
        binding.edtSearch.addTextChangedListener{
            searchJob?.cancel()
            searchJob = lifecycleScope.launch{
                delay(500)
                var keyword = binding.edtSearch.text.toString().trim()
                if(keyword.isNotEmpty()){
                    viewModel.searchProducts(keyword)
                } else{
                    viewModel.loadData()
                }
            }
        }
        binding.edtSearch.setOnEditorActionListener { v, actionId, event ->
            // ẩn bàn phím khi nhập xong
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
                return@setOnEditorActionListener true
            }
            binding.edtSearch.clearFocus() // Bỏ focus ở ô search
            false
        }

        //4. Cài đặt RecyclerView
        adapter = ProductAdapter{ clickedProduct ->
            // phần này sẽ chạy khi có 1 item bị bấm vào
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("product_id", clickedProduct.id)
            // Nhét vào  Intent
            startActivity(intent)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter


        // 5. Lắng nghe dữ liệu (Observe LiveData)
        observeViewModel()

        //nav_bottom
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when(item.itemId){
                R.id.nav_home -> {
                    true
                }
                R.id.nav_cart -> {
                    android.widget.Toast.makeText(this, "Giỏ hàng đang phát triển", android.widget.Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    android.widget.Toast.makeText(this, "Cá nhân đang phát triển", android.widget.Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
    private fun observeViewModel() {
        // observe: chờ & theo dõi -> danh sách sản phẩm
        viewModel.products.observe(this) { productList ->
            if (productList != null) {
                // đẩy danh sách mới vào Adapter (Tự động DiffUtil xử lý)
                adapter.submitList(productList)
                if (productList.isEmpty()) {
                    // Nếu rỗng: Ẩn danh sách, Hiện thông báo
                    binding.recyclerView.visibility = View.GONE
                    binding.tvEmptyMessage.visibility = View.VISIBLE
                } else {
                    // Nếu có dữ liệu: Hiện danh sách, Ẩn thông báo
                    binding.recyclerView.visibility = View.VISIBLE
                    binding.tvEmptyMessage.visibility = View.GONE
                }
            }
        }
        // Lắng nghe danh sách Danh mục từ API
        viewModel.categories.observe(this) { categoryList ->
            if (categoryList != null) {
                // Bơm dữ liệu mới vào Adapter
                (binding.rvCategories.adapter as? CategoryAdapter)?.updateData(categoryList)
            }
        }

        // observe trạng thái tải để hiện/ẩn xoay xoay
        viewModel.isLoading.observe(this) { isLoading ->
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }

        // observe lỗi để báo cho người dùng
        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}