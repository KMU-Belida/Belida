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
import kotlinx.android.synthetic.main.dialog_report.*
import java.util.*

class Report : Activity() {
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_report)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        report_cancel_btn.setOnClickListener {
            //취소 버튼
            finish()
        }
        report_save_btn.setOnClickListener {
            //보내기 버튼
            finish()
        }
    }
}