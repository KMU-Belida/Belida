package com.example.belida

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.option1_fragment.*

class OptionFragment : AppCompatActivity(){
     override fun onCreate(savedInstanceState:Bundle?){
         super.onCreate(savedInstanceState)
         setContentView(R.layout.option1_fragment)

         option1_close_btn.setOnClickListener {
             finish()
         }
    }
}