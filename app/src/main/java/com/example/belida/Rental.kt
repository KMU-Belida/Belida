package com.example.belida

import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.belida.database.Message
import com.example.belida.database.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.getValue
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_rental.*
import java.util.*

class Rental : Activity() {
    var dateString = ""
//    val database = Firebase.database
//    val userDB = database.getReference("user")

    lateinit var startDate: String
    lateinit var endDate: String
    lateinit var mDbRef: DatabaseReference
    lateinit var reservationToken: String
    lateinit var depositToken: String
    lateinit var receiverNickName: String
    lateinit var receiverEmail: String
    lateinit var senderNickName: String
    lateinit var senderEmail: String

//    lateinit var userKey: String

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_rental)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mDbRef = Firebase.database.reference

//        userKey = intent.getStringExtra("UserKey").toString()

        receiverNickName = intent.getStringExtra("ReceiverNickName").toString()
        receiverEmail = intent.getStringExtra("ReceiverEmail").toString()
        senderNickName = intent.getStringExtra("SenderNickName").toString()
        senderEmail = intent.getStringExtra("SenderEmail").toString()

        // ?????? ???????????? ?????? ???????????? ??????
        val senderRoom = receiverNickName + senderNickName

        // ????????? ???????????? ??????
        val receiverRoom = senderNickName + receiverNickName

        val opponentName: TextView = findViewById(R.id.opponentName)
        opponentName.text = receiverNickName

        date_start_text.setOnClickListener {
            val cal = Calendar.getInstance()    //???????????? ?????????
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    dateString = "${year}??? ${month + 1}??? ${dayOfMonth}???"
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
            val cal_2 = Calendar.getInstance()    //???????????? ?????????
            val dateSetListener =
                DatePickerDialog.OnDateSetListener { view, year, month, dayOfMonth ->
                    dateString = "${year}??? ${month + 1}??? ${dayOfMonth}???"
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
            //?????? ??????
            finish()
        }
        btnSave.setOnClickListener {
            reservationToken = reservationTokenEdit.text.toString()
            depositToken = depositTokenEdit.text.toString()
            //????????? ??????
            val message = "${receiverNickName}?????? \n" +
                    "?????????????????? ???????????? \n" +
                    "???????????? : " + "${startDate} ~ ${endDate} \n" +
                    "????????? : " + "${reservationToken} ?????? \n" +
                    "????????? : " + "${depositToken} ??????"

            // data class??? ????????? DB??? ??????
            val messageObject = Message(message, senderEmail, 1, true, reservationToken, depositToken)

            mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    // ?????? ???????????? ??????
                    mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }

            val message2 = "${senderNickName}????????? \n" +
                    "?????????????????? ???????????? \n" +
                    "???????????? : " + "$startDate ~ ${endDate} \n" +
                    "????????? : " + "${reservationToken} ?????? \n" +
                    "????????? : " + "${depositToken} ??????"

            // data class??? ????????? DB??? ??????
            val messageObject2 = Message(message2, receiverEmail, 2, false,  reservationToken, depositToken)

            mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
                .setValue(messageObject2).addOnSuccessListener {
                    // ?????? ???????????? ??????
                    mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                        .setValue(messageObject2)
                }
            finish()
        }
    }
}