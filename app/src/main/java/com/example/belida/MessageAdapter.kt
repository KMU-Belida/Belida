package com.example.belida

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.belida.database.Message
import org.w3c.dom.Text

class MessageAdapter(private val context: Context, private val messageList: ArrayList<Message>, private val userLoginedEmail: String):
    // Sender, Receiver ViewHolder 2개가 있기 때문에, RecycleViewHolder를 이용
    RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    // 사용자 이메일에 따라 어떤 viewholder를 사용할지 정하기 위해 변수 2개 생성
    private val receiveNum = 1 // 메세지를 받는 쪽
    private val sendNum = 2 // 메세지를 보내는 쪽

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == 1) { // 메세지를 받는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.chat_receive, parent, false)
            ReceiveViewHolder(view)
        } else { // 메세지를 보내는 화면
            val view: View = LayoutInflater.from(context).inflate(R.layout.chat_send, parent, false)
            SendViewHolder(view)
        }
    }

    // 뷰와 데이터 연결
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        // 메세지
        val currentMessage = messageList[position]

        // 보내는 데이터
        if(holder.javaClass == SendViewHolder::class.java) {
            val viewHolder = holder as SendViewHolder
            viewHolder.sendMessage.text = currentMessage.message
        } else { // 받는 데이터
            val viewHolder = holder as ReceiveViewHolder
            viewHolder.receiveMessage.text = currentMessage.message
        }
    }

    // messageList의 갯수를 돌려준다.
    override fun getItemCount(): Int {
        return messageList.size
    }

    // 어떤 Viewholder를 사용할지 정한다
    override fun getItemViewType(position: Int): Int {

        // 메세지
        val currentMessage = messageList[position]

        return if(userLoginedEmail.equals(currentMessage.sendEmail)) {
            sendNum
        } else {
            receiveNum
        }
    }

    // 뷰를 전달받아 객체가 생성됨
    // 메세지를 보낸 쪽
    class SendViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val sendMessage : TextView = itemView.findViewById(R.id.send_message_text)
    }

    // 메세지를 받은 쪽
    class ReceiveViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val receiveMessage : TextView = itemView.findViewById(R.id.receive_message_text)
    }
}