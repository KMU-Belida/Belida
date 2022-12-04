package com.example.belida

import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.belida.database.Message
import com.example.belida.database.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.dialog_rental.*
import kotlinx.android.synthetic.main.dialog_return.*
import java.util.*

class Return : Activity() {
    lateinit var receiverName: String
    lateinit var receiverEmail: String
    lateinit var senderName: String
    lateinit var senderEmail: String
    lateinit var senderRoom: String
    lateinit var receiverRoom: String
    lateinit var mDbRef: DatabaseReference
    private val database = Firebase.database
    private val userDB = database.getReference("user")

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_return)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        receiverName = intent.getStringExtra("ReceiverName").toString()
        receiverEmail = intent.getStringExtra("ReceiverEmail").toString()
        senderName = intent.getStringExtra("SenderName").toString()
        senderEmail = intent.getStringExtra("SenderEmail").toString()
        // 현재 로그인한 유저 대화방의 변수
        senderRoom = receiverName + senderName
        // 상대방 대화방의 변수
        receiverRoom = senderName + receiverName

        mDbRef = Firebase.database.reference

        return_cancel_btn.setOnClickListener {
            //취소 버튼
            finish()
        }
        return_save_btn.setOnClickListener {
            //보내기 버튼
            val message = "${receiverName}님께 \n" +
                    "반납신청서를 보냈어요"

            // data class에 넣어서 DB에 삽입
            val messageObject = Message(message, senderEmail)

            mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    // 삽입 성공했을 경우
                    mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }

            val message2 = "${senderName}님이 \n" +
                    "반납신청서를 보냈어요"

            // data class에 넣어서 DB에 삽입
            val messageObject2 = Message(message2, receiverEmail)

            mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
                .setValue(messageObject2).addOnSuccessListener {
                    // 삽입 성공했을 경우
                    mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                        .setValue(messageObject2)
                }

            val confirmIntent = Intent(this, ReturnConfirm::class.java)
            confirmIntent.putExtra("SenderName", senderName)
            confirmIntent.putExtra("ReceiverName", receiverName)
            confirmIntent.putExtra("SenderEmail", senderEmail)
            confirmIntent.putExtra("ReceiverEmail", receiverEmail)
            startActivity(confirmIntent)

            finish()
        }
    }
}