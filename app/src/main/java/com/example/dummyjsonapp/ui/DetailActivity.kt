package com.example.dummyjsonapp.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.dummyjsonapp.databinding.ActivityDetailBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val viewModel: ProductViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Ẩn navigation bar
        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        // lấy dữ liệu từ intent
        val product_id = intent.getIntExtra("product_id", -1)
        if(product_id != -1){
            viewModel.loadProductDetail(product_id)
        }

        viewModel.selectedProduct.observe(this){ product ->
            if(product != null) {
                // đổ dữ liệu cơ bản
                binding.tvDetailTitle.text = "${product.brand ?: ""} ${product.title}".trim()
                val originalPrice = product.price / (1 - product.discountPercentage / 100)
                binding.tvDetailPrice.text =
                    "$${product.price} (Giảm ${product.discountPercentage}%)"
                binding.tvOriginalPrice.text = "$${String.format("%.2f", originalPrice)}"
                binding.tvOriginalPrice.paintFlags =
                    binding.tvOriginalPrice.paintFlags or android.graphics.Paint.STRIKE_THRU_TEXT_FLAG
                binding.tvDetailRating.text = "⭐ ${product.rating}/5 | Kho: ${product.stock}"
                binding.tvDescription.text = product.description

                // Tổng hợp các thông số kỹ thuật và độ bền thành một chuỗi
                val specsBuilder = StringBuilder()
                specsBuilder.append("• Thương hiệu: ${product.brand ?: "Đang cập nhật"}\n")
                specsBuilder.append("• Trọng lượng: ${product.weight ?: 0}g\n")

                product.dimensions?.let {
                    specsBuilder.append("• Kích thước (R-C-S): ${it.width} x ${it.height} x ${it.depth} cm\n")
                }

                specsBuilder.append("• Bảo hành: ${product.warrantyInformation ?: "Không hỗ trợ"}\n")
                specsBuilder.append("• Đổi trả: ${product.returnPolicy ?: "Không hỗ trợ"}")

                binding.tvTechnicalSpecs.text = specsBuilder.toString()

                // Hiển thị bình luận
                if (!product.reviews.isNullOrEmpty()) {
                    val reviewText = product.reviews.joinToString("\n\n") { rev ->
                        "${rev.rating} ⭐ - ${rev.reviewerName}\n\"${rev.comment}\""
                    }
                    binding.tvReview.text = reviewText
                } else {
                    binding.tvReview.text = "Chưa có đánh giá nào."
                }


                //trượt ảnh
                if (product.images.isNotEmpty()) {
                    val sliderAdapter = ImageSliderAdapter(product.images)
                    binding.viewPagerImages.adapter = sliderAdapter
                }

                binding.btnBack.setOnClickListener {
                    finish()
                }
            }
        }
        binding.btnAddCart.setOnClickListener {
            if (product_id != -1) {
                viewModel.addToCart(product_id)
                Toast.makeText(this, "Đã thêm vào giỏ hàng!", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnBuyNow.setOnClickListener {
            if (product_id != -1) {
                viewModel.addToCart(product_id)
                Toast.makeText(this, "Chuyển đến giỏ hàng...", Toast.LENGTH_SHORT).show()
                // TODO: Mở màn hình Giỏ hàng hoặc Thanh toán sau
                finish() // Tạm thời đóng màn detail lại
            }
        }
        oserveViewModel()
    }

    private fun oserveViewModel() {
        viewModel.errorMessage.observe(this) { message ->
            if (message != null) {
                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
            }
        }
    }
}