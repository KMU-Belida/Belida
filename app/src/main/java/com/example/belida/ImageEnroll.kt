package com.example.belida

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import com.example.belida.model.ContentDTO
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.item_enroll.*
import java.text.SimpleDateFormat
import java.util.*

class ImageEnroll : AppCompatActivity(){
    var PICK_IMAGE_FROM_ALBUM = 0       // request code?? 뭐야 이거
    var storage : FirebaseStorage? = null
    var photoUri : Uri? = null
    var auth : FirebaseAuth? = null
    var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_enroll1)

        // 사용할 객체 생성해주기
        val upload_btn : Button = findViewById(R.id.upload_btn)
        val photo_btn : ImageButton = findViewById((R.id.photo_btn))

        // initiate storage (초기화하기)
        storage = FirebaseStorage.getInstance()
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

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
        back_btn.setOnClickListener{
            finish()
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
        // view에서 필요한거 가져오기
        var item_describe_et = findViewById(R.id.item_describe_et) as EditText
        var item_name_et = findViewById(R.id.item_name_et) as EditText
        var item_categori_et = findViewById(R.id.item_category_et) as EditText
        var item_price_et = findViewById(R.id.item_price_et) as EditText

        // Make filename
        var timestamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        var imageFileName = "IMAGE_" + timestamp + "_.png"

        var storageRef = storage?.reference?.child("images")?.child(imageFileName)

        // Promise 방식으로 upload 하는법 -(Google 권장 방식임)
        storageRef?.putFile(photoUri!!)?.continueWithTask { task: Task<UploadTask.TaskSnapshot> ->
            return@continueWithTask storageRef.downloadUrl
        }?.addOnSuccessListener { uri ->
            var contentDTO = ContentDTO()

            // Insert downloadUrl of image
            contentDTO.imageUrl = uri.toString()

            // Insert uid of user
            contentDTO.uid = auth?.currentUser?.uid

            // Insert userid
            contentDTO.userId = auth?.currentUser?.email

            // Insert explain of content
            contentDTO.explain = item_describe_et.text.toString()

            // Insert itemname, categori, price
            contentDTO.itemname = item_name_et.text.toString()
            contentDTO.category = item_categori_et.text.toString()
            contentDTO.price = item_price_et.text.toString()

            // Insert TimeStamp
            contentDTO.timestamp = System.currentTimeMillis()

            firestore?.collection("images")?.document()?.set(contentDTO)

            // 정상적으로 화면이 닫혔다는 값을 넘겨주기 위해서 setResult값을 RESULT_OK로 설정
            setResult(Activity.RESULT_OK)

            // upload 창 stack에서 삭제, 즉 뒤로가기버튼
            finish()
        }

        // Callback 방식으로 FileUpload, !! 두개로 nullsafty를 해준다..
//        storageRef?.putFile(photoUri!!)?.addOnSuccessListener {
//            Toast.makeText(this, "upload success!", Toast.LENGTH_LONG).show()
//
//            storageRef.downloadUrl.addOnSuccessListener { uri ->
//                var contentDTO = ContentDTO()
//
//                // Insert downloadUrl of image
//                contentDTO.imageUrl = uri.toString()
//
//                // Insert uid of user
//                contentDTO.uid = auth?.currentUser?.uid
//
//                // Insert userid
//                contentDTO.userId = auth?.currentUser?.email
//
//                // Insert explain of content
//                contentDTO.explain = item_describe_et.text.toString()
//
//                // Insert TimeStamp
//                contentDTO.timestamp = System.currentTimeMillis()
//
//                firestore?.collection("images")?.document()?.set(contentDTO)
//
//                // 정상적으로 화면이 닫혔다는 값을 넘겨주기 위해서 setResult값을 RESULT_OK로 설정
//                setResult(Activity.RESULT_OK)
//
//                // upload 창 stack에서 삭제, 즉 뒤로가기버튼
//                finish()
//            }
//        }
    }
}