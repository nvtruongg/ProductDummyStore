package com.example.dummyjsonapp.presentation.language

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.os.LocaleListCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.example.dummyjsonapp.core.preference.PrefManager
import com.example.dummyjsonapp.databinding.ActivityLanguageBinding
import com.example.dummyjsonapp.presentation.onboarding.OnboardingActivity
import com.example.dummyjsonapp.presentation.splash.SplashActivity

class LanguageActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLanguageBinding
    private lateinit var prefManager: PrefManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLanguageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.hide(WindowInsetsCompat.Type.navigationBars())
        insetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE

        prefManager = PrefManager(this)
        binding.btnVietnamese.setOnClickListener {
            changeLanguage("vi")
        }
        binding.btnEnglish.setOnClickListener {
            changeLanguage("en")
        }
    }
    private fun changeLanguage(languageCode: String) {
        prefManager.languageCode = languageCode

        val appLocale = LocaleListCompat.forLanguageTags(languageCode)
        AppCompatDelegate.setApplicationLocales(appLocale)

        val intent = if(prefManager.isFirstTimeLauncher){
            Intent(this, OnboardingActivity::class.java)
        }
        else{
            Intent(this, SplashActivity::class.java)
        }

        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }
}