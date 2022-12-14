package com.example.belida

import android.Manifest
import android.annotation.SuppressLint
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

    // category fillter ?????? ??????
    var category_fillter : String? = null

    // DB ????????? ?????? firestore ?????? ???????????????
    var firestore : FirebaseFirestore? = null

    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var gridRecyclerViewAdapter : GridRecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel
    private var isRunning = true

    // ?????? ???????????? ????????? ??????
    lateinit var userKey: String
    lateinit var userLoginedNickName: String
    lateinit var userLoginedEmail: String

    //?????? ????????????
    private val database = Firebase.database
    private val userDB = database.getReference("user")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.setBannerItems(fakeBannerItemList)
        viewModel.setGridItems(fakeGridItemList)

        userKey = intent.getStringExtra("UserKey").toString() // ?????? ???????????? userKey???
        userLoginedNickName = intent.getStringExtra("UserNickName").toString()
        userLoginedEmail = intent.getStringExtra("UserEmail").toString()
        getUserLocation() // ?????? ?????? ????????????

//        iv_hamburger.setOnClickListener(this)

        initViewPager2()
        subscribeObservers()
        autoScrollViewPager()
        //??? ??????, ???????????? ??????, ?????? ?????? ?????????
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
        //?????? ????????????
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
        //?????? ?????? ????????????????????? ??????
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            Toast.makeText(this, "?????? ????????? ??????????????????.", Toast.LENGTH_SHORT).show()
            var permissions = arrayOf(
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
            )
            ActivityCompat.requestPermissions(this, permissions, MY_PERMISSION_ACCESS_ALL)
        }
        fusedLocationClient.requestLocationUpdates(locationRequest,
            locationCallback,
            Looper.getMainLooper());

        // ?????? ?????? ??? textView ?????? ????????? ??????
        locationButton.setOnClickListener{
            fusedLocationClient.lastLocation.addOnSuccessListener { location : Location? ->
                if(location != null){
                    //addr??? ?????? ????????? ???????????? ?????? ???????????? ??????
                    var addr = geocoder.getFromLocation(location.latitude, location.longitude, 1)

                    //????????????????????? ???????????? ??? ????????? ?????????

                    var addrLocality = addr[0].subLocality

                    userDB.child(userKey).child("userLocation").setValue(addrLocality)

                    //????????? ?????? subLocality??? ??? ??????, ????????? ??????(???????????????)????????? adminArea??? ??????
                    user_location.text = addrLocality
                    Log.d("Test", "GPS Location changed, $addr")
                    fusedLocationClient.removeLocationUpdates(locationCallback);
//                    if(user_location.text != "??????"){
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
        if(intent.hasExtra("category_fillter")){
            category_fillter = intent.getSerializableExtra("category_fillter") as String
        }

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

    // RecyclerView adapter ?????????
    @SuppressLint("SuspiciousIndentation")
    inner class DetailViewRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){
        // ????????? ?????? ????????? ??????
        var contentDTOs : ArrayList<ContentDTO> = arrayListOf()
        var contentUidList : ArrayList<String> = arrayListOf()

        init {
            firestore?.collection("images")?.orderBy("timestamp", Query.Direction.DESCENDING)?.addSnapshotListener { querySnapshot, firebaseFirestoreException ->
                contentDTOs.clear()
                contentUidList.clear()

                for(snapshot in querySnapshot!!.documents){
                    var item = snapshot.toObject(ContentDTO::class.java)
                    if(category_fillter == null || category_fillter == "all"){
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    else{
                        if(item?.category == category_fillter){
                            contentDTOs.add(item!!)
                            contentUidList.add(snapshot.id)
                        }
                    }
                }
                // ?????? ?????? ?????? ????????? ???????????? ?????????
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


        // server ????????? mapping ????????????
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
            viewholder.small_item_price.text = contentDTOs!![p1].price + "???"

            // likes
//            viewholder.detailviewitem_favoritecounter_textview.text = "Likes " + contentDTOs!![p1].favoriteCount

            //ProfileImage
//            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.detailviewitem_profile_image)

            // ?????? ??????
            val layoutParams = p0.itemView.layoutParams
            layoutParams.height = 350
            p0.itemView.requestLayout()

            // ?????????????????? ????????? ??????
            p0.itemView.setOnClickListener {
                var intent = Intent(this@HomePage,ItemDetailPage::class.java)
                intent.putExtra("data", contentDTOs[p1])
                startActivity(intent)
                }

        }
    }
}