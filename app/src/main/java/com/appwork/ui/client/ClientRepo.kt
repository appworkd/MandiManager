package com.appwork.ui.client

import com.appwork.data.entities.ClientModel
import com.appwork.database.AppRoomDb

/**
 * Created by Vivek Kumar belongs to APP WORK  on 08-12-2020.
 */
class ClientRepo(private val db: AppRoomDb) {

    suspend fun insertClient(client: ClientModel) = db.getClientListDao().insertUpdateClient(client)

     fun getAllClients() = db.getClientListDao().getAllClients()
}