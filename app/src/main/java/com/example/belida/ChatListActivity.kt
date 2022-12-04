package com.example.belida

import android.accessibilityservice.GestureDescription.StrokeDescription
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.belida.database.User
import com.example.belida.databinding.ActivityChatlistBinding
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.GetTokenResult
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.suspendCoroutine

class ChatListActivity : AppCompatActivity() {
    val database = Firebase.database
    val userDB = database.getReference("user")

    lateinit var binding: ActivityChatlistBinding // 바인딩 객체
    lateinit var adapter: UserAdapter // 어댑터 객체

    // private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference // 데이터베이스 객체
    private lateinit var userList: ArrayList<User> // 데이터를 담을 리스트

    // 현재 로그인한 유저의 정보
    lateinit var userKey: String
    lateinit var userLoginedName: String
    lateinit var userLoginedEmail: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatlistBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //인증 초기화
        // mAuth = Firebase.auth

        // 현재 로그인한 유저
        userKey = intent.getStringExtra("UserKey").toString()
        userLoginedName = intent.getStringExtra("UserName").toString()
        userLoginedEmail = intent.getStringExtra("UserEmail").toString()

        //db 초기화
        mDbRef = Firebase.database.reference

        //리스트 초기화
        userList = ArrayList()

        // adapter 초기화
        adapter = UserAdapter(this, userList, userLoginedEmail, userLoginedName)

        // layout은 LinearLayout으로 설정
        binding.userRecycelrView.layoutManager = LinearLayoutManager(this)
        // adpater는 Useradapter로 설정
        binding.userRecycelrView.adapter = adapter

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    //유저 정보
                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (userLoginedEmail != currentUser?.userEmail) {
                        userList.add(currentUser!!)
                    }
                }
                adapter.notifyDataSetChanged()
            }
            override fun onCancelled(error: DatabaseError) {
                //실패 시 실행
            }
        })
    }
}