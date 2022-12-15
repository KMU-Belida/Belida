package com.example.belida

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatImageButton
import com.example.belida.database.User
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_category.*

class MypageActivity : AppCompatActivity() {
    val database = Firebase.database
    val userDB = database.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage)

        val userKey = intent.getStringExtra("UserKey").toString() // 현재 로그인한 userKey값

        val accountInfoBtn: AppCompatImageButton = findViewById(R.id.imageButton3)
        val myPostBtn: AppCompatButton = findViewById(R.id.button2)
        val myBorrowBtn: AppCompatButton = findViewById(R.id.button3)
        val myLocationBtn: AppCompatButton = findViewById(R.id.button4)
        accountInfoBtn.setOnClickListener {
            moveMypageInfo(userKey)
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
        setUserInformationAndDisplay(userKey)
        home_btn1.setOnClickListener {
            val intent = Intent(this,HomePage::class.java)
            startActivity(intent)
        }
        category_btn1.setOnClickListener {
            val intent = Intent(this,Category::class.java)
            startActivity(intent)
        }

        addbtn1.setOnClickListener {
            val intent = Intent(this,ImageEnroll::class.java)
            startActivity(intent)
        }

        chat_btn1.setOnClickListener {
            val intent = Intent(this,ChatListActivity::class.java)
            startActivity(intent)
        }
    }

    fun moveMypageInfo(userKey: String) {
        val intent = Intent(this, MypageInfo::class.java)
        intent.putExtra("UserKey", userKey)
        startActivity(intent)
    }

    fun moveMyPostPage() {
        //내가 올린 물품
    }

    fun moveMyBorrowPage() {
        //내가 대여한 물품
    }

    fun moveLocationPage() {
        val intent = Intent(this, LocationActivity::class.java)
        startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
        finish()
    }

    fun setUserInformationAndDisplay(userKey: String) {
        // 화면에 띄울 유저 정보 값
        val userNickname: TextView = findViewById(R.id.user_nickname)
        val userLocation: TextView = findViewById(R.id.user_location)
        val belidaToken: TextView = findViewById(R.id.user_token)

        userDB.get().addOnSuccessListener {
            userNickname.text =
                it.child(userKey).getValue(User::class.java)?.userNickName.toString()
            userLocation.text =
                it.child(userKey).getValue(User::class.java)?.userLocation.toString()
            belidaToken.text =
                it.child(userKey).getValue(User::class.java)?.belidaToken.toString()
        }
    }
}