package com.appwork.model

data class UserModel(var userId: Long = 0,
                     var userImage: String? = null,
                     var userName: String? = null,
                     var userPassword: String? = null,
                     var userDesc: String? = null,
                     var contactNumber: String? = null,
                     var address: String? = null,
                     var amount: Double = 0.0,
                     var userEmail: String? = null,
                     var userTinNumber: String? = null,
                     var isStatus: Boolean = false)