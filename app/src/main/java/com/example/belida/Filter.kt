package com.example.belida

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.activity_filter.*

class Filter : AppCompatActivity() {

    private var userList = arrayListOf<DataVo>(
        DataVo("디지털기기",R.drawable.monitor),
        DataVo("레저/스포츠",R.drawable.sport),
        DataVo("의류/잡화",R.drawable.clothes),
        DataVo("생활/주방",R.drawable.pot),
        DataVo("가구/인테리어",R.drawable.furniture),
        DataVo("취미/악기/게임",R.drawable.game),
        DataVo("반려동물용품",R.drawable.dog),
        DataVo("뷰티/미용",R.drawable.beauty),
        DataVo("유아용완구",R.drawable.child),
        DataVo("도서/음반",R.drawable.book),
        DataVo("식물",R.drawable.plant),
        DataVo("기타",R.drawable.etc)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter)

        back_filter_btn.setOnClickListener {
            finish()
        }

        val mAdapter1 = CustomAdapter(this,userList)
        gridRecyclerView2.adapter = mAdapter1

        val gridLayoutManager = GridLayoutManager(applicationContext,4)
        gridRecyclerView2.layoutManager = gridLayoutManager

    }
}