package com.example.belida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.activity_category.*
import kotlinx.android.synthetic.main.home_page.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class Category : AppCompatActivity() {

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
        setContentView(R.layout.activity_category)

        val mAdapter = CustomAdapter(this,userList)
        gridRecyclerView1.adapter = mAdapter

        val gridLayoutManager = GridLayoutManager(applicationContext,3)
        gridRecyclerView1.layoutManager = gridLayoutManager

        back_category_btn.setOnClickListener {
            finish()
        }
//        var data:MutableList<ListData> = setData()
//        var adapter = CustomAdapter()
//        adapter.listData = data
//        gridRecyclerView1.adapter = adapter
//        gridRecyclerView1.layoutManager = LinearLayoutManager(this)
    }
//    fun setData():MutableList<ListData>{
//        var data:MutableList<ListData> = mutableListOf()
//        for(num in 1..10){
//            var cat_title = "${num}번째 타이틀"
//            var listdata = ListData(num,cat_title)
//            data.add(listdata)
//        }
//        return data
//    }
}