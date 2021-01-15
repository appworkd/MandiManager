package com.appwork.utils

import android.graphics.drawable.Drawable
import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import androidx.databinding.ObservableField
import androidx.lifecycle.MutableLiveData
import com.appwork.mandisamiti.R
import com.bumptech.glide.Glide

/**
 * Created by Vivek Kumar belongs to APP WORK  on 14-12-2020.
 */

@BindingAdapter("app:imageUrl")
fun loadImage(view: ImageView, imageUrl: String) {
    Glide.with(view)
            .load(imageUrl)
            .into(view)
}

@BindingAdapter("app:imagePath")
fun loadImageWithPath(view: ImageView, imageUrl: MutableLiveData<String>) {
    val currentImage = getImage(imageUrl.value)

    Glide.with(view)
            .load(currentImage)
            .placeholder(R.drawable.ic_user)
            .into(view)


}

fun getImage(imageUrl: String?): String {
    var imagePath: String? = ""
    imagePath = imageUrl

    return imagePath.toString()
}

