package com.appwork.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appwork.data.DataPref

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
class UserVMFactory(private val repository: UserRepository, private val prefs: DataPref) :
        ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return UserVM(repository,prefs) as T
    }
}