package com.example.belida

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.belida.model.ContentDTO
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.item_board.*
import kotlinx.android.synthetic.main.item_detail.view.*

class BoardActivity : AppCompatActivity() {

    // DB 접근을 위해 firestore 변수 만들어주기
    var firestore : FirebaseFirestore? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_board)
//        val view: LayoutInflater = getLayoutInflater()


        // initialize
        firestore = FirebaseFirestore.getInstance()

        detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
        detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(this)

//        view.detailviewfragment_recyclerview.adapter = DetailViewRecyclerViewAdapter()
//        view.detailviewfragment_recyclerview.layoutManager = LinearLayoutManager(activity)

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
            var view = LayoutInflater.from(p0.context).inflate(R.layout.item_detail, p0, false)
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
            viewholder.detailviewitem_profile_textview.text = contentDTOs!![p1].userId

            // Image
            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.detailviewitem_imageview_content)

            // Explain of content
            viewholder.detailviewitem_explain_textview.text = contentDTOs!![p1].explain

            // item name
            viewholder.item_name.text = contentDTOs!![p1].itemname

            // item category
            viewholder.item_category.text = contentDTOs!![p1].category

            // item price
            viewholder.item_price.text = contentDTOs!![p1].price

            // likes
//            viewholder.detailviewitem_favoritecounter_textview.text = "Likes " + contentDTOs!![p1].favoriteCount

            //ProfileImage
//            Glide.with(p0.itemView.context).load(contentDTOs!![p1].imageUrl).into(viewholder.detailviewitem_profile_image)

        }

    }
}