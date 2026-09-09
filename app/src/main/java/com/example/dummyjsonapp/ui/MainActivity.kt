package com.example.dummyjsonapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.dummyjsonapp.R
import com.example.dummyjsonapp.databinding.ActivityMainBinding
import com.google.gson.Gson

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: ProductViewModel
    private lateinit var adapter: ProductAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        // 1. Khởi tạo ViewBinding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 2. Cài đặt RecyclerView
        adapter = ProductAdapter{ clickedProduct ->
            // phần này sẽ chạy khi có 1 item bị bấm vào
            val intent = Intent(this, DetailActivity::class.java)

            // Đóng gói đối tượng Product thành chuỗi JSON
            val productJson = Gson().toJson(clickedProduct)
            intent.putExtra("EXTRA_PRODUCT_JSON", productJson) // Nhét vào  Intent

            startActivity(intent)
        }
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 3. Khởi tạo ViewModel
        viewModel = ViewModelProvider(this)[ProductViewModel::class.java]

        // 4. Lắng nghe dữ liệu (Observe LiveData)
        observeViewModel()
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