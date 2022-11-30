package com.example.belida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import kotlinx.android.synthetic.main.activity_home_search.*
import kotlinx.android.synthetic.main.activity_home_search.category_btn
import kotlinx.android.synthetic.main.activity_home_search.home_btn
import kotlinx.android.synthetic.main.home_page.*

class HomeSearch : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search)

        filter_btn.setOnClickListener {
            val intent = Intent(this,Filter::class.java)
            startActivity(intent)
        }
        home_btn.setOnClickListener {
            val intent = Intent(this,HomePage::class.java)
            startActivity(intent)
        }
        category_btn.setOnClickListener {
            val intent = Intent(this,Category::class.java)
            startActivity(intent)
        }
    }
}