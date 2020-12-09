package com.appwork.data.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.appwork.data.entities.ClientModel

/**
 * Created by Vivek Kumar belongs to APP WORK  on 08-12-2020.
 */
@Dao
interface ClientListDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUpdateClient(clientModel: ClientModel): Long

    @Query("Select * from client_table")
    fun getAllClients(): LiveData<List<ClientModel>>
}