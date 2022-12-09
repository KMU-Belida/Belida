package com.example.belida

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.home_page.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private lateinit var fusedLocationClient: FusedLocationProviderClient


class HomePage : AppCompatActivity(), View.OnClickListener,Interaction {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var gridRecyclerViewAdapter : GridRecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel
    private var isRunning = true

    // 현재 로그인한 유저의 정보
    lateinit var userKey: String
    lateinit var userLoginedName: String
    lateinit var userLoginedEmail: String

    //위치 받아오기
    private val database = Firebase.database
    private val userDB = database.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.setBannerItems(fakeBannerItemList)
        viewModel.setGridItems(fakeGridItemList)

        userKey = intent.getStringExtra("UserKey").toString() // 현재 로그인한 userKey값
        userLoginedName = intent.getStringExtra("UserName").toString()
        userLoginedEmail = intent.getStringExtra("UserEmail").toString()

//        iv_hamburger.setOnClickListener(this)

        initViewPager2()
        subscribeObservers()
        autoScrollViewPager()
        //홈 버튼, 카테고리 버튼, 검색 버튼 인텐트
        home_btn.setOnClickListener {
            val intent = Intent(this,HomePage::class.java)
            startActivity(intent)
        }
        category_btn.setOnClickListener {
            val intent = Intent(this,Category::class.java)
            startActivity(intent)
        }
        chat_btn.setOnClickListener {
            val intent = Intent(this,ChatListActivity::class.java)
            intent.putExtra("UserKey", userKey)
            intent.putExtra("UserName", userLoginedName)
            intent.putExtra("UserEmail", userLoginedEmail)
            startActivity(intent)
        }
        home_search_btn.setOnClickListener{
            val intent = Intent(this,HomeSearch::class.java)
            startActivity(intent)
        }
        addbtn.setOnClickListener {
            val intent = Intent(this,ImageEnroll::class.java)
            startActivity(intent)
        }
        item_board_btn.setOnClickListener {
            var intent = Intent(this, BoardActivity::class.java)
            startActivity(intent)
        }
        //위치 가져오기
        val userKey = intent.getStringExtra("UserKey").toString() // 데이터베이스에 저장된 유저Key값
        val MY_PERMISSION_ACCESS_ALL = 100
        val geocoder = Geocoder(this)
        val locationButton: ImageButton = findViewById(R.id.locationButton)
        val user_location = findViewById<TextView>(R.id.user_location)

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

                    var addrLocality = addr[0].subLocality

                    userDB.child(userKey).child("userLocation").setValue(addrLocality)

                    //저장된 값의 subLocality가 구 단위, 하지만 외국(에뮬레이터)에서는 adminArea가 최선
                    user_location.text = addrLocality
                    Log.d("Test", "GPS Location changed, $addr")
                    fusedLocationClient.removeLocationUpdates(locationCallback);
//                    if(user_location.text != "주소"){
//                        location_next_btn.setOnClickListener {
//                            val intent = Intent(this, HomePage::class.java)
//                            intent.putExtra("UserKey", userKey)
//                            startActivity(intent)
//                            finish()
//                        }
//                    }
                }
            }
        }
    }

    private fun initViewPager2() {
        viewPager2.apply {
            viewPagerAdapter = ViewPagerAdapter(this@HomePage)
            adapter = viewPagerAdapter
            registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)

                    isRunning=true
                    tv_page_number.text = "${position + 1}"

                    viewModel.setCurrentPosition(position)
                }
            })
        }
        gridRecyclerView.apply{
            gridRecyclerViewAdapter = GridRecyclerViewAdapter()
            layoutManager = GridLayoutManager(this@HomePage,4)

            adapter = gridRecyclerViewAdapter
        }
    }

    private fun subscribeObservers() {
        viewModel.bannerItemList.observe(this, Observer { bannerItemList ->
            viewPagerAdapter.submitList(bannerItemList)
        })
        viewModel.gridItemList.observe(this,Observer {gridItemList->
            gridRecyclerViewAdapter.submitList(gridItemList)
        })
        viewModel.currentPosition.observe(this, Observer { currentPosition ->
            viewPager2.currentItem = currentPosition
        })
    }

    private fun autoScrollViewPager() {
        lifecycleScope.launchWhenResumed {
            while (isRunning) {
                delay(3000)
                viewModel.getcurrentPosition()?.let {
                    viewModel.setCurrentPosition((it.plus(1)) % 5)
                }
            }
        }
    }


    override fun onPause() {
        super.onPause()
        isRunning = false
    }

    override fun onResume() {
        super.onResume()
        isRunning = true
    }

    override fun onBannerItemClicked(bannerItem: BannerItem) {
        startActivity(Intent(this@HomePage, EventActivity::class.java))
    }

    override fun onClick(v: View?) {
    }
}