package com.appwork.ui.auth

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.appwork.data.entities.UserModel
import com.appwork.mandisamiti.R
import com.appwork.ui.client.ClientListActivity
import com.appwork.mandisamiti.databinding.ActivityRegisterScreenBinding
import org.kodein.di.KodeinAware
import org.kodein.di.generic.instance
import org.kodein.di.android.kodein

class RegisterScreenActivity : AppCompatActivity(), IValidationManager, KodeinAware {
    override val kodein by kodein()
    private val factory: UserVMFactory by instance()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val registerBind: ActivityRegisterScreenBinding =
                DataBindingUtil.setContentView(this, R.layout.activity_register_screen)
        val registerVM = ViewModelProvider(this, factory).get(UserVM::class.java)
        registerBind.registerVM = registerVM
        registerVM.authManager = this
    }

    override fun onStartAuth() {
        Log.d("RegisterScreen", "onStartAuth")
    }

    override fun onSuccess(user: UserModel) {

        val intent = Intent(this, ClientListActivity::class.java)
        startActivity(intent)
        finish()
    }

    override fun onFinished() {
        Log.d("RegisterScreen", "onFinished")
    }

    override fun onError(errorMessage: String) {
        Log.d("RegisterScreen", "errorMessage $errorMessage")
    }


}