package com.example.belida

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.declare.*
import kotlinx.android.synthetic.main.declare_fragment3.*

class DeclareThree: AppCompatActivity() {
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.declare_fragment3)

        back_declare_btn3.setOnClickListener{
            finish()
        }
    }
}