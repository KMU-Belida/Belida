package com.example.belida

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton

class MypageActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)
        val accountInfoBtn: AppCompatImageButton = findViewById(R.id.imageButton3)
        val myPostBtn: AppCompatButton = findViewById(R.id.button2)
        val myBorrowBtn: AppCompatButton = findViewById(R.id.button3)
        val myLocationBtn: AppCompatButton = findViewById(R.id.button4)
        accountInfoBtn.setOnClickListener{
            moveMypageInfo()
        }
        myPostBtn.setOnClickListener {
            moveMyPostPage()
        }
        myBorrowBtn.setOnClickListener {
            moveMyBorrowPage()
        }
        myLocationBtn.setOnClickListener {
            moveLocationPage()
        }

    }
    fun moveMypageInfo(){
        startActivity(Intent(this, MypageInfo::class.java))
    }
    fun moveMyPostPage(){
        //내가 올린 물품
    }
    fun moveMyBorrowPage(){
        //내가 대여한 물품
    }
    fun moveLocationPage(){
        val intent = Intent(this, LocationActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }
}