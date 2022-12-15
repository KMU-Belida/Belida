package com.example.belida

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.example.belida.database.User
import com.bumptech.glide.Glide
import com.example.belida.model.ContentDTO
import com.google.android.gms.location.*
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_home_search.*
import kotlinx.android.synthetic.main.activity_location.*
import kotlinx.android.synthetic.main.home_page.*
import kotlinx.android.synthetic.main.home_page.category_btn
import kotlinx.android.synthetic.main.home_page.chat_btn
import kotlinx.android.synthetic.main.home_page.home_btn
import kotlinx.android.synthetic.main.item_detail_small.view.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

private lateinit var fusedLocationClient: FusedLocationProviderClient


class HomePage : AppCompatActivity(), View.OnClickListener,Interaction {

    // DB 접근을 위해 firestore 변수 만들어주기
    var firestore : FirebaseFirestore? = null

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var gridRecyclerViewAdapter : GridRecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel
    private var isRunning = true

    // 현재 로그인한 유저의 정보
    lateinit var userKey: String
    lateinit var userLoginedNickName: String
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
        userLoginedNickName = intent.getStringExtra("UserNickName").toString()
        userLoginedEmail = intent.getStringExtra("UserEmail").toString()
        getUserLocation() // 위치 정보 가져오기

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
            intent.putExtra("UserNickName", userLoginedNickName)
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
//        item_board_btn.setOnClickListener {
//            var intent = Intent(this, HomeSearch::class.java)
//            startActivity(intent)
//        }
        mypage_btn.setOnClickListener {
            val intent = Intent(this,MypageActivity::class.java)
            intent.putExtra("UserKey", userKey)
            startActivity(intent)
        }
        //위치 가져오기
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
        // initialize
        firestore = FirebaseFirestore.getInstance()

        home_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        home_recyclerview.layoutManager = LinearLayoutManager(this)
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

    fun getUserLocation() {
        userDB.get().addOnSuccessListener {
            user_location.text =
                it.child(userKey).getValue(User::class.java)?.userLocation.toString()
        }
    }

    // RecyclerView adapter 만들기
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // 정보들 담을 리스트 생성
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()

                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                }
                // 값을 새로 보기 위해서 새로고침 해주기
                notifyDataSetChanged()
            }
        }

        override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail_small, p0, false)
            return CustomViewHolder(view)
        }

        inner class CustomViewHolder(view: View) : RecyclerView.ViewHolder(view)

        override fun getItemCount(): Int {
            return contentDTOs.size
        }


        // server 데이터 mapping 시켜주기
        override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
            var viewholder = (p0 as CustomViewHolder).itemView

            // UserId
//            viewholder.detailviewitem_profile_textview.text = contentDTOs!![p1].userId

            // Image
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.small_detailviewitem_imageview_content)

            // Explain of content
//            viewholder.detailviewitem_explain_textview.text = contentDTOs!![p1].explain

            // item name
            viewholder.small_item_name.text = contentDTOs!![p1].itemname

            // item category
            viewholder.small_detailviewitem_category_textview.text = contentDTOs!![p1].category

            // item price
            viewholder.small_item_price.text = contentDTOs!![p1].price + "원"

            // likes
//            viewholder.detailviewitem_favoritecounter_textview.text = "Likes " + contentDTOs!![p1].favoriteCount

            //ProfileImage
//            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.detailviewitem_profile_image)

            // 간격 조절
            val layoutParams = p0.itemView.layoutParams
            layoutParams.height = 350
            p0.itemView.requestLayout()

            // 리사이클러뷰 아이템 클릭
            p0.itemView.setOnClickListener {
                var intent = Intent(this@HomePage,ItemDetailPage::class.java)
                intent.putExtra("data", contentDTOs[p1])
                startActivity(intent)
                }

        }
    }
}