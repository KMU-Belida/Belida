package com.example.belida

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.declare.*

class Declare : AppCompatActivity(){
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.declare)

        declare_btn1.setOnClickListener{
            val intent = Intent(this,DeclareOne::class.java)
            startActivity(intent)
        }

        declare_btn2.setOnClickListener {
            val intent = Intent(this, DeclareTwo::class.java)
            startActivity(intent)
        }

        declare_btn3.setOnClickListener {
            val intent = Intent(this, DeclareThree::class.java)
            startActivity(intent)
        }

        declare_btn4.setOnClickListener {
            val intent = Intent(this, DeclareFour::class.java)
            startActivity(intent)
        }
        back_declare_btn.setOnClickListener{
            finish()
        }
    }
}