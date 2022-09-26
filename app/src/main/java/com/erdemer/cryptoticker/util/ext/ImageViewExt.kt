package com.erdemer.cryptoticker.util.ext

import android.widget.ImageView
import com.bumptech.glide.Glide
import com.erdemer.cryptoticker.R


fun ImageView.loadImage(url: String) {
    Glide.with(this.context)
        .load(url)
        .placeholder(R.drawable.ic_image_placeholder)
        .error(R.drawable.ic_image_error_placeholder)
        .fitCenter()
        .into(this)
}

fun ImageView.setPriceChangeIcon(priceChange: Double) {
    if (priceChange > 0) {
        this.setImageResource(R.drawable.ic_arrow_up)
    } else if (priceChange < 0) {
        this.setImageResource(R.drawable.ic_arrow_down)
    } else {
        this.gone()
    }
}