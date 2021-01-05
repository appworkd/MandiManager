package com.appwork.ui.orders

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider

/**
 * Created by Vivek Kumar belongs to APP WORK  on 12-12-2020.
 */
class OrderVMFactory(private val repo:OrderRepo):ViewModelProvider.NewInstanceFactory() {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return OrderVM(repo) as T
    }
}