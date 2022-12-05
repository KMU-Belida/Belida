package com.example.belida

import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.example.belida.model.ContentDTO

interface Interaction: View.OnClickListener {
//    val container: ViewGroup?
//    val activity: Context

//    abstract val contentDTOs: ArrayList<ContentDTO>

    fun onBannerItemClicked(bannerItem: BannerItem)
}