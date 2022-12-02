package com.example.belida

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.*
import androidx.recyclerview.widget.GridLayoutManager
import androidx.viewpager2.widget.ViewPager2
import kotlinx.android.synthetic.main.home_page.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomePage : AppCompatActivity(), View.OnClickListener,Interaction {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var gridRecyclerViewAdapter : GridRecyclerViewAdapter
    lateinit var viewModel: MainActivityViewModel
    private var isRunning = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.home_page)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        viewModel.setBannerItems(fakeBannerItemList)
        viewModel.setGridItems(fakeGridItemList)

        iv_hamburger.setOnClickListener(this)
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
        home_search_btn.setOnClickListener{
            val intent = Intent(this,HomeSearch::class.java)
            startActivity(intent)
        }
        mypage_btn.setOnClickListener{
            val intent = Intent(this,MypageActivity::class.java)
            startActivity(intent)
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

    override fun onClick(p0: View?) {
        p0?.let {
            when (it.id) {
                R.id.iv_hamburger -> {

                }
            }
        }
    }
}


//    private lateinit var appBarConfiguration: AppBarConfiguration
//    private lateinit var binding: ActivityMainBinding
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        binding = ActivityMainBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        setSupportActionBar(binding.toolbar)
//
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        appBarConfiguration = AppBarConfiguration(navController.graph)
//        setupActionBarWithNavController(navController, appBarConfiguration)
//
//        binding.fab.setOnClickListener { view ->
//            Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
//                .setAction("Action", null).show()
//        }
//    }
//
//    override fun onCreateOptionsMenu(menu: Menu): Boolean {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.menu_main, menu)
//        return true
//    }
//
//    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        return when (item.itemId) {
//            R.id.action_settings -> true
//            else -> super.onOptionsItemSelected(item)
//        }
//    }
//
//    override fun onSupportNavigateUp(): Boolean {
//        val navController = findNavController(R.id.nav_host_fragment_content_main)
//        return navController.navigateUp(appBarConfiguration)
//                || super.onSupportNavigateUp()
//    }
//}