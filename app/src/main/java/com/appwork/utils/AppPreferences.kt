package com.appwork.utils

import android.content.Context
import android.content.SharedPreferences
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.*

class AppPreferences(  // Context
        private val context: Context) {
    // Shared Preferences
    private val pref: SharedPreferences

    // Editor for Shared preferences
    private val editor: SharedPreferences.Editor

    // Shared pref mode
    var PRIVATE_MODE = 0
    var userName: String?
        get() = pref.getString(StringValues.LOG_IN_USER_NAME, "")
        set(name) {
            editor.putString(StringValues.LOG_IN_USER_NAME, name)
            editor.commit()
        }
    var userEmail: String?
        get() = pref.getString(StringValues.USER_EMAIL, "")
        set(email) {
            editor.putString(StringValues.USER_EMAIL, email)
            editor.commit()
        }
    var userAddress: String?
        get() = pref.getString(StringValues.USER_CONTACT_NUMBER, "")
        set(address) {
            editor.putString(StringValues.LOG_IN_USER_ADDRESS, address)
            editor.commit()
        }
    var userContactNumber: String?
        get() = pref.getString(StringValues.LOG_IN_USER_ADDRESS, "")
        set(number) {
            editor.putString(StringValues.USER_CONTACT_NUMBER, number)
            editor.commit()
        }
    var userId: Long
        get() = pref.getLong(StringValues.LOG_IN_USER_ID, 0L)
        set(userId) {
            editor.putLong(StringValues.LOG_IN_USER_ID, userId)
            editor.commit()
        }//EDIT: gso to gson

    //Retrieve the values
    val officeAddress: ArrayList<String>
        get() {
            //Retrieve the values
            val gson = Gson()
            val jsonText = pref.getString("officeAddress", "")
            return gson.fromJson(jsonText, object : TypeToken<ArrayList<String?>?>() {}.type)
        }

    fun setIsDefaultOfficeAddress(officeAddress: List<String?>?) {
        val officeAddresses = Gson().toJson(officeAddress)
        editor.putString("isDefaultOfficeAddress", officeAddresses)
        editor.commit()
    }

    fun clearAllPreference() {
        editor.clear().apply()
    }

    companion object {
        // SharedPref file name
        private const val PREF_NAME = "Mandi_Prefs"
    }

    init {
        pref = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE)
        editor = pref.edit()
        editor.apply()
    }
}