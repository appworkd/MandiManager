package com.appwork.utils

import android.content.Context
import android.graphics.Color
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import com.appwork.mandisamiti.R
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout

object UiUtils {
    fun createSnackBar(context: Context, view: ViewGroup?, msg: String?) {
        Snackbar.make(view!!.rootView, msg!!, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(context.resources.getColor(R.color.colorAccent))
                .setTextColor(Color.WHITE)
                .show()
    }

    fun hideTilError(context: Context?, view: View) {
        if (view is TextInputLayout) {
            if (view.isEnabled()) {
                view.setEnabled(false)
            }
        }
    }

    fun checkKeyBoard(context: Context?, viewGroup: ViewGroup?): Boolean {
        viewGroup!!.viewTreeObserver.addOnGlobalFocusChangeListener { oldFocus: View?, newFocus: View? ->
            val rect = Rect()
            viewGroup.getWindowVisibleDisplayFrame(rect)
            //Height of view
            val screenHeight = viewGroup.rootView.height
            val keyBoardHeight = screenHeight - rect.bottom
            if (keyBoardHeight > screenHeight * 0.15) {
            } else {
            }
        }
        return false
    }

    fun hideSoftKeyboard(view: View?, context: Context) {
        val inputManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        inputManager?.hideSoftInputFromWindow(view!!.windowToken, 0)
    }
}