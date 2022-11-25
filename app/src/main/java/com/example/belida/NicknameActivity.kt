package com.example.belida

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.belida.database.User
import kotlinx.android.synthetic.main.activity_nickname.*

class NicknameActivity : AppCompatActivity() {
    private val database = Firebase.database
    private val userDB = database.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_nickname)
        val nickNameRegisterButton: Button = findViewById(R.id.nickname_next_btn)
        // 닉네임 등록 버튼을 눌렀을 경우
        nickNameRegisterButton.setOnClickListener {
            pushNicknameDB()
        }
    }

    // 닉네임 값 DB에 넣기
    fun pushNicknameDB() {
        val userKey = intent.getStringExtra("UserKey")
        val nickNameEdit: EditText = findViewById(R.id.nickname)
        val nickName = nickNameEdit.text.toString()
        if (userKey != null) {
            userDB.child(userKey).child("userNickName").setValue(nickName)
        }
    }
}