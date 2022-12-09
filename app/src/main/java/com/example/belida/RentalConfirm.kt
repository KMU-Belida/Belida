package com.example.belida

import android.app.Activity
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.Bundle
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
import kotlinx.android.synthetic.main.dialog_rental_confirm.*

class RentalConfirm : Activity() {
    var dateString = ""

    lateinit var startDate: String
    lateinit var endDate: String
    lateinit var receiverNickName: String
    lateinit var receiverEmail: String
    lateinit var senderNickName: String
    lateinit var senderEmail: String
    lateinit var senderRoom: String
    lateinit var receiverRoom: String
    lateinit var mDbRef: DatabaseReference
    private val database = Firebase.database
    private val userDB = database.getReference("user")

    companion object {
        lateinit var reservationToken: String
        lateinit var depositToken: String
    }

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dialog_rental_confirm)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        mDbRef = Firebase.database.reference

        receiverNickName = intent.getStringExtra("ReceiverNickName").toString()
        receiverEmail = intent.getStringExtra("ReceiverEmail").toString()
        senderNickName = intent.getStringExtra("SenderNickName").toString()
        senderEmail = intent.getStringExtra("SenderEmail").toString()
        reservationToken = intent.getStringExtra("ReservationToken").toString()
        depositToken = intent.getStringExtra("DepositToken").toString()

        // 현재 로그인한 유저 대화방의 변수
        senderRoom = receiverNickName + senderNickName

        // 상대방 대화방의 변수
        receiverRoom = senderNickName + receiverNickName

        borrow_confirm_btn.setOnClickListener {
            val senderMessage = "대여가 확정되었습니다."
            insertSenderDB(senderMessage)

            val receiverMessage = "대여가 확정되었습니다."
            insertReceiverDB(receiverMessage)

            subtractBelidaToken()
            finish()
        }
        borrow_cancel_btn.setOnClickListener {
            val senderMessage = "대여가 취소되었습니다."
            insertSenderDB(senderMessage)

            val receiverMessage = "대여가 취소되었습니다."
            insertReceiverDB(receiverMessage)
            finish()
        }

    }

    fun insertSenderDB(senderMessage : String) {
        // data class에 넣어서 DB에 삽입
        val senderMessageObject = Message(senderMessage, senderEmail)

        mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
            .setValue(senderMessageObject).addOnSuccessListener {
                // 삽입 성공했을 경우
                mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                    .setValue(senderMessageObject)
            }
    }

    fun insertReceiverDB(receiverMessage: String) {
        // data class에 넣어서 DB에 삽입
        val receiverMessageObject = Message(receiverMessage, receiverEmail)

        mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
            .setValue(receiverMessageObject).addOnSuccessListener {
                // 삽입 성공했을 경우
                mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                    .setValue(receiverMessageObject)
            }
    }

    fun subtractBelidaToken() {
        userDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (targetSnapshot in dataSnapshot.children) {
                    if(targetSnapshot.getValue(User::class.java)?.userEmail.equals(receiverEmail)) {
                        val currentBelidaToken = targetSnapshot.getValue(User::class.java)?.belidaToken
                        val totalNeedBelidaToken = reservationToken.toInt() + depositToken.toInt()
                        userDB.child(targetSnapshot.key.toString()).child("belidaToken").setValue(
                            currentBelidaToken?.minus(totalNeedBelidaToken)
                        )

                    } else if(targetSnapshot.getValue(User::class.java)?.userEmail.equals(senderEmail)) {
                        val currentBelidaToken = targetSnapshot.getValue(User::class.java)?.belidaToken
                        val totalNeedBelidaToken = reservationToken.toInt() + depositToken.toInt()
                        userDB.child(targetSnapshot.key.toString()).child("belidaToken").setValue(
                            currentBelidaToken?.minus(totalNeedBelidaToken)
                        )
                    }
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext,
                    databaseError.message,
                    Toast.LENGTH_SHORT).show()
            }
        })
    }
}
