package com.example.belida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.home_page.*

class Category : AppCompatActivity() {
    lateinit var userKey: String
    lateinit var userLoginedNickName: String
    lateinit var userLoginedEmail: String

    private var userList = arrayListOf<DataVo>(
        DataVo("디지털기기",R.drawable.monitor),
        DataVo("스포츠",R.drawable.sport),
        DataVo("의류/잡화",R.drawable.clothes),
        DataVo("생활/주방",R.drawable.pot),
        DataVo("인테리어",R.drawable.furniture),
        DataVo("취미/게임",R.drawable.game),
        DataVo("동물용품",R.drawable.dog),
        DataVo("뷰티/미용",R.drawable.beauty),
        DataVo("유아용완구",R.drawable.child),
        DataVo("도서/음반",R.drawable.book),
        DataVo("식물",R.drawable.plant),
        DataVo("기타",R.drawable.etc)
    )
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category)

        userKey = intent.getStringExtra("UserKey").toString() // 현재 로그인한 userKey값
        userLoginedNickName = intent.getStringExtra("UserNickName").toString()
        userLoginedEmail = intent.getStringExtra("UserEmail").toString()

        val mAdapter = CustomAdapter(this,userList)
        gridRecyclerView1.adapter = mAdapter

        val gridLayoutManager = GridLayoutManager(applicationContext,3)
        gridRecyclerView1.layoutManager = gridLayoutManager

        home_btn1.setOnClickListener {
            val intent = Intent(this,HomePage::class.java)
            startActivity(intent)
        }
        category_btn1.setOnClickListener {
            val intent = Intent(this,Category::class.java)
            startActivity(intent)
        }

        back_category_btn.setOnClickListener {
            finish()
        }
        addbtn1.setOnClickListener {
            val intent = Intent(this,ImageEnroll::class.java)
            startActivity(intent)
        }
        chat_btn1.setOnClickListener {
            val intent = Intent(this,ChatListActivity::class.java)
            intent.putExtra("UserKey", userKey)
            intent.putExtra("UserNickName", userLoginedNickName)
            intent.putExtra("UserEmail", userLoginedEmail)
            startActivity(intent)
        }
        mypage_btn1.setOnClickListener {
            val intent = Intent(this,MypageActivity::class.java)
            startActivity(intent)
        }
    }

 }
