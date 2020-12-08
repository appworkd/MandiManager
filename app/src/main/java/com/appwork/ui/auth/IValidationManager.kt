package com.appwork.ui.auth

import com.appwork.data.entities.UserModel

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
interface IValidationManager {
    public fun onStartAuth()
    public fun onSuccess(user: UserModel)
    public fun onFinished()
    public fun onError(errorMessage: String)
}