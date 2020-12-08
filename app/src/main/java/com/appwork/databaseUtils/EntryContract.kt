package com.appwork.databaseUtils

import android.provider.BaseColumns

class EntryContract {
    object EntryColumns : BaseColumns {
        const val TABLE_ENTRY = "table_entry"
        const val ENTRY_ID = "entry_id"
        const val ENTRY_USER_ID = "user_id"
        const val ENTRY_NAME = "entry_name"
        const val ORG_NAME = "org_name"
        const val ENTRY_NOP = "entry_nop"
        const val ENTRY_PIECE_WT = "entry_per_piece_wt"
        const val ENTRY_REMAIN_WT = "entry_remain_wt"
        const val ENTRY_TOTAL_WT = "entry_total_wt"
        const val ENTRY_TOTAL_AMOUNT = "entry_total_amount"
        const val ENTRY_LB_RATE = "entry_lb_rate"
        const val ENTRY_TOTAL_LB_CHARGE = "entry_total_lb_charge"
        const val ENTRY_ADAT_CHARGE = "entry_adat_charge"
        const val ENTRY_COMMODITY_PRICE = "entry_commodity_price"
        const val ENTRY_FARE = "entry_fare"
        const val ENTRY_PREV_AMOUNT = "entry_prev_amount"
        const val ENTRY_GRANT_TOTAL = "entry_grand_total"
        const val ENTRY_ENTRY_DATE = "entry_date"
        const val ENTRY_ENTRY_ATTACHMENT = "entry_attachment"
        const val ENTRY_ENTRY_IS_GIVEN = "is_given"
    }
}