package com.appwork.ui.orders

import com.appwork.data.entities.OrderModel
import com.appwork.database.AppRoomDb

/**
 * Created by Vivek Kumar belongs to APP WORK  on 10-12-2020.
 */
class OrderRepo(private val db: AppRoomDb) {
    suspend fun insertOrUpdateOrder(order: OrderModel) = db.getOrderDao().insertOrUpdate(order)

    fun getAllOrders() = db.getOrderDao().getAllOrders()
}