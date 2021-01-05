package com.appwork.ui.client

import com.appwork.data.entities.ClientModel

/**
 * Created by Vivek Kumar belongs to APP WORK  on 08-12-2020.
 */
interface IAddClientManager {
    fun onError(errorCode: Int)
    fun onSuccess(client: ClientModel)

    fun moveToAddClient()

    fun capturePhoto()
}