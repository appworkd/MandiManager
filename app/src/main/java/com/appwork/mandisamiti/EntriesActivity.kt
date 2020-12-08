package com.appwork.mandisamiti

import android.content.Intent
import android.os.Bundle
import android.transition.Fade
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.EntryAdapter
import com.appwork.database.MandiManagement
import com.appwork.model.TransactionModel
import com.appwork.utils.RecyclerViewUtils
import com.appwork.utils.StringValues
import java.util.*

class EntriesActivity : AppCompatActivity(), View.OnClickListener {
    private var txtGiveGet: TextView? = null
    private var txtTotalAmount: TextView? = null

    //Classes
    private var adapter: EntryAdapter? = null
    private var entryList: List<TransactionModel?>? = null
    private var userId: Long = 0
    private var db: MandiManagement? = null
    private var parentLayout: ConstraintLayout? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entries)
        //MappingView
        txtGiveGet = findViewById(R.id.txt_give_get)
        txtTotalAmount = findViewById(R.id.txt_total_amount)
        val btnGet = findViewById<Button>(R.id.btn_get)
        val btnGive = findViewById<Button>(R.id.btn_gave)
        val rvEntry = findViewById<RecyclerView>(R.id.rv_entries)
        val tlEntry = findViewById<Toolbar>(R.id.tl_entry)
        parentLayout = findViewById(R.id.cont_entry)
        entryList = ArrayList()
        adapter = EntryAdapter(this)
        val fade = Fade()
        fade.excludeTarget(android.R.id.statusBarBackground, true)
        fade.excludeTarget(android.R.id.navigationBarBackground, true)
        window.enterTransition = fade
        window.exitTransition = fade
        //Intent Data
        if (intent.hasExtra("userId")) {
            userId = intent.getLongExtra(StringValues.USER_ID, -1)
        }
        RecyclerViewUtils.setUpRecyclerview(this, rvEntry, RecyclerView.VERTICAL)
        rvEntry.adapter = adapter
        //Set On click
        btnGet.setOnClickListener(this)
        btnGive.setOnClickListener(this)
        db = MandiManagement(this)
        setSupportActionBar(tlEntry)
    }

    override fun onResume() {
        super.onResume()
        entryList = db!!.getTranList(userId)
        if (!entryList.isNullOrEmpty()) {
            (entryList as List<TransactionModel>).sortedWith(compareBy { it.tranDate })
            adapter!!.setDataList(entryList)
        }
        if (entryList!!.isNotEmpty()) {
            calculateEntriesTotal()
        }
    }

    private fun calculateEntriesTotal() {
        var amountGave = 0
        var amountGot = 0
        for (model in entryList!!) {
            if (model!!.tranIsGiven.equals("true", ignoreCase = true)) {
                amountGave += model.transAmount!!.toInt()
            } else {
                amountGot = amountGot + model.transAmount!!.toInt()
            }
        }
        if (amountGave > amountGot) {
            val amount = amountGave - amountGot
            txtTotalAmount!!.text = String.format(Locale.getDefault(), "%s%s", "₹ ", amount)
            txtGiveGet!!.text = resources.getString(R.string.str_u_give)
            txtTotalAmount!!.setTextColor(this.resources.getColor(R.color.colorGive))
        } else {
            val amount = amountGot - amountGave
            txtTotalAmount!!.text = String.format(Locale.getDefault(), "%s%s", "₹ ", amount)
            txtGiveGet!!.text = resources.getString(R.string.str_u_get)
            txtTotalAmount!!.setTextColor(this.resources.getColor(R.color.colorGet))
        }
    }

    override fun onStop() {
        super.onStop()
    }

    override fun onClick(v: View) {
        val id = v.id
        when (id) {
            R.id.btn_get -> {
                val intent = Intent(this, TransactionLogActivity::class.java)
                intent.putExtra(StringValues.GAVE, false)
                intent.putExtra(StringValues.USER_ID, userId)
                startActivity(intent)
            }
            R.id.btn_gave -> {
                val intent2 = Intent(this, TransactionLogActivity::class.java)
                intent2.putExtra(StringValues.GAVE, true)
                intent2.putExtra(StringValues.USER_ID, userId)
                startActivity(intent2)
            }
        }
    }
}