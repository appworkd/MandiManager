package com.appwork.application

import android.app.Application
import com.appwork.data.DataPref
import com.appwork.database.AppRoomDb
import com.appwork.ui.auth.UserRepository
import com.appwork.ui.auth.UserVMFactory
import com.appwork.ui.client.ClientRepo
import com.appwork.ui.client.ClientVMFactory
import com.appwork.utils.AppPreferences
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.x.androidXModule
import org.kodein.di.generic.bind
import org.kodein.di.generic.instance
import org.kodein.di.generic.provider
import org.kodein.di.generic.singleton

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
class MyApplication : Application(), KodeinAware {
    override val kodein = Kodein.lazy {
        import(androidXModule(this@MyApplication))
        bind() from singleton { AppRoomDb(instance()) }
        bind() from singleton { AppPreferences(instance()) }
        bind() from singleton { DataPref(instance()) }
        bind() from singleton { UserRepository(instance()) }
        bind() from singleton { ClientRepo(instance()) }
        bind() from provider { UserVMFactory(instance(), instance()) }
        bind() from provider { ClientVMFactory(instance(),instance()) }
    }
}