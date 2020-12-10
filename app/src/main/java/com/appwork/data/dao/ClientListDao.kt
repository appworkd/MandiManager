package com.appwork.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
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

    @Query("UPDATE client_table SET clientImg = :clientImage WHERE clientId = :clientId")
    suspend fun updateClientImage(clientId: Int, clientImage: String): Long
}