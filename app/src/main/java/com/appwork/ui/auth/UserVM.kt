package com.appwork.ui.auth

import android.view.View
import androidx.databinding.Observable
import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.appwork.data.DataPref
import com.appwork.data.entities.UserModel
import com.appwork.utils.hideKeyboard
import com.appwork.utils.showSnackBar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
class UserVM(private val userRepository: UserRepository, private val prefs: DataPref) : ViewModel() {
    var name: String? = null
    var password: String? = null
    var phoneNumber: String? = null
    var confirmPassword: String? = null
    private var imageUri: String? = null
    lateinit var authManager: IValidationManager
    private var existingUser: UserModel? = null
    private var insertId: Long = 0L
    var moveToSignUp = MutableLiveData(false)
    var moveToMainScreen = MutableLiveData(false)

    fun loginUser(v: View) {
        v.hideKeyboard()
        if (phoneNumber.isNullOrEmpty()) {
            authManager.onError("Number can not be empty")
            v.showSnackBar("Number can not be empty")
            return
        }
        if (password.isNullOrEmpty()) {
            authManager.onError("Password can not be empty")
            v.showSnackBar("Password can not be empty")
            return
        }
        viewModelScope.launch(Dispatchers.Main) {
            existingUser = userRepository.getUserData(phoneNumber.toString())
            if (existingUser != null) {
                val savedPass = existingUser!!.password
                if (!savedPass.equals(password, ignoreCase = true)) {
                        authManager.onError("Password did not match")
                        v.showSnackBar("Password did not match")
                    return@launch
                    } else {
                        moveToMainScreen.value=true
                        prefs.saveUserContact(phoneNumber.toString().trim())
                        prefs.saveUserInt(userId = existingUser!!.id)
                    }
            } else {
                authManager.onError("User not exist.")
                v.showSnackBar("User not exist.")
                return@launch
            }
        }
        return
    }

    fun onRegisterClick(v: View) {
        authManager.onStartAuth()
        if (password.isNullOrEmpty()) {
            authManager.onError("Password can not be empty")
            return
        }
        if (name.isNullOrEmpty()) {
            authManager.onError("Name can not be empty")
            return
        }
        if (!isPasswordMatched(password!!, confirmPassword!!)) {
            authManager.onError("Password should match")
            return
        }
        if (phoneNumber.isNullOrEmpty()) {
            authManager.onError("Phone Number can not be empty")
            return
        }

        //Coroutines
        viewModelScope.launch(Dispatchers.IO) {
            v.hideKeyboard()
            val startDate = LocalDate.now().toEpochDay()
            val userModel = UserModel(
                    name.toString(),
                    password.toString(),
                    phoneNumber.toString(),
                    "",
                    "",
                    startDate,
                    1L,
                    imageUri,
                    ""
            )
            val userCheckJob = async {
                existingUser = userRepository.getUserData(phoneNumber.toString())
            }
            userCheckJob.await()
            if (existingUser != null) {
                    v.showSnackBar("User already exist")
                    authManager.onError("User already exist")
            } else {
                    insertId = userRepository.insertUser(userModel)
                    if (insertId != 0L) {
                        prefs.saveUserContact(phoneNumber.toString().trim())
                        prefs.saveUserInt(insertId)
                    }
                    if (insertId != 0L) {
                        authManager.onSuccess(userModel)
                    } else {
                        authManager.onError("Something went wrong")
                    }
            }
        }
    }

    fun moveToSignUp(v: View) {
        moveToSignUp.value =true
    }

    private fun isPasswordMatched(password: String, confirmPassword: String): Boolean {
        return (password.trim() == confirmPassword.trim())
    }
}