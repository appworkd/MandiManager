package com.appwork.data.entities

import android.util.Log
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable
import java.text.SimpleDateFormat
import java.util.*

@Entity(tableName = "table_orders")
data class OrderModel(var entryName: String? = null,
                      var numberOfPieces: String? = null,
                      var wtPerPiece: String? = null,
                      var remainingWt: String? = null,
                      var totalWt: String? = null,
                      var totalAmount: String? = null,
                      var lbRate: String? = null,
                      var lbCharge: String? = null,
                      var adatCharge: String? = null,
                      var commodityRate: String? = null,
                      var fare: String? = null,
                      var prevAmount: String? = null,
                      var grandTotal: String? = null,
                      var entryDate: Long = 0,
                      var entryAttachment: String? = null,
                      @ColumnInfo(name = "Diya")
                      var isGave: Boolean = false) : Serializable, Comparable<OrderModel> {
    @PrimaryKey(autoGenerate = true)
    var orderId: Int = 0
    override fun compareTo(other: OrderModel): Int {
        Log.e("OrderModel "," Date : Epoch $entryDate +\n anotherDate : $other.entryDate")
        return entryDate.compareTo(other.entryDate)
    }

    fun parseDateFormat(): String {
        Log.e("OrderModel "," Date : Epoch $entryDate")
       return SimpleDateFormat("yyyy-MM-dd",Locale.getDefault()).format(Date(entryDate))
    }


}