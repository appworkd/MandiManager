package com.appwork.utils

import android.content.Context
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

object RecyclerViewUtils {
    fun setUpRecyclerview(context: Context?, recyclerView: RecyclerView?, orientation: Int) {
        val layoutManager = LinearLayoutManager(context, orientation, false)
        recyclerView!!.layoutManager = layoutManager
    }
}