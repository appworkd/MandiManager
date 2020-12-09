package com.appwork.data.entities

data class EntryModel(var entryId: Long = 0,
                      var entryName: String? = null,
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
                      var isGave: Boolean = false) : Comparable<EntryModel> {
    override fun compareTo(other: EntryModel): Int {
        return entryDate.compareTo(other.entryDate)
    }

}