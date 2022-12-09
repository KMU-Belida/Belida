package com.example.belida

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.dialog_chat_rating.*
import java.util.*

class Rating : Activity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_chat_rating)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        rating_cancel_btn.setOnClickListener {
            //취소 버튼
            finish()
        }
        rating_save_btn.setOnClickListener {
            //보내기 버튼
            finish()
        }
    }
}