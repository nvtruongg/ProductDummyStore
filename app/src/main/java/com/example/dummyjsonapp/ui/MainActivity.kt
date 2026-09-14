package com.example.dummyjsonapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.dummyjsonapp.R
import com.example.dummyjsonapp.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

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

        // Nạp HomeFragment lần đầu nếu chưa có savedInstanceState
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, HomeFragment())
                .commit()
        }

        // Nav Bottom
        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, HomeFragment())
                        .commit()
                    true
                }
                R.id.nav_favorite -> {
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.fragmentContainer, FavoriteFragment())
                        .commit()
                    true
                }
                R.id.nav_cart -> {
                    Toast.makeText(this, "Giỏ hàng đang phát triển", Toast.LENGTH_SHORT).show()
                    true
                }
                R.id.nav_profile -> {
                    Toast.makeText(this, "Cá nhân đang phát triển", Toast.LENGTH_SHORT).show()
                    true
                }
                else -> false
            }
        }
    }
}