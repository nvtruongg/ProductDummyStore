package com.example.dummyjsonapp.ui

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import com.example.dummyjsonapp.R
import com.example.dummyjsonapp.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // 1. Khởi tạo ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val dummyCategories = listOf("Tất cả", "Trang điểm", "Nội thất", "Nước hoa", "Thời trang", "Thực phẩm")
        val categoryAdapter = CategoryAdapter(dummyCategories)
        // Cài đặt lướt ngang
        binding.rvCategories.layoutManager = androidx.recyclerview.widget.LinearLayoutManager(
            this, androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL, false
        )
        binding.rvCategories.adapter = categoryAdapter

        //sự kiện ô tìm kiếm
        binding.edtSearch.setOnEditorActionListener { v, actionId, event ->
            val keyword = binding.edtSearch.text.toString()
            if(keyword.isNotEmpty()){
                android.widget.Toast.makeText(this, "Đang tìm kiếm $keyword", android.widget.Toast.LENGTH_SHORT).show()
                //gắn api tim kiếm json
            }
            // ẩn bàn phím khi nhập xong
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
                imm.hideSoftInputFromWindow(binding.edtSearch.windowToken, 0)
                return@setOnEditorActionListener true
            }
            false
        }

        // 2. Cài đặt RecyclerView
        adapter = ProductAdapter{ clickedProduct ->
            // phần này sẽ chạy khi có 1 item bị bấm vào
            val intent = Intent(this, DetailActivity::class.java)

            // Đóng gói đối tượng Product thành chuỗi JSON
            val productJson = Gson().toJson(clickedProduct)
            intent.putExtra("EXTRA_PRODUCT_JSON", productJson) // Nhét vào  Intent

            startActivity(intent)
        }
        binding.recyclerView.layoutManager = GridLayoutManager(this, 2)
        binding.recyclerView.adapter = adapter

        // 3. Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        // 4. Lắng nghe dữ liệu (Observe LiveData)
        observeViewModel()

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