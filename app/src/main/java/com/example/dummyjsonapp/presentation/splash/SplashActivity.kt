package com.example.dummyjsonapp.presentation.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.dummyjsonapp.core.preference.PrefManager
import com.example.dummyjsonapp.databinding.ActivitySplashBinding
import com.example.dummyjsonapp.presentation.main.MainActivity
import com.example.dummyjsonapp.presentation.language.LanguageActivity

class SplashActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySplashBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

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