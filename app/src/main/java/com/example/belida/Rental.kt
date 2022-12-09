package com.example.belida

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.belida.database.Message
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_rental.*
import java.util.*

class Rental : Activity() {
    var dateString = ""

    lateinit var receiverNickName: String
    lateinit var receiverEmail: String
    lateinit var senderNickName: String
    lateinit var senderEmail: String
    lateinit var startDate: String
    lateinit var endDate: String
    lateinit var mDbRef: DatabaseReference

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_rental)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mDbRef = Firebase.database.reference

        receiverNickName = intent.getStringExtra("ReceiverNickName").toString()
        receiverEmail = intent.getStringExtra("ReceiverEmail").toString()
        senderNickName = intent.getStringExtra("SenderNickName").toString()
        senderEmail = intent.getStringExtra("SenderEmail").toString()

        // 현재 로그인한 유저 대화방의 변수
        val senderRoom = receiverNickName + senderNickName

        // 상대방 대화방의 변수
        val receiverRoom = senderNickName + receiverNickName

        val opponentName: TextView = findViewById(R.id.opponentName)
        opponentName.text = receiverNickName

        date_start_text.setOnClickListener {
            val cal = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    dateString = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                    startDate = dateString
                    date_start_text.text = dateString
                }
            DatePickerDialog(
                this,
                dateSetListener,
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        date_end_text.setOnClickListener {
            val cal_2 = Calendar.getInstance()    //캘린더뷰 만들기
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    dateString = "${year}년 ${month + 1}월 ${dayOfMonth}일"
                    endDate = dateString
                    date_end_text.text = dateString
                }
            DatePickerDialog(
                this,
                dateSetListener,
                cal_2.get(Calendar.YEAR),
                cal_2.get(Calendar.MONTH),
                cal_2.get(Calendar.DAY_OF_MONTH)
            ).show()
        }

        val reservationTokenEdit: EditText = findViewById(R.id.reservation_token)
        val depositTokenEdit: EditText = findViewById(R.id.deposit_token)

        btnCancel.setOnClickListener {
            //취소 버튼
            finish()
        }
        btnSave.setOnClickListener {

            val reservationToken = reservationTokenEdit.text.toString()
            val depositToken = depositTokenEdit.text.toString()
            //보내기 버튼
            val message = "${receiverNickName}님께 \n" +
                    "대여신청서를 보냈어요 \n" +
                    "대여기간 : " + "${startDate} ~ ${endDate} \n" +
                    "예약금 : " + "${reservationToken} 벨리 \n" +
                    "보증금 : " + "${depositToken} 벨리"

            // data class에 넣어서 DB에 삽입
            val messageObject = Message(message, senderEmail)

            mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    // 삽입 성공했을 경우
                    mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }

            val message2 = "${senderNickName}님께서 \n" +
                    "대여신청서를 보냈어요 \n" +
                    "대여기간 : " + "$startDate ~ ${endDate} \n" +
                    "예약금 : " + "${reservationToken} 벨리 \n" +
                    "보증금 : " + "${depositToken} 벨리"

            // data class에 넣어서 DB에 삽입
            val messageObject2 = Message(message2, receiverEmail)

            mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
                .setValue(messageObject2).addOnSuccessListener {
                    // 삽입 성공했을 경우
                    mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                        .setValue(messageObject2)
                }

            val confirmIntent = Intent(this, RentalConfirm::class.java)
            confirmIntent.putExtra("SenderNickName", senderNickName)
            confirmIntent.putExtra("ReceiverNickName", receiverNickName)
            confirmIntent.putExtra("SenderEmail", senderEmail)
            confirmIntent.putExtra("ReceiverEmail", receiverEmail)
            confirmIntent.putExtra("ReservationToken", reservationToken)
            confirmIntent.putExtra("DepositToken", depositToken)

            Handler(Looper.getMainLooper()).postDelayed({
                //실행할 코드
                startActivity(confirmIntent)
            }, 1500)

            finish()
        }
    }
}