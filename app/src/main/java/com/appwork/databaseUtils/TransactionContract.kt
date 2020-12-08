package com.appwork.databaseUtils

import android.provider.BaseColumns

class TransactionContract {
    object TransactionColumns : BaseColumns {
        const val TABLE_TRANSACTION = "table_transaction"
        const val TRAN_ID = "trans_id"
        const val TRAN_USER_ID = "trans_user_id"
        const val TRAN_DATE = "trans_date"
        const val TRAN_REASON = "trans_reason"
        const val TRAN_AMOUNT = "trans_amount"
        const val TRAN_ATTACHMENT = "trans_attachment"
        const val TRAN_IS_GIVEN = "trans_is_given"
    }
}