package com.example.belida

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.item_enroll.*

class ImageEnroll : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_enroll)

        declare_btn.setOnClickListener {
            val intent = Intent(this, Declare::class.java)
            startActivity(intent)
        }
        option1_btn.setOnClickListener {
            val intent = Intent(this,OptionFragment::class.java)
            startActivity(intent)
        }
        option2_btn.setOnClickListener {
            val intent = Intent(this,OptionFragment2::class.java)
            startActivity(intent)
        }
    }
}