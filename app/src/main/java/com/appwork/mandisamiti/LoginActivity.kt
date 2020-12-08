package com.appwork.mandisamiti

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import com.appwork.database.MandiManagement
import com.appwork.ui.auth.RegisterScreenActivity
import com.appwork.utils.AppPreferences
import com.appwork.utils.UiUtils
import com.appwork.utils.ValidationUtils
import com.google.android.material.textfield.TextInputLayout

class LoginActivity : AppCompatActivity(), View.OnClickListener {
    private var tilEmail: TextInputLayout? = null
    private var tilPassword: TextInputLayout? = null
    private var edtEmail: EditText? = null
    private var edtPass: EditText? = null
    private var txtForgetPass: TextView? = null
    private var txtRegister: TextView? = null
    private var btnLogin: Button? = null
    private var imgApp: ImageView? = null
    private var validations: ValidationUtils? = null
    private var loginParent: ConstraintLayout? = null
    private var isEmailValid = false
    private var isPasswordValid = false
    private var database: MandiManagement? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        //Mapping
        imgApp = findViewById(R.id.imgLogin)
        tilEmail = findViewById(R.id.til_email)
        tilPassword = findViewById(R.id.til_password)
        edtEmail = findViewById(R.id.edt_email)
        edtPass = findViewById(R.id.edt_password)
        btnLogin = findViewById(R.id.btn_login)
        loginParent = findViewById(R.id.loginContainer)
        txtForgetPass = findViewById(R.id.txt_forget_password)
        txtRegister = findViewById(R.id.txt_register)
        //Class initialization
        validations = ValidationUtils(this)
        UiUtils.checkKeyBoard(this, loginParent)
        database = MandiManagement(this)
        //Set Listeners
        btnLogin!!.setOnClickListener(this)
        txtForgetPass!!.setOnClickListener(this)
        txtRegister!!.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        val currentViewId = v.id
        when (currentViewId) {
            R.id.btn_login -> {
                //Todo apply validation on email and password
                emailValidations()
                if (isEmailValid && isPasswordValid) {
                    //Match Password
                    UiUtils.hideSoftKeyboard(v, this)
                    val userModel = database!!.getUserInfo(edtEmail!!.text.toString().trim { it <= ' ' })
                    if (userModel == null || userModel.userEmail == null) {
                        UiUtils.createSnackBar(this, loginParent, "User doesn't exist")
                    } else {
                        if (userModel.userPassword.equals(edtPass!!.text.toString().trim { it <= ' ' }, ignoreCase = true)) {
                            UiUtils.createSnackBar(this, loginParent, "Login Successfully")
                            val preferences = AppPreferences(this)
                            preferences.userId = userModel.userId
                            preferences.userName = userModel.userName
                            preferences.userEmail = userModel.userEmail
                            preferences.userContactNumber = userModel.contactNumber
                            val intent = Intent(this, UserListActivity::class.java)
                            startActivity(intent)
                            finish()
                        } else {
                            UiUtils.createSnackBar(this, loginParent, "Password/Email is wrong")
                        }
                    }
                }
            }
            R.id.txt_forget_password -> {
                //Navigate to Password Change screen
                val intentForgotPassword = Intent(this, ForgotPasswordActivity::class.java)
                startActivity(intentForgotPassword)
            }
            R.id.txt_register -> {
                //Register Screen
                val intent = Intent(this, RegisterScreenActivity::class.java)
                startActivity(intent)
            }
        }
    }

    private fun emailValidations() {
        isEmailValid = validations!!.validateEmail(loginParent, tilEmail, edtEmail)
        if (isEmailValid) {
            //Todo check password validations
            isPasswordValid = validations!!.validatePassword(loginParent, tilPassword, edtPass)
        }
    }
}