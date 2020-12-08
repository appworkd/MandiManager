package com.appwork.databaseUtils

import android.provider.BaseColumns

class UserContract private constructor() {
    object UserEntry : BaseColumns {
        const val TABLE_USER = "user_details_table"
        const val USER_ID = "user_id"
        const val USER_NAME = "user_name"
        const val USER_EMAIL = "user_email"
        const val USER_PHONE = "user_phone"
        const val USER_PASSWORD = "user_password"
        const val USER_IMAGE = "user_image"
        const val USER_TIL_NUMBER = "user_til_name"
        const val USER_DESC = "user_desc"
        const val USER_ADDRESS = "user_address"
    }
}