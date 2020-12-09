package com.appwork.data.entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "client_table")
data class ClientModel(
        var parentId: Long = 0,
        var clientName: String? = null,
        var clientNumber: String? = null,
        var clientAddress: String? = null,
        var clientAmount: String? = null,
        var clientImg: String? = null,
        @ColumnInfo(name = "Diya")
        var isGave: String? = null) : Serializable, Comparable<ClientModel> {
    @PrimaryKey(autoGenerate = true)
    var clientId: Int = 0
    override fun compareTo(other: ClientModel): Int {
        return clientName!!.compareTo(other.clientName!!)
    }
}