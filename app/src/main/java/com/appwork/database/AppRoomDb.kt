package com.appwork.database

import android.content.Context
import androidx.room.*
import com.appwork.data.dao.UserDao
import com.appwork.data.entities.UserModel
import com.appwork.utils.DateConverters

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
@Database(entities = [UserModel::class], version = 1)
@TypeConverters(DateConverters::class)
abstract class AppRoomDb : RoomDatabase() {

    abstract fun getUserDao(): UserDao

    companion object {
        private const val DB_NAME = "MyAppDb"

        @Volatile
        private var instance: AppRoomDb? = null
        private val LOCK = Any()

        //Lock is used so only single instance will be created
        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: createDb(context).also {
                instance = it
            }
        }

        private fun createDb(context: Context) =
                Room.databaseBuilder(
                        context.applicationContext,
                        AppRoomDb::class.java,
                        DB_NAME
                ).build()
    }
}