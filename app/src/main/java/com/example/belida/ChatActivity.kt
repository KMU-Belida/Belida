package com.example.belida

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.example.belida.database.Message
import com.example.belida.databinding.ActivityChatBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase


class ChatActivity : AppCompatActivity() {

    // 대화 사람 선택시 상대방 정보 가져오기
    private lateinit var receiverName: String
    private lateinit var receiverEmail: String

    // 바인딩 객체
    private lateinit var binding: ActivityChatBinding
    // DB 객체
    lateinit var mDbRef: DatabaseReference

    private lateinit var receiverRoom: String // 상대방 대화방
    private lateinit var senderRoom: String // 현재 로그인한 유저 대화방

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //넘어온 데이터 변수에 담기
        receiverName = intent.getStringExtra("opponentName").toString()
        receiverEmail = intent.getStringExtra("opponentEmail").toString()

        //db 초기화
        mDbRef = Firebase.database.reference

        // 현재 로그인한 유저 이메일
        val senderName = intent.getStringExtra("UserLoginedNickname").toString()
        val senderEmail = intent.getStringExtra("UserLoginedEmail").toString()

        // 현재 로그인한 유저 대화방의 변수
        senderRoom = receiverName + senderName

        // 상대방 대화방의 변수
        receiverRoom = senderName + receiverName

        // 액션바에 상대방 이름 보여주기
        val text1 = findViewById<TextView>(R.id.user_name)
        text1.text = receiverName

        // 메세지 전송 버튼 이벤트
        binding.sendBtn.setOnClickListener {
            // 입력한 메세지
            val message = binding.messageEdit.text.toString()
            // data class에 넣어서 DB에 삽입
            val messageObject = Message(message, senderEmail)

            mDbRef.child("chattingRooms").child(senderRoom).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    // 삽입 성공했을 경우
                    mDbRef.child("chattingRooms").child(receiverRoom).child("messages").push()
                        .setValue(messageObject)
                }
        }
    }
}