package com.example.belida

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatButton
import com.example.belida.database.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import org.w3c.dom.Text

class MypageInfo : AppCompatActivity() {
    val database = Firebase.database
    val userDB = database.getReference("user")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mypage_account)

        val infoBtn: AppCompatButton = findViewById(R.id.button)
        val passwordBtn: AppCompatButton = findViewById(R.id.button7)
        val withdrawBtn: AppCompatButton = findViewById(R.id.button8)

        val userKey = intent.getStringExtra("UserKey").toString() // 데이터베이스에 저장된 유저Key값
        setUserInformationAndDisplay(userKey)
    }

    fun setUserInformationAndDisplay(userKey : String) {
        // 화면에 띄울 유저 정보 값
        val userNickname : TextView = findViewById((R.id.account_nickname))
        val userName : TextView = findViewById((R.id.account_name))
        val userEmail : TextView = findViewById(R.id.account_email)
        val userLocation : TextView = findViewById(R.id.account_location)

        userDB.get().addOnSuccessListener {
            userNickname.text =
                it.child(userKey).getValue(User::class.java)?.userNickName.toString()
            userName.text = it.child(userKey).getValue(User::class.java)?.userName.toString()
            userEmail.text = it.child(userKey).getValue(User::class.java)?.userEmail.toString()
            userLocation.text =
                it.child(userKey).getValue(User::class.java)?.userLocation.toString()
        }
    }
}