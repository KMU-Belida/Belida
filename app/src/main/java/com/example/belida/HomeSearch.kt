package com.example.belida

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belida.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_home_search.*
import kotlinx.android.synthetic.main.home_page.*
import kotlinx.android.synthetic.main.item_detail.view.*
import kotlinx.android.synthetic.main.item_detail_small.view.*

class HomeSearch : AppCompatActivity() {

    // DB 접근을 위해 firestore 변수 만들어주기
    var firestore : FirebaseFirestore? = null

    // 검색 텍스트
    var fillter_text : String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_search)

        filter_btn.setOnClickListener {
            val intent = Intent(this,Filter::class.java)
            startActivity(intent)
        }

        // initialize
        firestore = FirebaseFirestore.getInstance()

        search_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        search_recyclerview.layoutManager = LinearLayoutManager(this)

        // 검색하기 버튼 누를때
        run_search_button.setOnClickListener {
            fillter_text = search_txt.text.toString()
            search_recyclerview.adapter = DetailViewRecyclerViewAdapter()
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
                    if(fillter_text == null){
                        contentDTOs.add(item!!)
                        contentUidList.add(snapshot.id)
                    }
                    else{
                        if(item?.category.toString().contains(fillter_text!!) || item?.itemname.toString().contains(fillter_text!!)){
                            contentDTOs.add(item!!)
                            contentUidList.add(snapshot.id)
                        }
                    }
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
                var intent = Intent(this@HomeSearch,ItemDetailPage::class.java)
                intent.putExtra("data", contentDTOs[p1])
                startActivity(intent)
            }

        }
    }
}