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

    @Query("SELECT * FROM CLIENT_TABLE WHERE parentId = :parentId")
    fun getParentClintList(parentId: Long): LiveData<List<ClientModel>>

    @Query("SELECT * FROM CLIENT_TABLE WHERE clientNumber LIKE :phoneNumber AND parentId LIKE :parentId")
  suspend  fun getClient(phoneNumber: String, parentId: Long): ClientModel
}