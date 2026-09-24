package com.example.dummyjsonapp.presentation.checkout

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bumptech.glide.Glide
import com.example.dummyjsonapp.R
import com.example.dummyjsonapp.databinding.ActivityCheckoutBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

@AndroidEntryPoint
class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private val viewModel: CheckoutViewModel by viewModels()
    private val orderCode = "ORD-${System.currentTimeMillis().toString().takeLast(6)}"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupAddressSection()
        setupPaymentMethods()
        observeUiState()

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
                    generateVietQrCode(viewModel.uiState.value.finalTotal)
                }
            }
        }
    }

    private fun observeUiState() {
       lifecycleScope.launch {
           lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
               viewModel.uiState.collect { state ->
                   binding.tvSubtotal.text = String.format("$%.2f", state.subTotal)
                   binding.tvShippingFee.text = String.format("$%.2f", state.shippingFee)
                   binding.tvFinalTotal.text = String.format("$%.2f", state.finalTotal)
                   binding.btnPlaceOrder.text =
                       "Đặt hàng — " + String.format("$%.2f", state.finalTotal)

                   if (binding.rgPaymentMethods.checkedRadioButtonId == R.id.rbBanking) {
                       generateVietQrCode(state.finalTotal)
                   }
               }
            }
        }
    }

    private fun generateVietQrCode(finalTotal: Double) {
        if(finalTotal == 0.0) return

        val amountVnd = (finalTotal * 25000).roundToInt()
        val bankId = "mb"
        val accountNo = "0379996828"
        val accountName = "NGUYEN VIET TRUONG"

        val qrUrl = "https://img.vietqr.io/image/$bankId-$accountNo-compact2.png" +
                "?amount=$amountVnd" +
                "&addInfo=$orderCode" +
                "&accountName=$accountName"

        Glide.with(this)
            .load(qrUrl)
            .placeholder(R.drawable.outline_animated_images_24)
            .into(binding.ivQrCode)

        binding.tvBankingInfo.text = "Ngân hàng: MB\n" +
                "STK: $accountNo\n" +
                "Chủ TK: $accountName\n\n" +
                "Nội dung CK: $orderCode"
    }
}