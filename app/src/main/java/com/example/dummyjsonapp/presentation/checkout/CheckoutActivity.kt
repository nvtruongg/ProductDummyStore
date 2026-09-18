package com.example.dummyjsonapp.presentation.checkout

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
    private val viewModel: CheckoutViewModel by viewModels()
    private var currentFinalTotalUsd: Double = 0.0
    private val orderCode = "ORD-${System.currentTimeMillis().toString().takeLast(6)}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAddressSection()

        setupPaymentMethods()

        observeViewModel()

        viewModel.loadCart()

        binding.btnPlaceOrder.setOnClickListener {
            viewModel.placeOrder()
            val intent = Intent(this, OrderSuccessActivity::class.java)
            intent.putExtra("ORDER_CODE", orderCode)
            startActivity(intent)

            finish()
        }
    }

    private fun setupAddressSection() {
        // dữ liệu người dùng giả lập
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
                    binding.layoutBankingDetails.visibility = View.GONE
                }
                R.id.rbBanking -> {
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

            binding.tvSubtotal.text = String.format("$%.2f", subtotal)
            binding.tvShippingFee.text = String.format("$%.2f", shippingFee)
            binding.tvFinalTotal.text = String.format("$%.2f", currentFinalTotalUsd)

            binding.btnPlaceOrder.text = "Đặt hàng — " + String.format("$%.2f", currentFinalTotalUsd)

            if (binding.rgPaymentMethods.checkedRadioButtonId == R.id.rbBanking) {
                generateVietQrCode()
            }
        }
    }

    private fun generateVietQrCode() {
        val amountVnd = (currentFinalTotalUsd * 25000).roundToInt()

        val bankId = "mb"
        val accountNo = "0379996828"
        val accountName = "NGUYEN VIET TRUONG"

        val qrUrl = "https://img.vietqr.io/image/$bankId-$accountNo-compact2.png" +
                "?amount=$amountVnd" +
                "&addInfo=$orderCode" +
                "&accountName=$accountName"

        Glide.with(this)
            .load(qrUrl)
            .placeholder(android.R.drawable.ic_menu_gallery)
            .into(binding.ivQrCode)

        binding.tvBankingInfo.text = "Ngân hàng: MB\n" +
                "STK: $accountNo\n" +
                "Chủ TK: $accountName\n\n" +
                "Nội dung CK: $orderCode"
    }
}