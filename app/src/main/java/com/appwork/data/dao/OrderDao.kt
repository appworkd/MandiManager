package com.appwork.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.appwork.data.entities.OrderModel

/**
 * Created by Vivek Kumar belongs to APP WORK  on 10-12-2020.
 */
@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(order: OrderModel): Long

    @Query("SELECT * FROM TABLE_ORDERS")
    fun getAllOrders(): LiveData<List<OrderModel>>
}