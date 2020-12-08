package com.appwork.utils

import android.content.Context
import android.content.DialogInterface
import com.appwork.mandisamiti.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {
    fun showDialogBox(
            context: Context,
            msg: String?,
            positiveLabel: String?,
            positiveClick: DialogInterface.OnClickListener?,
            negativeLabel: String?,
            negativeListener: DialogInterface.OnClickListener?,
            isCancelable: Boolean): MaterialAlertDialogBuilder {
        val builder = MaterialAlertDialogBuilder(context)
        builder.setTitle(context.resources.getString(R.string.app_name))
                .setCancelable(isCancelable)
                .setPositiveButton(positiveLabel, positiveClick)
                .setNegativeButton(negativeLabel, negativeListener)
                .setMessage(msg)
        return builder
    }
}