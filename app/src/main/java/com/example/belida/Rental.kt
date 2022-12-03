package com.example.belida
import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_event.*
import kotlinx.android.synthetic.main.dialog_rental.*
import java.util.*

class Rental : Activity() {
    var dateString = ""
    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_rental)

        date_start_text.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                dateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                date_start_text.text = dateString
            }
            DatePickerDialog(this, dateSetListener, cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH)).show()
        }

        date_end_text.setOnClickListener {
            val cal_2 = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener = DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                dateString = "${year}년 ${month+1}월 ${dayOfMonth}일"
                date_end_text.text = dateString
            }
            DatePickerDialog(this, dateSetListener, cal_2.get(Calendar.YEAR),cal_2.get(Calendar.MONTH),cal_2.get(Calendar.DAY_OF_MONTH)).show()
        }

        btnCancle.setOnClickListener {
            //취소 버튼
            finish()
        }
        btnSave.setOnClickListener {
            //보내기 버튼
            finish()
        }
    }
}