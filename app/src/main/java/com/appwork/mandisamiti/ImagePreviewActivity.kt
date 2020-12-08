package com.appwork.mandisamiti

import android.os.Bundle
import android.transition.Fade
import android.view.MenuItem
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.appwork.model.TransactionModel
import com.appwork.utils.DateUtils
import com.appwork.utils.StringValues
import com.bumptech.glide.Glide
import java.util.*

class ImagePreviewActivity : AppCompatActivity() {
    private var tvName: TextView? = null
    private var tvDate: TextView? = null
    private var tvAmount: TextView? = null
    private var tvType: TextView? = null
    private var imgUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_image_preview)
        //Map View
        tvName = findViewById(R.id.tv_name_val)
        tvDate = findViewById(R.id.tv_date_value)
        tvAmount = findViewById(R.id.tv_amount_value)
        tvType = findViewById(R.id.tv_transaction_type_value)
        val fade = Fade()
        val decor = window.decorView
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade
        val imgPreview = findViewById<ImageView>(R.id.iv_preview)
        //Intent
        val bundle = intent.getBundleExtra("myBundle")
        if (bundle != null) {
            val model = bundle.getSerializable(StringValues.KEY_IMAGE_URI) as TransactionModel
            imgUri = model.tranAttachment
            tvName!!.text = model.tranReason
            tvDate!!.text = DateUtils.getStringCurrentDate(model.tranDate)
            tvAmount!!.text = String.format(Locale.getDefault(), "%s%s", "₹ ", model.transAmount)
            if (model.tranIsGiven.equals("true", ignoreCase = true)) {
                tvType!!.text = "Gave"
            } else {
                tvType!!.text = "Got"
            }
        }

        Glide.with(this)
                .load(imgUri)
                .centerCrop()
                .placeholder(R.drawable.ic_user)
                .into(imgPreview)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val homeId = item.itemId
        if (homeId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }
}