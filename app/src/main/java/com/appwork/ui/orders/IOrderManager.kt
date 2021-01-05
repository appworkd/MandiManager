package com.appwork.ui.orders

import com.appwork.data.entities.OrderModel

/**
 * Created by Vivek Kumar belongs to APP WORK  on 12-12-2020.
 */
interface IOrderManager {
    fun onError(error: String)
    fun onSuccess(order:OrderModel)
}