package com.example.belida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.belida.databinding.ActivityNaviBinding
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.jar.Manifest

private const val TAG_HOME = "home_fragment"
private const val TAG_CATEGORY = "category_fragment"
private const val TAG_CHATTING = "chatting_fragment"
private const val TAG_MY_PAGE = "my_page_fragment"

class NaviActivity : AppCompatActivity() {
    private lateinit var binding : ActivityNaviBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNaviBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setFragment(TAG_HOME, HomeFragment())

        binding.navigationView.setOnItemSelectedListener { item ->
            when(item.itemId) {
                R.id.homeFragment -> setFragment(TAG_HOME, HomeFragment())
                R.id.categoryFragment -> setFragment(TAG_CATEGORY, CategoryFragment())
                R.id.chattingFragment -> setFragment(TAG_CHATTING, ChattingFragment())
                R.id.myPageFragment-> setFragment(TAG_MY_PAGE, MyPageFragment())
            }
            true
        }

        // add button 객채 만들기 kotilin 에서는 as로 업캐스팅해줌 자바는 c++처럼 강제 형변환 형태
        val add_button = findViewById(R.id.addButton) as FloatingActionButton

        // add button 눌렸을 경우
        add_button.setOnClickListener {
            // 권한 확인?? 어케함
            // if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) == PakageManager.PERMISSION_GRANTED)
            moveAddPage()
        }

        // 이미지 저장 허용?
        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), 1)

    }

    private fun setFragment(tag: String, fragment: Fragment) {
        val manager: FragmentManager = supportFragmentManager
        val fragTransaction = manager.beginTransaction()

        if (manager.findFragmentByTag(tag) == null){
            fragTransaction.add(R.id.mainFrameLayout, fragment, tag)
        }

        val home = manager.findFragmentByTag(TAG_HOME)
        val category = manager.findFragmentByTag(TAG_CATEGORY)
        val chatting = manager.findFragmentByTag(TAG_CHATTING)
        val myPage = manager.findFragmentByTag(TAG_MY_PAGE)

        if (home != null){
            fragTransaction.hide(home)
        }

        if (category != null){
            fragTransaction.hide(category)
        }

        if (chatting != null){
            fragTransaction.hide(chatting)
        }

        if (myPage != null) {
            fragTransaction.hide(myPage)
        }

        if (tag == TAG_HOME) {
            if (home != null) {
                fragTransaction.show(home)
            }
        }

        else if (tag == TAG_CATEGORY){
            if (category != null){
                fragTransaction.show(category)
            }
        }

        else if (tag == TAG_CHATTING){
            if (chatting != null){
                fragTransaction.show(chatting)
            }
        }

        else if (tag == TAG_MY_PAGE){
            if (myPage != null){
                fragTransaction.show(myPage)
            }
        }

        fragTransaction.commitAllowingStateLoss()
    }

    fun moveAddPage(){

        // 현재 로그인한 사용자 정보를 user 변수로 불러오기
        val user = Firebase.auth.currentUser
        // user 상태가 존재하면 다음페이지로 넘겨주기
        if (user != null) {
            // User is signed in
            startActivity(Intent(this, ImageEnroll::class.java))
        } else {
            // No user is signed in
        }
    }
}