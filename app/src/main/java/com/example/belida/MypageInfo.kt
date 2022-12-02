package com.example.belida

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton

class MypageInfo : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage_account)
        val infoBtn: AppCompatButton = findViewById(R.id.button)
        val passwordBtn: AppCompatButton = findViewById(R.id.button7)
        val withdrawBtn: AppCompatButton = findViewById(R.id.button8)
    }
}