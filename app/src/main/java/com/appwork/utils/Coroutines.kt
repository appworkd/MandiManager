package com.appwork.utils

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */
object Coroutines {
    fun main(work: suspend (() -> Unit)) =
            CoroutineScope(Dispatchers.Main).launch {
                work()
            }

    fun io(work: suspend (() -> Unit)) =
            CoroutineScope(Dispatchers.IO)
                    .launch {
                        work()
                    }
}