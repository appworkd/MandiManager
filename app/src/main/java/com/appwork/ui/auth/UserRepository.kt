package com.appwork.ui.auth

import com.appwork.data.entities.UserModel
import com.appwork.database.AppRoomDb

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
class UserRepository(
        private val db: AppRoomDb
) {
    suspend fun insertUser(user: UserModel) = db.getUserDao().insertUser(user)

    fun getUsers() = db.getUserDao().getUserData()

    suspend fun getUserData(number: String) = db.getUserDao().isUserExist(number)
}