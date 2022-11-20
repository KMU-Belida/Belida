package com.example.belida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class LoginActivity : AppCompatActivity() {
    // firebase 라이브러리 불러오기
    var auth: FirebaseAuth? = null

    // 주의 onCreate밖에서 view정보를 호출하면 안됨, 선언까지는가능!!
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // 정보 가져오기 ?
        auth = FirebaseAuth.getInstance()

        // 사용하고싶은 객체를 바인딩으로 생성해야하나?
        val login_button: Button = findViewById(R.id.email_login_btn)

        // 로그인 버튼이 눌렸을 경우 이벤트 처리 하는 곳
        login_button.setOnClickListener {
            signinAndSignup()
        }
    }

    fun signinAndSignup(){
        val emailEdit: EditText = findViewById(R.id.email_edittext)
        val passwordEdit: EditText = findViewById(R.id.password_edittext)
        auth?.createUserWithEmailAndPassword(emailEdit.text.toString(), passwordEdit.text.toString())
            ?.addOnCompleteListener {
                task ->
                    if(task.isSuccessful){
                        // Creating a user account
                        Toast.makeText(this, "회원가입 및 로그인 완료!", Toast.LENGTH_LONG).show()
                        moveMainPage(task.result.user)
//                    }else if(!task.exception?.message.isNullOrEmpty()){
//                        // show login error message
//                        Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
                    }else{
                        // 회원가입도 아니고 로그인 에러도 아닐경우 로그인으로 넘어가기
                        signinEmail()
                    }
        }
    }
    fun signinEmail(){
        val emailEdit: EditText = findViewById(R.id.email_edittext)
        val passwordEdit: EditText = findViewById(R.id.password_edittext)
        auth?.signInWithEmailAndPassword(emailEdit.text.toString(), passwordEdit.text.toString())
            ?.addOnCompleteListener {
                    task ->
                if(task.isSuccessful){
                    // Login
                    Toast.makeText(this, "로그인 완료!", Toast.LENGTH_LONG).show()
                    moveMainPage(task.result.user)
                }else{
                    // 로그인 에러 메세지 보여주기
                    Toast.makeText(this, task.exception?.message, Toast.LENGTH_LONG).show()
            }
        }
    }
    // 로그인이 성공하면 다음페이지로 넘어가는 함수 구현
    fun moveMainPage(user:FirebaseUser?){
        // user 상태가 존재하면 다음페이지로 넘겨주기
        if(user != null){
            startActivity(Intent(this, NaviActivity::class.java))
        }
    }
}