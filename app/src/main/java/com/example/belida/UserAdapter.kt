package com.example.belida

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.belida.database.User

class UserAdapter(private val context: Context, private val userList: ArrayList<User>, private val userLoginedEmail: String, private val userLoginedName: String):
    RecyclerView.Adapter<UserAdapter.UserViewHolder>(){

        /**
        * 화면 설정정
       */
        // user_layout을 연결하는 기능
        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
            val view: View = LayoutInflater.from(context).
            inflate(R.layout.user_layout, parent, false)

            return UserViewHolder(view)
        }

        /**
         * 데이터 설정
        */
        // 데이터를 전달받아 user_layout에 보여주는 기능
        override fun onBindViewHolder(holder: UserViewHolder, position: Int) {

            //데이터 담기
            val currentUser = userList[position]

            //화면에 데이터 보여주기
            holder.nameText.text = currentUser.userName
            holder.stateText.text = currentUser.userEmail

            //아이템 클릭 이벤트
            //대화방으로 이동하는 기능 구현
            holder.itemView.setOnClickListener{
                val intent = Intent(context, ChatActivity::class.java)

                //넘길 데이터
                intent.putExtra("UserLoginedEmail", userLoginedEmail)
                intent.putExtra("UserLoginedName", userLoginedName)
                intent.putExtra("opponentName", currentUser.userName)
                intent.putExtra("opponentEmail", currentUser.userToken)

                context.startActivity(intent)
            }
        }

        /**
         * 데이터 갯수 가져오기기
        */
        // userList의 갯수를 돌려준다.
        override fun getItemCount(): Int {
            return userList.size
        }

        // 뷰를 전달받아 텍스트뷰 객체를 만들 수 있다.
        // itemView 에는 user_layout을 넘긴다
        class UserViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

            val nameText: TextView = itemView.findViewById(R.id.name_text)
            val stateText: TextView = itemView.findViewById(R.id.state_text)
        }
    }