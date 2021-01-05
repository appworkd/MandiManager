package com.appwork.mandisamiti

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.observe
import com.appwork.data.entities.UserModel
import com.appwork.mandisamiti.databinding.ActivityLoginBinding
import com.appwork.ui.auth.IValidationManager
import com.appwork.ui.auth.RegisterScreenActivity
import com.appwork.ui.auth.UserVM
import com.appwork.ui.auth.UserVMFactory
import com.appwork.ui.client.ClientListActivity
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class LoginActivity : AppCompatActivity(), KodeinAware, IValidationManager {
    override val kodein: Kodein by kodein()
    private var loginBinding: ActivityLoginBinding? = null
    private val loginFactory: UserVMFactory by instance()
    private var loginVM: UserVM? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        loginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)
        loginBinding!!.lifecycleOwner = this
        loginVM = ViewModelProvider(this, loginFactory).get(UserVM::class.java)
        loginBinding!!.loginVM = loginVM
        loginVM!!.authManager = this

        loginVM!!.moveToMainScreen.observe(owner = this) {
            if (it) {
                Intent(this, ClientListActivity::class.java).also { intent ->
                    startActivity(intent)
                    finish()
                }
            }
        }
        loginVM!!.moveToSignUp.observe(owner = this) {
            if (it) {
                Intent(this, RegisterScreenActivity::class.java).also { intent ->
                    startActivity(intent)
                }
            }
        }
    }

    override fun onStartAuth() {
    }

    override fun onSuccess(user: UserModel) {
    }

    override fun onFinished() {
    }

    override fun onError(errorMessage: String) {

    }


}