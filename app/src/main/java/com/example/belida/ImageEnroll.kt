package com.example.belida

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.item_enroll.*
import java.text.SimpleDateFormat
import java.util.*

class ImageEnroll : AppCompatActivity(){
    var PICK_IMAGE_FROM_ALBUM = 0       // request code?? 뭐야 이거
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_enroll)

        // 사용할 객체 생성해주기
        val upload_btn : Button = findViewById(R.id.upload_btn)
        val photo_btn : ImageButton = findViewById((R.id.photo_btn))

        // initiate storage (초기화하기)
        storage = FirebaseStorage.getInstance()

        // upload button 눌리는 event 처리하기
        photo_btn.setOnClickListener {
            // open the album
            var photoPikerIntent = Intent(Intent.ACTION_PICK)
            photoPikerIntent.type = "image/*"
            startActivityForResult(photoPikerIntent, PICK_IMAGE_FROM_ALBUM)
        }

        // upload button 눌리는 event 처리하기
        upload_btn.setOnClickListener {
            contentUpload()
        }

        // 상세 설명 버튼들
        declare_btn.setOnClickListener {
            val intent = Intent(this, Declare::class.java)
            startActivity(intent)
        }
        option1_btn.setOnClickListener {
            val intent = Intent(this,OptionFragment::class.java)
            startActivity(intent)
        }
        option2_btn.setOnClickListener {
            val intent = Intent(this,OptionFragment2::class.java)
            startActivity(intent)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // 사용할 객채 생성해주기
        val photo_btn : ImageButton = findViewById((R.id.photo_btn))

        if(requestCode == PICK_IMAGE_FROM_ALBUM){
            if(resultCode == Activity.RESULT_OK){
                // 사진 선택이 잘 되었을경우, 이미지 경로 이쪽으로 넘어옴
                photoUri = data?.data
                photo_btn.setImageURI(photoUri)
            }else{
                // Exit ImageEnroll
                finish()
            }
        }
    }

    fun contentUpload(){
        // Make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // FileUpload, !! 두개로 nullsafty를 해준다..
        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
            Toast.makeText(this, "upload success!", Toast.LENGTH_LONG).show()
            // upload 창 stack에서 삭제, 즉 뒤로가기버튼
            finish()
        }
    }

}