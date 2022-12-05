package com.example.belida

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.AppCompatImageButton
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_location.*

private lateinit var fusedLocationClient: FusedLocationProviderClient

class LocationActivity : AppCompatActivity() {
    private val database = Firebase.database
    private val userDB = database.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_location)

        val userKey = intent.getStringExtra("UserKey").toString() // 데이터베이스에 저장된 유저Key값
        val MY_PERMISSION_ACCESS_ALL = 100
        val geocoder = Geocoder(this)
        val locationButton: FloatingActionButton = findViewById(R.id.location_btn)
        val user_location = findViewById<TextView>(R.id.location_textView)
        // 마지막 위치를 가져오기 위함
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        val locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                if (locationResult == null) {
                    return
                }
                for (location in locationResult.locations) {
                    if (location != null) {
                        val latitude = location.latitude
                        val longitude = location.longitude
                        Log.d("Test", "GPS Location changed, Latitude: $latitude" +
                                ", Longitude: $longitude")
                    }
                }
            }
        }
        val locationRequest = LocationRequest.create()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 20 * 1000
        //위치 권한 설정되어있는지 확인
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "위치 권한을 설정해주세요.", Toast.LENGTH_SHORT).show()
            var permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper());
        
        // 버튼 클릭 시 textView 현재 위치로 변경
        locationButton.setOnClickListener{
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if(location != null){
                    //addr에 위도 경도값 이용하여 주소 생성하여 저장
                    var addr = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    //파이어베이스에 저장해야 할 지역구 단위값

                    var addrLocality = addr?.get(0)?.adminArea

                    userDB.child(userKey).child("userLocation").setValue(addrLocality)

                    //저장된 값의 subLocality가 구 단위, 하지만 외국(에뮬레이터)에서는 adminArea가 최선
                    user_location.text = addrLocality
                    Log.d("Test", "GPS Location changed, $addr")
                    fusedLocationClient.removeLocationUpdates(locationCallback);
                    if(user_location.text != "주소"){
                        location_next_btn.setOnClickListener {
                            val intent = Intent(this, HomePage::class.java)
                            intent.putExtra("UserKey", userKey)
                            startActivity(intent)
                            finish()
                        }
                    }
                }
            }
        }
    }

}