package com.example.belida

import android.content.Intent
import android.os.Bundle
import android.provider.ContactsContract.Data
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.example.belida.database.User
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.getValue
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
        if (userKey != null && checkNickNameBlank(nickName)) {
            userDB.child(userKey).child("userNickName").setValue("")
            checkNicknameDuplicate(nickName, userKey)
        }
    }

    fun checkNicknameDuplicate(nickName : String, userKey : String) {
        userDB.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    var isDuplicate = false
                    for (targetSnapshot in dataSnapshot.children) {
                        if(!targetSnapshot.getValue(User::class.java)?.userNickName.equals(nickName)) {
                            // println(targetSnapshot.getValue(User::class.java)?.userNickName + "중복X")
                            continue
                        } else {
                            // println(targetSnapshot.getValue(User::class.java)?.userNickName + "중복")
                            isDuplicate = true
                            break
                        }
                    }
                    if (!isDuplicate) {
                        Toast.makeText(applicationContext, "닉네임 등록 완료", Toast.LENGTH_SHORT).show()
                        userDB.child(userKey).child("userNickName").setValue(nickName)
                        moveMainPage()
                    } else {
                        Toast.makeText(applicationContext, "중복된 닉네임 존재.", Toast.LENGTH_SHORT).show()
                    }
                }
                override fun onCancelled(databaseError: DatabaseError) {
                    Toast.makeText(applicationContext,
                        databaseError.message,
                        Toast.LENGTH_SHORT).show()
                }
            })
    }

    fun checkNickNameBlank (nickName : String): Boolean {
        if (nickName.equals("")) {
            Toast.makeText(applicationContext, "닉네임을 입력해주세요", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    fun moveMainPage(){
        startActivity(Intent(this, NaviActivity::class.java))
    }
}