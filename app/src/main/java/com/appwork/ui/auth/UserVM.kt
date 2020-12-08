package com.appwork.ui.auth

import android.util.Log
import android.view.View
import androidx.lifecycle.ViewModel
import com.appwork.data.DataPref
import com.appwork.data.entities.UserModel
import com.appwork.utils.Coroutines
import com.appwork.utils.hideKeyboard
import com.appwork.utils.showSnackBar
import org.joda.time.LocalDate

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
class UserVM(private val userRepository: UserRepository, private val prefs: DataPref) : ViewModel() {
    var email: String? = null
    var name: String? = null
    var password: String? = null
    var phoneNumber: String? = null
    var confirmPassword: String? = null
    var imageUri: String? = null
    lateinit var authManager: IValidationManager

    fun loginUser(v: View) {

    }

    fun onRegisterClick(v: View) {
        authManager.onStartAuth()
        if (password.isNullOrEmpty()) {
            authManager.onError("Password can\'t be empty")
            return
        }
        if (name.isNullOrEmpty()) {
            authManager.onError("Name can\'t be empty")
            return
        }
        if (!isPasswordMatched(password!!, confirmPassword!!)) {
            authManager.onError("Password should match")
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            authManager.onError("Phone Number can\'t be empty")
            return
        }

        //Coroutines
        Coroutines.main {
            v.hideKeyboard()
            val date = LocalDate().toDate()
            val userModel = UserModel(
                    name.toString(),
                    phoneNumber.toString(),
                    "",
                    "",
                    date,
                    date,
                    imageUri,
                    ""
            )
            val id = userRepository.insertUser(userModel)
            Log.d("UserVM", "Id: $id")
            if (id != 0L) {
                v.showSnackBar("Registered Successfully!!")
                authManager.onSuccess(userModel)
                prefs.saveUserContact(phoneNumber.toString().trim())
            } else {
                authManager.onError("Something went wrong")
            }
            return@main
        }
    }

    private fun isPasswordMatched(password: String, confirmPassword: String): Boolean {
        return (password.trim() == confirmPassword.trim())
    }
}