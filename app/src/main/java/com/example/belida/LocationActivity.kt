package com.example.belida

import android.Manifest
import android.content.pm.PackageManager
import android.location.Geocoder
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices

private lateinit var fusedLocationClient: FusedLocationProviderClient

class LocationActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val MY_PERMISSION_ACCESS_ALL = 100
        val geocoder = Geocoder(this)
        val locationButton: AppCompatImageButton = findViewById(R.id.location_btn)
        val user_location = findViewById<TextView>(R.id.location_textView)
        // 마지막 위치를 가져오기 위함
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        //위치 권한 설정되어있는지 확인
        if (ActivityCompat.checkSelfPermission(this,
                        Manifest.permission.ACCESS_FINE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this, "위치 권한을 설정해주세요.", Toast.LENGTH_SHORT).show()
            var permissions = arrayOf(
                android.Manifest.permission.ACCESS_FINE_LOCATION,
                android.Manifest.permission.ACCESS_COARSE_LOCATION
            )
        }else{
            // 버튼 클릭 시 textView 현재 위치로 변경
            locationButton.setOnClickListener{
                fusedLocationClient.lastLocation.addOnSuccessListener {
                    //addr에 위도 경도값 이용하여 주소 생성하여 저장
                    val addr = geocoder.getFromLocation(it.latitude, it.longitude, 1)
                    //저장된 값의 subLocality가 구 단위
                    user_location.text = addr[0].subLocality
                }

            }
        }
    }
}