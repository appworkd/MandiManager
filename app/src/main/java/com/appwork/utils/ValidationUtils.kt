package com.appwork.utils

import android.content.Context
import android.content.res.Resources
import android.util.Patterns
import android.view.ViewGroup
import android.widget.EditText
import com.appwork.mandisamiti.R
import com.google.android.material.textfield.TextInputLayout

class ValidationUtils(private val context: Context) {
    private val resources: Resources
    fun validateName(textInputLayout: TextInputLayout?, editText: EditText?): Boolean {
        val value = editText!!.text.toString().trim { it <= ' ' }
        return if (value.isEmpty()) {
            textInputLayout!!.isEnabled = true
            textInputLayout.error = resources.getString(R.string.validation_empty)
            false
        } else {
            true
        }
    }

    fun validateNumber(textInputLayout: TextInputLayout?, editText: EditText?): Boolean {
        val value = editText!!.text.toString().trim { it <= ' ' }
        if (value.isEmpty()) {
            textInputLayout!!.isEnabled = true
            textInputLayout.error = resources.getString(R.string.validation_empty)
            return false
        }
        return if (value.length < 10 || value.length > 14) {
            textInputLayout!!.isEnabled = true
            textInputLayout.error = resources.getString(R.string.validation_number)
            false
        } else {
            true
        }
    }

    fun validateEmail(parent: ViewGroup?, tilEmail: TextInputLayout?, edtEmail: EditText?): Boolean {
        val emailValue = edtEmail!!.text.toString().trim { it <= ' ' }
        if (emailValue.isEmpty()) {
            UiUtils.createSnackBar(context, parent, resources.getString(R.string.email_validation_empty))
            tilEmail!!.isEnabled = true
            tilEmail.error = resources.getString(R.string.email_validation_empty)
            return false
        }
        return if (!Patterns.EMAIL_ADDRESS.matcher(emailValue).matches()) {
            UiUtils.createSnackBar(context, parent, resources.getString(R.string.email_validation_type))
            tilEmail!!.isEnabled = true
            tilEmail.error = resources.getString(R.string.email_validation_type)
            false
        } else {
            true
        }
    }

    fun validatePassword(parent: ViewGroup?, tilPassword: TextInputLayout?, edtPassword: EditText?): Boolean {
        val emailValue = edtPassword!!.text.toString().trim { it <= ' ' }
        return if (emailValue.isEmpty()) {
            UiUtils.createSnackBar(context, parent, resources.getString(R.string.pass_validation_empty))
            tilPassword!!.isEnabled = true
            tilPassword.error = resources.getString(R.string.pass_validation_empty)
            false
        } else {
            true
        }
    }

    init {
        resources = context.resources
    }
}