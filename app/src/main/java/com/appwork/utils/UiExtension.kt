package com.appwork.utils

import android.content.Context
import android.content.DialogInterface
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import java.lang.Exception

/**
 * Created by Vivek Kumar belongs to APP WORK  on 05-12-2020.
 */

fun View.showSnackBar(msg: String, length: Int = Snackbar.LENGTH_LONG) {
    Snackbar.make(this, msg, length).show()
}

fun View.visible() {
    if (this.visibility == View.GONE || this.visibility == View.INVISIBLE) {
        this.visibility = View.VISIBLE
    } else {
        this.visibility = View.GONE
    }
}

fun Context.showToast(msg: String, length: Int = Toast.LENGTH_LONG) {
    Toast.makeText(this, msg, length).show()
}

fun View.hideKeyboard(): Boolean {
    return try {
        val inputMethodManager = this.context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(this.windowToken, 0)
    } catch (e: Exception) {
        false
    }
}

fun View.showKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    this.requestFocus()
    imm.showSoftInput(this, 0)
}

fun Context.createDialog(msg: String,
                                            title: String,
                                            ok: String,
                                            cancel: String,
                                            cancelable: Boolean,
                                            positiveCLick: DialogInterface.OnClickListener,
                                            negativeCLick: DialogInterface.OnClickListener): MaterialAlertDialogBuilder {

    return MaterialAlertDialogBuilder(this)
            .setTitle(title)
            .setMessage(msg)
            .setCancelable(cancelable)
            .setPositiveButton(ok, positiveCLick)
            .setNegativeButton(cancel, negativeCLick)
}