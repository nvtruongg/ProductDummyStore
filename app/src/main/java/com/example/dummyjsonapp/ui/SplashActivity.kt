package com.example.dummyjsonapp.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.dummyjsonapp.PrefManager
import com.example.dummyjsonapp.databinding.ActivitySplashBinding

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(binding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        var prefManager = PrefManager(this)

        Handler(Looper.getMainLooper()).postDelayed({
            if(prefManager.isFirstTimeLauncher){
                startActivity(Intent(this, LanguageActivity::class.java))
            }
            else{
                startActivity(Intent(this, MainActivity::class.java))
            }
            finish()
        }, 2000)
    }
}