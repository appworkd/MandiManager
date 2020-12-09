package com.appwork.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appwork.data.entities.UserModel

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: UserModel): Long

    @Query("SELECT * FROM user_table ORDER BY id DESC")
    fun getUserData(): LiveData<List<UserModel>>
}