package com.example.belida

import android.view.View

interface Interaction: View.OnClickListener {
    fun onBannerItemClicked(bannerItem: BannerItem)
}