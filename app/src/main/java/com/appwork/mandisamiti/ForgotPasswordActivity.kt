package com.appwork.mandisamiti

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.appwork.utils.UiUtils
import com.appwork.utils.ValidationUtils
import com.google.android.material.textfield.TextInputLayout

class ForgotPasswordActivity : AppCompatActivity(), View.OnClickListener {
    private var edtEmail: EditText? = null
    private var tilEmail: TextInputLayout? = null
    private var btnSubmit: Button? = null
    private var forgotPasswordParent: ConstraintLayout? = null
    private var validations: ValidationUtils? = null
    private var isEmailValid = false
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_forgot_password)
        //Mapping
        edtEmail = findViewById(R.id.edt_email_register)
        tilEmail = findViewById(R.id.til_email)
        btnSubmit = findViewById(R.id.btn_submit)
        forgotPasswordParent = findViewById(R.id.forgotPasswordContainer)
        //Class initialization
        validations = ValidationUtils(this)
        UiUtils.checkKeyBoard(this, forgotPasswordParent)
        //Set Listeners
        btnSubmit!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        if (v === btnSubmit) {
            UiUtils.hideSoftKeyboard(v, this)
            //Todo apply validation on email and password
            emailValidations()
            if (isEmailValid) {
            }
        }
    }

    private fun emailValidations() {
        isEmailValid = validations!!.validateEmail(forgotPasswordParent, tilEmail, edtEmail)
    }
}