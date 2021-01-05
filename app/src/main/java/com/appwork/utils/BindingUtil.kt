package com.appwork.utils

import android.net.Uri
import android.widget.ImageView
import androidx.databinding.BindingAdapter
import com.bumptech.glide.Glide

/**
 * Created by Vivek Kumar belongs to APP WORK  on 14-12-2020.
 */

@BindingAdapter("imageUrl")
fun loadImage(view: ImageView, uri: Uri) {
    Glide.with(view)
            .load(uri)
            .into(view)
}

