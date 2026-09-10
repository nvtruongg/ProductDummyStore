package com.example.dummyjsonapp

import android.content.Context

class PrefManager(context: Context) {
    private val prefs = context.getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)

    //lưu trạng thái mở app lần đầu
    var isFirstTimeLauncher : Boolean
        get() = prefs.getBoolean("isFirstTimeLauncher", true)
        set(value) = prefs.edit().putBoolean("isFirstTimeLauncher", value).apply()

    // lưu ngôn ngữ mặc định
    var languageCode : String
        get() = prefs.getString("languageCode", "vi") ?: "vi"
        set(value) = prefs.edit().putString("languageCode", value).apply()
}