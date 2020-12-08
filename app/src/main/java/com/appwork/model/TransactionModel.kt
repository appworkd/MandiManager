package com.appwork.model

import java.io.Serializable

data class TransactionModel(
        var tranId: Long = 0,
        var tranDate: Long = 0,
        var transAmount: String? = null,
        var tranReason: String? = null,
        var tranAttachment: String? = null,
        var tranIsGiven: String? = null
) : Comparable<TransactionModel>, Serializable {
    override fun compareTo(other: TransactionModel): Int {
        return tranDate.compareTo(other.tranDate)
    }

}