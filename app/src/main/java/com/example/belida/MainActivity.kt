package com.example.belida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.widget.AppCompatButton
import com.example.belida.database.User
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.common.model.AuthErrorCause.*
import com.kakao.sdk.user.UserApiClient

class MainActivity : AppCompatActivity() {

    val TAG = "MainActivity" // Tag는 바뀌지 않으므로 한 번 선언해서 계속 재사용
    val database = Firebase.database
    val userDB = database.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // 로그인 정보 확인
        UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
            if (error != null) {
                Toast.makeText(this, "토큰 정보 보기 실패", Toast.LENGTH_SHORT).show()
            } else if (tokenInfo != null) {
                Toast.makeText(this, "토큰 정보 보기 성공", Toast.LENGTH_SHORT).show()
                val intent = Intent(this, NicknameActivity::class.java)
                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
                finish()
            }
        }
        val callback: (OAuthToken?, Throwable?) -> Unit = { token, error ->
            if (error != null) {
                when {
                    error.toString() == AccessDenied.toString() -> {
                        Toast.makeText(this, "접근이 거부 됨(동의 취소)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidClient.toString() -> {
                        Toast.makeText(this, "유효하지 않은 앱", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidGrant.toString() -> {
                        Toast.makeText(this, "인증 수단이 유효하지 않아 인증할 수 없는 상태", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidRequest.toString() -> {
                        Toast.makeText(this, "요청 파라미터 오류", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == InvalidScope.toString() -> {
                        Toast.makeText(this, "유효하지 않은 scope ID", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Misconfigured.toString() -> {
                        Toast.makeText(this, "설정이 올바르지 않음(android key hash)", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == ServerError.toString() -> {
                        Toast.makeText(this, "서버 내부 에러", Toast.LENGTH_SHORT).show()
                    }
                    error.toString() == Unauthorized.toString() -> {
                        Toast.makeText(this, "앱이 요청 권한이 없음", Toast.LENGTH_SHORT).show()
                    }
                    else -> { // Unknown
                        Toast.makeText(this, "기타 에러", Toast.LENGTH_SHORT).show()
                    }
                }
            }
            else if (token != null) {
                Toast.makeText(this, "로그인에 성공하였습니다.", Toast.LENGTH_SHORT).show()
                UserApiClient.instance.me { user, error ->
                    if (error != null) {
                        Log.e(TAG, "사용자 정보 요청 실패", error)
                    }
                    else if (user != null) {
                        // val userId = user.id
                        val userEmail = user.kakaoAccount?.email.toString()
                        val userNickName = user.kakaoAccount?.profile?.nickname.toString()
                        checkEmailDuplicate(userEmail, userNickName, token.toString())
                        finish()
                    }
                }
//                val intent = Intent(this, NicknameActivity::class.java)
//                startActivity(intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP))
//                finish()
            }
        }

        val kakao_login_button = findViewById<AppCompatButton>(R.id.kakao_login_button) // 로그인 버튼

        kakao_login_button.setOnClickListener {
            if(UserApiClient.instance.isKakaoTalkLoginAvailable(this)){
                UserApiClient.instance.loginWithKakaoTalk(this, callback = callback)


            }else{
                UserApiClient.instance.loginWithKakaoAccount(this, callback = callback)
            }
        }

        // 이메일로 로그인 버튼 눌러서 화면 전환하는 함수 만들기
        fun moveEmailLoginPage(){
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)

        }

        // 이메일로 로그인 버튼 객체 만들기
        val email_login_button: Button = findViewById(R.id.email_login_button)
        // 이메일로 로그인 버튼 눌렀을경우
        email_login_button.setOnClickListener {
            moveEmailLoginPage()
        }

    }

    // 새로운 유저일 경우 DB에 유저 정보 넣기
    fun pushEmailDB(userEmail : String, userNickName : String, token : String) {
        val userKey = userDB.push().key.toString()
        userDB.child(userKey).setValue(User(userEmail, "", userNickName, "", token))
        val userKeyIntent = Intent(this, NicknameActivity::class.java)
        userKeyIntent.putExtra("UserKey", userKey)
        startActivity(userKeyIntent)
    }

    // 새로운 유저인지 확인하기 위해 이메일 중복 확인
    fun checkEmailDuplicate(userEmail: String, userNickName: String, token: String) {
        userDB.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                var isDuplicate = false
                for (targetSnapshot in dataSnapshot.children) {
                    if(!targetSnapshot.getValue(User::class.java)?.userEmail.equals(userEmail)) {
                        // println(targetSnapshot.getValue(User::class.java)?.userNickName + "중복X")
                        continue
                    } else {
                        // println(targetSnapshot.getValue(User::class.java)?.userNickName + "중복")
                        isDuplicate = true
                        break
                    }
                }
                if (!isDuplicate) {
                    pushEmailDB(userEmail, userNickName, token)
                } else {
                    moveMainPage()
                }
            }
            override fun onCancelled(databaseError: DatabaseError) {
                Toast.makeText(applicationContext,
                        databaseError.message,
                        Toast.LENGTH_SHORT).show()
            }
        })
    }

    // 메인페이지로 이동
    fun moveMainPage(){
        startActivity(Intent(this, NaviActivity::class.java))
    }
}