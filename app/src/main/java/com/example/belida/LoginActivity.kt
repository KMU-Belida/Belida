package com.example.belida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.belida.database.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class LoginActivity : AppCompatActivity() {
    // firebase 라이브러리 불러오기
    var auth: FirebaseAuth? = null
    val database = Firebase.database
    val userDB = database.getReference("user")

    // 현재 로그인한 유저의 정보
    lateinit var userKey: String
    lateinit var userLoginedNickName: String
    lateinit var userLoginedEmail: String

    // 주의 onCreate밖에서 view정보를 호출하면 안됨, 선언까지는가능!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 정보 가져오기 ?
        auth = FirebaseAuth.getInstance()

        // 사용하고싶은 객체를 바인딩으로 생성해야하나?
        val login_button: Button = findViewById(R.id.email_login_button)
        // 로그인 버튼이 눌렸을 경우 이벤트 처리 하는 곳
        login_button.setOnClickListener {
            signinAndSignup()
        }
    }

    fun signinAndSignup() {
        val emailEdit: EditText = findViewById(R.id.email_edittext)
        val passwordEdit: EditText = findViewById(R.id.password_edittext)
        auth?.createUserWithEmailAndPassword(
            emailEdit.text.toString(),
            passwordEdit.text.toString()
        )
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Creating a user account
                    Toast.makeText(this, "회원가입 및 로그인 완료!", Toast.LENGTH_LONG).show()
                    userKey = userDB.push().key.toString()
                    userDB.child(userKey).setValue(
                        User(
                            emailEdit.text.toString(),
                            passwordEdit.text.toString(),
                            "",
                            "",
                            "",
                        )
                    )
                    val userKeyIntent = Intent(this, NicknameActivity::class.java)
                    userKeyIntent.putExtra("UserKey", userKey)
                    userKeyIntent.putExtra("UserEmail", emailEdit.text.toString())
                    startActivity(userKeyIntent)
//                    }else if(!task.exception?.message.isNullOrEmpty()){
//                        // show login error message
//                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                } else {
                    // 회원가입도 아니고 로그인 에러도 아닐경우 로그인으로 넘어가기
                    signinEmail()
                }
            }
    }

    fun signinEmail() {
        val emailEdit: EditText = findViewById(R.id.email_edittext)
        val passwordEdit: EditText = findViewById(R.id.password_edittext)
        auth?.signInWithEmailAndPassword(emailEdit.text.toString(), passwordEdit.text.toString())
            ?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Login
                    Toast.makeText(this, "로그인 완료!", Toast.LENGTH_LONG).show()
                    getUserKeyAndMoveMainPage(task.result.user, emailEdit.text.toString())
                } else {
                    // 로그인 에러 메세지 보여주기
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                }
            }
    }

    // 유저 키 가져오기 및 메인 페이지 이동
    fun getUserKeyAndMoveMainPage(user: FirebaseUser?, userEmail : String) {
        userDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (targetSnapshot in dataSnapshot.children) {
                    if(targetSnapshot.getValue(User::class.java)?.userEmail.equals(userEmail)) {
                        userKey = targetSnapshot.key.toString()
                        userLoginedNickName = targetSnapshot.getValue(User::class.java)?.userNickName.toString()
                        userLoginedEmail = targetSnapshot.getValue(User::class.java)?.userEmail.toString()
                        break
                    }
                }
                moveMainPage(user)
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext,
                    databaseError.message,
                    Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 로그인이 성공하면 다음페이지로 넘어가는 함수 구현
    fun moveMainPage(user: FirebaseUser?) {
        // user 상태가 존재하면 다음페이지로 넘겨주기
        if (user != null) {
            val userKeyIntent = Intent(this, HomePage::class.java)
            userKeyIntent.putExtra("UserKey", userKey)
            userKeyIntent.putExtra("UserNickName", userLoginedNickName)
            userKeyIntent.putExtra("UserEmail", userLoginedEmail)
            startActivity(userKeyIntent)
        }
    }
}