package com.appwork.data.entities

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.io.Serializable
import java.util.*

/**
 * Created by Vivek Kumar belongs to APP WORK  on 04-12-2020.
 */

@Entity(tableName = "user_table",indices = [Index(value = ["userNumber"],unique = true)])
data class UserModel(
        val userName: String? = null,
        val password:String?=null,
        val userNumber: String? = null,
        val userAddress: String? = null,
        val userTinNumber: String? = null,
        val userCreatedOn: Long? = null,
        val userUpdatedOn: Long? = null,
        val userProfileImage: String? = null,
        val userAdhar: String? = null
) : Serializable {
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0
}
