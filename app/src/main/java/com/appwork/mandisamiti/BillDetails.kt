package com.appwork.mandisamiti

import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Resources
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.appwork.database.MandiManagement
import com.appwork.databaseUtils.EntryContract.EntryColumns
import com.appwork.data.entities.ClientModel
import com.appwork.data.entities.EntryModel
import com.appwork.utils.DateUtils
import com.appwork.utils.StringValues
import com.appwork.utils.UiUtils
import java.net.URLEncoder
import java.util.*

class BillDetails : AppCompatActivity(), View.OnClickListener {
    private var tvPerson: TextView? = null
    private var tvPhone: TextView? = null
    private var tvAddress: TextView? = null
    private var tvDate: TextView? = null
    private var tvEntryId: TextView? = null
    private var tvComName: TextView? = null
    private var tvPieces: TextView? = null
    private var tvWtPerP: TextView? = null
    private var tvRemainWt: TextView? = null
    private var tvTotalWt: TextView? = null
    private var tvAmount: TextView? = null
    private var tvLbCharge: TextView? = null
    private var tvMandiCharge: TextView? = null
    private var tvCommodityAmount: TextView? = null
    private var tvFare: TextView? = null
    private var tvPreAmount: TextView? = null
    private var tvPaidAmount: TextView? = null
    private var btnShare: Button? = null

    //String
    private var entryId: Long = -1
    private var resource: Resources? = null
    private var clientId: Long = -1
    private var clientNumber: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bill_details)
        //Mapping View
        tvPerson = findViewById(R.id.tv_name)
        tvPhone = findViewById(R.id.tv_phone_number)
        tvAddress = findViewById(R.id.tv_address)
        tvDate = findViewById(R.id.tv_date)
        tvEntryId = findViewById(R.id.tv_entry_id_value)
        tvComName = findViewById(R.id.tv_com_value)
        tvPieces = findViewById(R.id.tv_pieces_value)
        tvWtPerP = findViewById(R.id.tv_wt_piece_value)
        tvRemainWt = findViewById(R.id.tv_remain_wt_value)
        tvTotalWt = findViewById(R.id.tv_total_wt_value)
        tvAmount = findViewById(R.id.tv_total_amount_value)
        tvLbCharge = findViewById(R.id.tv_total_lcharge_value)
        tvMandiCharge = findViewById(R.id.tv_mandi_ch_value)
        tvCommodityAmount = findViewById(R.id.tv_commodity_amount_value)
        tvFare = findViewById(R.id.tv_fare_value)
        tvPreAmount = findViewById(R.id.tv_prev_amount_value)
        tvPaidAmount = findViewById(R.id.tv_final_amount_value)
        btnShare = findViewById(R.id.btn_share)
        val parent = findViewById<ConstraintLayout>(R.id.bill_parent)
        btnShare!!.setOnClickListener(this)
        //IntentValues
        if (intent.hasExtra(EntryColumns.ENTRY_ID)) {
            entryId = intent.getLongExtra(EntryColumns.ENTRY_ID, -1)
        }
        if (intent.hasExtra(StringValues.USER_ID)) {
            clientId = intent.getLongExtra(StringValues.USER_ID, -1)
        }
        if (intent.hasExtra(StringValues.USER_NUM)) {
            clientNumber = intent.getStringExtra(StringValues.USER_NUM)
        }
        //Database
        resource = this.resources
        val database = MandiManagement(this)
        val model = database.getBillDetails(entryId)
        val clientModel = database.getClient(clientId)
        if (clientModel.clientName == null) {
            UiUtils.createSnackBar(this, parent, "Something went wrong.")
        } else {
            setPersonalDetails(clientModel)
        }
        if (model.entryName == null) {
            UiUtils.createSnackBar(this, parent, "Something went wrong.")
        } else {
            setData(model)
        }
    }

    private fun setPersonalDetails(clientModel: ClientModel?) {
        tvPerson!!.setText(clientModel!!.clientName)
        tvPhone!!.setText(clientModel.clientNumber)
        tvAddress!!.setText(clientModel.clientAddress)
    }

    private fun setData(model: EntryModel?) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = model!!.entryDate
        val date = DateUtils.getStringCurrentDate(model.entryDate)
        val rupee = resource!!.getString(R.string.txt_indian_rupee)
        tvDate!!.text = date
        tvComName!!.setText(model.entryName)
        tvEntryId!!.text = String.format(Locale.getDefault(), "%s%s", "", model.entryId)
        tvPieces!!.setText(model.numberOfPieces)
        tvWtPerP!!.text = String.format(Locale.getDefault(), "%s%s", model.wtPerPiece, "Kgs")
        tvRemainWt!!.text = String.format(Locale.getDefault(), "%s%s", model.remainingWt, "Kgs")
        tvTotalWt!!.text = String.format(Locale.getDefault(), "%s%s", model.totalWt, "Kgs")
        tvAmount!!.text = String.format(Locale.getDefault(), "%s%s", model.totalAmount, rupee)
        tvLbCharge!!.text = String.format(Locale.getDefault(), "%s%s", model.lbCharge, rupee)
        tvMandiCharge!!.text = String.format(Locale.getDefault(), "%s%s", model.adatCharge, rupee)
        tvCommodityAmount!!.text = String.format(Locale.getDefault(), "%s%s", model.commodityRate, rupee)
        tvFare!!.text = String.format(Locale.getDefault(), "%s%s", model.fare, rupee)
        tvPreAmount!!.text = String.format(Locale.getDefault(), "%s%s", model.prevAmount, rupee)
        tvPaidAmount!!.text = String.format(Locale.getDefault(), "%s%s", model.grandTotal, rupee)
    }

    override fun onClick(v: View) {
        if (v === btnShare) {
            //todo share whatsapp
            if (isAppInstalled("com.whatsapp")) {
                try {
                    val sendIntent = Intent()
                    sendIntent.action = Intent.ACTION_VIEW
                    val url = ("https://api.whatsapp.com/send?phone=" + clientNumber + "&text="
                            + URLEncoder.encode("This is my text to send.", "UTF-8"))
                    sendIntent.setPackage("com.whatsapp")
                    sendIntent.data = Uri.parse(url)
                    if (sendIntent.resolveActivity(packageManager) != null) {
                        startActivity(sendIntent)
                    }
                    startActivity(sendIntent)
                } catch (e: Exception) {
                    e.message
                }
            }
        }
    }

    fun isAppInstalled(uri: String?): Boolean {
        val packageManager = packageManager
        return try {
            packageManager.getPackageInfo(uri, PackageManager.GET_ACTIVITIES)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            e.message
            false
        }
    }
}