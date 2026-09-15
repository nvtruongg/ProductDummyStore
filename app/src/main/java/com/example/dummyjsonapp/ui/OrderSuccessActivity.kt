package com.example.dummyjsonapp.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.dummyjsonapp.databinding.ActivityOrderSuccessBinding

class OrderSuccessActivity : AppCompatActivity() {
    private lateinit var binding: ActivityOrderSuccessBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityOrderSuccessBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Nhận mã đơn hàng từ màn Checkout truyền sang
        val orderCode = intent.getStringExtra("ORDER_CODE") ?: "N/A"
        binding.tvOrderSuccessCode.text = "Mã đơn hàng: $orderCode"

        // Xử lý nút Quay về trang chủ
        binding.btnContinueShopping.setOnClickListener {
            val intent = Intent(this, MainActivity::class.java)
            // Cờ này giúp xóa toàn bộ các màn hình Checkout/Detail đang mở đè lên nhau
            // Đưa người dùng về đúng màn hình MainActivity ban đầu
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }
    }
}