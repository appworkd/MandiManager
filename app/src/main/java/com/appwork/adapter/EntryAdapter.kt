package com.appwork.adapter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.EntryAdapter.EntryVH
import com.appwork.mandisamiti.ImagePreviewActivity
import com.appwork.mandisamiti.R
import com.appwork.model.TransactionModel
import com.appwork.utils.CircleImageView
import com.appwork.utils.DateUtils
import com.appwork.utils.StringValues
import com.appwork.utils.StringValues.KEY_IMAGE_URI
import com.bumptech.glide.Glide
import java.util.*

class EntryAdapter(private val context: Context) : RecyclerView.Adapter<EntryVH>() {
    private var entryList: List<TransactionModel?>? = null
    fun setDataList(entryList: List<TransactionModel?>?) {
        this.entryList = entryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EntryVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_entries,
                parent,
                false)
        return EntryVH(view)
    }

    override fun onBindViewHolder(holder: EntryVH, position: Int) {
        holder.setData(position)
    }

    override fun getItemCount(): Int {
        return if (entryList == null) {
            0
        } else {
            entryList!!.size
        }
    }

    inner class EntryVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val imgEntry: CircleImageView = itemView.findViewById(R.id.img_entry)
        private val tvEntryName: TextView = itemView.findViewById(R.id.txt_entry_title)
        private val tvEntryDate: TextView = itemView.findViewById(R.id.txt_entry_time)
        private val tvGave: TextView = itemView.findViewById(R.id.txt_given_money)
        private val tvGet: TextView = itemView.findViewById(R.id.txt_get)
        private val cont: CardView = itemView.findViewById(R.id.cont_img)
        fun setData(position: Int) {
            val entry = entryList!![position]
            if (entry?.tranAttachment == null || entry.tranAttachment!!.isEmpty()) {
                cont.visibility = View.GONE
            } else {
                cont.visibility = View.VISIBLE
                Glide.with(context)
                        .load(entry.tranAttachment)
                        .centerCrop()
                        .placeholder(R.drawable.ic_user)
                        .into(imgEntry)
            }
            tvEntryName.text = entry?.tranReason!!
            if (entry.tranIsGiven.equals("true", ignoreCase = true)) {
                tvGave.text = String.format(Locale.getDefault(), "%s%s", "₹ ", entry.transAmount)
                tvGet.text = ""
            } else {
                tvGet.text = String.format(Locale.getDefault(), "%s%s", "₹ ", entry.transAmount)
                tvGave.text = ""
            }
            tvEntryDate.text = DateUtils.getStringCurrentDate(entry.tranDate)
        }

        init {
            imgEntry.setOnClickListener { v: View? ->
                val bundle= Bundle()
                bundle.putSerializable(KEY_IMAGE_URI,entryList!![adapterPosition])
                val intent = Intent(context, ImagePreviewActivity::class.java)
                intent.putExtra("myBundle", bundle)
                val compat = ActivityOptionsCompat.makeSceneTransitionAnimation(
                        (context as Activity),
                        imgEntry,
                        Objects.requireNonNull(ViewCompat.getTransitionName(imgEntry))!!)
                context.startActivity(intent, compat.toBundle())
            }
        }
    }
}