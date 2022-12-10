package com.example.belida

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.example.belida.model.ContentDTO
import kotlinx.android.synthetic.main.item_detail.*
import kotlinx.android.synthetic.main.item_detail_small.view.*

class ItemDetailPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.item_detail)

        val datas = intent.getSerializableExtra("data") as ContentDTO

        // photo
        Glide.with(this).load(datas.imageUrl).into(detailviewitem_imageview_content)
        // item_name
        item_name.text = datas.itemname
        // category
        item_category.text = datas.category
        // explain
        detailviewitem_explain_textview.text = datas.explain
        // price
        item_price.text = datas.price + "원"

    }
}