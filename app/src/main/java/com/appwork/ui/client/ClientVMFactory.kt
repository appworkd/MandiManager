package com.appwork.ui.client

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.appwork.application.MyApplication
import com.appwork.database.AppRoomDb

/**
 * Created by Vivek Kumar belongs to APP WORK  on 08-12-2020.
 */
class ClientVMFactory(private val repo: ClientRepo,private val application: Application):ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return ClientListVM(repo,application)  as T
    }
}