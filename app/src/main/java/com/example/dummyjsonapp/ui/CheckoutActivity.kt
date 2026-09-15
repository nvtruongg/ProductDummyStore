package com.example.dummyjsonapp.ui

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.example.dummyjsonapp.R
import com.example.dummyjsonapp.databinding.ActivityCheckoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlin.math.roundToInt

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private val viewModel: ProductViewModel by viewModels()

    // Lưu lại tổng tiền cuối cùng (đã cộng phí ship)
    private var currentFinalTotalUsd: Double = 0.0

    // Tạo sẵn một mã đơn hàng duy nhất cho phiên thanh toán này
    private val orderCode = "ORD-${System.currentTimeMillis().toString().takeLast(6)}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 1. Khởi tạo dữ liệu giả lập cho phần Địa chỉ (Mock Data)
        setupAddressSection()

        // 2. Thiết lập logic chọn Phương thức thanh toán
        setupPaymentMethods()

        // 3. Lắng nghe dữ liệu Giỏ hàng từ ViewModel
        observeViewModel()

        // Yêu cầu load lại giỏ hàng để chắc chắn giá trị là mới nhất
        viewModel.loadCart()

        // 4. Xử lý nút Đặt hàng
        binding.btnPlaceOrder.setOnClickListener {
            viewModel.placeOrder()

            // 2. Chuyển sang màn hình Thành công, mang theo mã đơn hàng
            val intent = Intent(this, OrderSuccessActivity::class.java)
            intent.putExtra("ORDER_CODE", orderCode)
            startActivity(intent)

            // 3. Đóng màn hình Checkout này lại
            finish()
        }
    }

    private fun setupAddressSection() {
        // Tự động điền dữ liệu người dùng (Giả lập)
        binding.tvCustomerInfo.text = "Nguyễn Viết Trường | (+84) 987-xxx-xxx"
        binding.tvShippingAddress.text = "Hà Nội"

        binding.btnChangeAddress.setOnClickListener {
            Toast.makeText(this, "Chức năng đổi địa chỉ đang phát triển", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupPaymentMethods() {
        binding.rgPaymentMethods.setOnCheckedChangeListener { _, checkedId ->
            when (checkedId) {
                R.id.rbCOD -> {
                    // Ẩn khối QR nếu chọn COD
                    binding.layoutBankingDetails.visibility = View.GONE
                }
                R.id.rbBanking -> {
                    // Hiện khối QR và tiến hành tạo ảnh QR
                    binding.layoutBankingDetails.visibility = View.VISIBLE
                    generateVietQrCode()
                }
            }
        }
    }

    private fun observeViewModel() {
        viewModel.totalPrice.observe(this) { subtotal ->
            // Giả định phí ship là $2.00
            val shippingFee = 2.00
            currentFinalTotalUsd = subtotal + shippingFee

            // Cập nhật giao diện Tóm tắt đơn hàng
            binding.tvSubtotal.text = String.format("$%.2f", subtotal)
            binding.tvShippingFee.text = String.format("$%.2f", shippingFee)
            binding.tvFinalTotal.text = String.format("$%.2f", currentFinalTotalUsd)

            // Cập nhật nội dung nút Đặt hàng (CTA)
            binding.btnPlaceOrder.text = "Đặt hàng — " + String.format("$%.2f", currentFinalTotalUsd)

            // Nếu người dùng đang chọn sẵn Tab Banking mà giá thay đổi (ví dụ áp dụng voucher) -> Render lại QR
            if (binding.rgPaymentMethods.checkedRadioButtonId == R.id.rbBanking) {
                generateVietQrCode()
            }
        }
    }

    private fun generateVietQrCode() {
        // Vì DummyJSON dùng USD, chúng ta quy đổi tạm sang VNĐ (tỉ giá giả định 25.000đ) để QR quét được
        val amountVnd = (currentFinalTotalUsd * 25000).roundToInt()

        // Thông tin ngân hàng của shop
        val bankId = "mb"
        val accountNo = "0379996828"
        val accountName = "NGUYEN VIET TRUONG"

        // API VietQR (Compact2 là template hiển thị logo ngân hàng + mã QR gọn gàng)
        val qrUrl = "https://img.vietqr.io/image/$bankId-$accountNo-compact2.png" +
                "?amount=$amountVnd" +
                "&addInfo=$orderCode" +
                "&accountName=$accountName"

        // Load QR Code bằng Glide
        Glide.with(this)
            .load(qrUrl)
            .placeholder(android.R.drawable.ic_menu_gallery) // Ảnh hiển thị tạm trong lúc load mạng
            .into(binding.ivQrCode)

        // Cập nhật Text hướng dẫn bên dưới mã QR
        binding.tvBankingInfo.text = "Ngân hàng: MB\n" +
                "STK: $accountNo\n" +
                "Chủ TK: $accountName\n\n" +
                "Nội dung CK: $orderCode"
    }
}