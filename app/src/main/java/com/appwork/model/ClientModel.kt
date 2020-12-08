package com.appwork.model

data class ClientModel(var clientId: Long = 0,
                       var parentId: Long = 0,
                       var clientName: String? = null,
                       var clientNumber: String? = null,
                       var clientAddress: String? = null,
                       var clientAmount: String? = null,
                       var clientImg: String? = null,
                       var isGave: String? = null) : Comparable<ClientModel> {
    override fun compareTo(other: ClientModel): Int {
        return clientName!!.compareTo(other.clientName!!)
    }
}