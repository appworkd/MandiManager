package com.appwork.databaseUtils

import android.provider.BaseColumns

class ClientContract {
    object ClientValues : BaseColumns {
        const val TABLE_CLIENT = "table_client"
        const val CLIENT_ID = "client_id"
        const val CLIENT_PARENT_ID = "client_parent_id"
        const val CLIENT_NAME = "client_name"
        const val CLIENT_NUMBER = "client_number"
        const val CLIENT_ADDRESS = "client_address"
        const val CLIENT_IMAGE = "client_image"
        const val CLIENT_IS_GAVE = "client_is_gave"
        const val CLIENT_AMOUNT = "client_amount"
    }
}