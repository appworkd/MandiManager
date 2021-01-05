package com.appwork.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesKey
import androidx.datastore.preferences.createDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Created by Vivek Kumar belongs to APP WORK  on 06-12-2020.
 */
class DataPref(context: Context) {
    private val appContext = context.applicationContext
    private val dataStore: DataStore<Preferences>

    companion object {
        const val PREFS = "App_Preferences"
        val KEY_USER_NUMBER = preferencesKey<String>("user_number")
        val KEY_USER_ID = preferencesKey<Long>("user_id")
    }

    init {
        dataStore = context.createDataStore(
                name = PREFS)
    }

    val userContact: Flow<String?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_NUMBER]
        }

    suspend fun saveUserContact(userContact: String) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_NUMBER] = userContact
        }
    }

    val userId: Flow<Long?>
        get() = dataStore.data.map { preferences ->
            preferences[KEY_USER_ID]
        }

    suspend fun saveUserInt(userId: Long) {
        dataStore.edit { preferences ->
            preferences[KEY_USER_ID] = userId
        }
    }

}