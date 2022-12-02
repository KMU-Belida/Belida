package com.example.belida

import android.os.Binder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.belida.database.Message
import com.example.belida.databinding.ActivityChatBinding
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_chat.*


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

    private lateinit var messageList: ArrayList<Message> // 메세지를 담을 리스트

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChatBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 현재 로그인한 유저 이메일
        val senderName = intent.getStringExtra("UserLoginedName").toString()
        val senderEmail = intent.getStringExtra("UserLoginedEmail").toString()

        val plus_btn: Button = findViewById(R.id.plus_btn)

        plus_btn.setOnClickListener {
            if(plus_container.visibility == View.GONE){
                plus_container.visibility = View.VISIBLE
            }
            else{
                plus_container.visibility = View.GONE
            }
        }

        // 메세지 리스트 초기화
        messageList = ArrayList()
        // adapter 초기화
        val messageAdapter: MessageAdapter = MessageAdapter(this, messageList, senderEmail)

        // RecyclerView
        binding.chatRecyclerView.layoutManager = LinearLayoutManager(this)
        binding.chatRecyclerView.adapter = messageAdapter

        //넘어온 데이터 변수에 담기
        receiverName = intent.getStringExtra("opponentName").toString()
        receiverEmail = intent.getStringExtra("opponentEmail").toString()

        //db 초기화
        mDbRef = Firebase.database.reference

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
            // 메세지 전송하고 메세지 입력하는 부분 초기화
            binding.messageEdit.setText("")
        }

        // 메세지 가져오기
        mDbRef.child("chattingRooms").child(senderRoom).child("messages")
            .addValueEventListener(object: ValueEventListener{
                // 데이터 가져오는 기능
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList.clear() // 실행이 되면 미리 데이터를 비워줌

                    for(postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    // 화면에 메세지 내용 보여줌
                    messageAdapter.notifyDataSetChanged()
                }
                override fun onCancelled(error: DatabaseError) {
                }
            })
    }
}