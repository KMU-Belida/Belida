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
import kotlinx.android.synthetic.main.dialog_rental.*
import kotlinx.android.synthetic.main.dialog_return.*
import java.util.*

class Return : Activity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_return)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        return_cancel_btn.setOnClickListener {
            //취소 버튼
            finish()
        }
        return_save_btn.setOnClickListener {
            //보내기 버튼
            finish()
        }
    }
}