package com.example.belida

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class Holder(itemView: View): RecyclerView.ViewHolder(itemView) {
    var idTextView = itemView.findViewById<TextView>(R.id.iv_grid_image)
    var textTitleTextView = itemView.findViewById<TextView>(R.id.tv_grid_title)

    fun setText(listData : ListData){
        idTextView.text = "${listData.cat_image}"
        textTitleTextView.text = listData.cat_title
    }
}