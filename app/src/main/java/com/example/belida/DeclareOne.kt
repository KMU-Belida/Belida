package com.example.belida

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.declare.*
import kotlinx.android.synthetic.main.declare_fragment1.*

class DeclareOne: AppCompatActivity() {
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.declare_fragment1)

        back_declare_btn1.setOnClickListener{
            finish()
        }
    }
}