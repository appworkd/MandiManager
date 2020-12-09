package com.appwork.database

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper
import com.appwork.databaseUtils.ClientContract.ClientValues
import com.appwork.databaseUtils.EntryContract.EntryColumns
import com.appwork.databaseUtils.TransactionContract.TransactionColumns
import com.appwork.databaseUtils.UserContract.UserEntry
import com.appwork.data.entities.ClientModel
import com.appwork.data.entities.EntryModel
import com.appwork.model.TransactionModel
import com.appwork.model.UserModel
import java.util.*

class MandiManagement(context: Context?) : SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
    private var database: SQLiteDatabase? = null
    override fun onCreate(db: SQLiteDatabase) {
        //This will call once only when app get installed
        db.execSQL(SQL_CREATE_USER_ENTRIES)
        db.execSQL(CREATE_ENTRY_TABLE)
        db.execSQL(CREATE_TABLE_TRANSACTION)
        db.execSQL(CREATE_TABLE_CLIENT)
        if (database == null) {
            database = db
        }
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        //Any modification
        db.execSQL(SQL_DELETE_USER_ENTRIES)
        db.execSQL(DELETE_ENTRIES)
        db.execSQL(DELETE_TRANSACTION)
        db.execSQL(DELETE_CLIENT)
        onCreate(db)
    }

    fun insertUserData(values: ContentValues?): Long {
        database = writableDatabase
        return database!!.insert(UserEntry.TABLE_USER, null, values)
    }

    fun isExistUser(email: String): Boolean {
        database = readableDatabase
        val projection = arrayOf<String?>(UserEntry.USER_EMAIL)
        val selection = UserEntry.USER_EMAIL + " = ?"
        val selectArgs = arrayOf(email)
        val cursor = database!!.query(UserEntry.TABLE_USER,
                projection,
                selection,
                selectArgs,
                null,
                null,
                null)
        while (cursor.moveToNext()) {
            val savedEmail = cursor.getString(cursor.getColumnIndex(UserEntry.USER_EMAIL))
            if (savedEmail.equals(email, ignoreCase = true)) {
                cursor.close()
                return true
            }
        }
        cursor.close()
        return false
    }

    fun getUserInfo(email: String?): UserModel {
        database = readableDatabase
        val userModel = UserModel()
        val selection = UserEntry.USER_EMAIL + " = ?"
        val selectArgs = arrayOf(email)
        val cursor = database!!.query(UserEntry.TABLE_USER,
                null,
                selection,
                selectArgs,
                null,
                null,
                null)
        while (cursor.moveToNext()) {
            userModel.userEmail = cursor.getString(cursor.getColumnIndex(UserEntry.USER_EMAIL))
            userModel.userId=cursor.getLong(cursor.getColumnIndex(UserEntry.USER_ID))
            userModel.userImage = cursor.getString(cursor.getColumnIndex(UserEntry.USER_IMAGE))
            userModel.userName = cursor.getString(cursor.getColumnIndex(UserEntry.USER_NAME))
            userModel.userPassword = cursor.getString(cursor.getColumnIndex(UserEntry.USER_PASSWORD))
            userModel.contactNumber = cursor.getString(cursor.getColumnIndex(UserEntry.USER_PHONE))
            userModel.userDesc = cursor.getString(cursor.getColumnIndex(UserEntry.USER_DESC))
            userModel.userTinNumber = cursor.getString(cursor.getColumnIndex(UserEntry.USER_TIL_NUMBER))
            userModel.address = cursor.getString(cursor.getColumnIndex(UserEntry.USER_ADDRESS))
        }
        cursor.close()
        return userModel
    }

    fun updateProfilePic(userId: Long, contentValues: ContentValues?): Int {
        var updateId = -1
        database = writableDatabase
        if (userId != -1L) {
            updateId = database!!.update(UserEntry.TABLE_USER,
                    contentValues,
                    UserEntry.USER_ID + "=" + userId,
                    null)
        }
        return updateId
    }

    fun updateUser(userId: Long, contentValues: ContentValues?): Int {
        var updateId = -1
        database = writableDatabase
        if (userId != -1L) {
            updateId = database!!.update(UserEntry.TABLE_USER,
                    contentValues,
                    UserEntry.USER_ID + "=" + userId,
                    null)
        }
        return updateId
    }

    fun updateClientAmount(clientId: Long, contentValues: ContentValues?): Int {
        var updateId = -1
        database = writableDatabase
        if (clientId != -1L) {
            updateId = database!!.update(ClientValues.TABLE_CLIENT,
                    contentValues,
                    ClientValues.CLIENT_ID + "=" + clientId,
                    null)
        }
        return updateId
    }

    fun getClientAmount(clientId: Long): Cursor? {
        return try {
            database = readableDatabase
            database!!.rawQuery("SELECT client_is_gave, client_amount  FROM table_client WHERE client_id =$clientId",
                    null)
        } catch (e: Exception) {
            e.message
            null
        }
    }

    val allUsers: List<UserModel>
        get() {
            val userList: MutableList<UserModel> = ArrayList()
            database = readableDatabase
            val cursor = database!!.query(UserEntry.TABLE_USER,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)
            while (cursor.moveToNext()) {
                val userModel = UserModel()
                userModel.userEmail = cursor.getString(cursor.getColumnIndex(UserEntry.USER_EMAIL))
                userModel.userId=cursor.getLong(cursor.getColumnIndex(UserEntry.USER_ID))
                userModel.userName = cursor.getString(cursor.getColumnIndex(UserEntry.USER_NAME))
                userModel.userPassword = cursor.getString(cursor.getColumnIndex(UserEntry.USER_PASSWORD))
                userModel.contactNumber = cursor.getString(cursor.getColumnIndex(UserEntry.USER_PHONE))
                userList.add(userModel)
            }
            cursor.close()
            return userList
        }

    //Insert into Entry table
    fun insertEntry(values: ContentValues?): Long {
        database = writableDatabase
        return database!!.insert(EntryColumns.TABLE_ENTRY, null, values)
    }

    fun updateEntry(entryId: Long, contentValues: ContentValues?): Long {
        var updateId: Long = -1
        database = writableDatabase
        if (entryId != -1L) {
            updateId = database!!.update(EntryColumns.TABLE_ENTRY,
                    contentValues,
                    EntryColumns.ENTRY_ID + "=" + entryId,
                    null).toLong()
        }
        return updateId
    }

    fun getBillDetails(entryId: Long): EntryModel {
        database = readableDatabase
        val model = EntryModel()
        val cursor = database!!.rawQuery("SELECT * FROM table_entry WHERE entry_id =$entryId", null)
        while (cursor.moveToNext()) {
            model.entryName = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_NAME))
            model.entryId = entryId
            // model.setEntryName(cursor.getString(cursor.getColumnIndex(ORG_NAME)));
            model.numberOfPieces = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_NOP))
            model.wtPerPiece = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_PIECE_WT))
            model.remainingWt = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_REMAIN_WT))
            model.totalWt = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_WT))
            model.totalAmount = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_AMOUNT))
            model.lbRate = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_LB_RATE))
            model.lbCharge = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_LB_CHARGE))
            model.adatCharge = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_ADAT_CHARGE))
            model.commodityRate = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_COMMODITY_PRICE))
            model.fare = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_FARE))
            model.prevAmount = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_PREV_AMOUNT))
            model.grandTotal = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_GRANT_TOTAL))
            model.entryDate = cursor.getLong(cursor.getColumnIndex(EntryColumns.ENTRY_ENTRY_DATE))
            model.entryAttachment = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_ENTRY_ATTACHMENT))
            //            model.setGave((cursor.getString(cursor.getColumnIndex(ENTRY_ENTRY_IS_GIVEN))));
        }
        cursor.close()
        return model
    }

    fun getEntryList(userId: Long): List<EntryModel> {
        val entryList: MutableList<EntryModel> = ArrayList()
        database = readableDatabase
        val cursor = database!!.rawQuery("SELECT * FROM table_entry WHERE user_id =$userId", null)
        while (cursor.moveToNext()) {
            val model = EntryModel()
            model.entryName = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_NAME))
            model.entryId = cursor.getInt(cursor.getColumnIndex(EntryColumns.ENTRY_ID)).toLong()
            // model.setEntryName(cursor.getString(cursor.getColumnIndex(ORG_NAME)));
            model.numberOfPieces = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_NOP))
            model.wtPerPiece = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_PIECE_WT))
            model.remainingWt = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_REMAIN_WT))
            model.totalWt = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_WT))
            model.totalAmount = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_AMOUNT))
            model.lbRate = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_LB_RATE))
            model.lbCharge = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_LB_CHARGE))
            model.adatCharge = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_ADAT_CHARGE))
            model.commodityRate = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_COMMODITY_PRICE))
            model.fare = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_FARE))
            model.prevAmount = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_PREV_AMOUNT))
            model.grandTotal = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_GRANT_TOTAL))
            model.entryDate = cursor.getLong(cursor.getColumnIndex(EntryColumns.ENTRY_ENTRY_DATE))
            model.entryAttachment = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_ENTRY_ATTACHMENT))
            //            model.setGave((cursor.getString(cursor.getColumnIndex(ENTRY_ENTRY_IS_GIVEN))));
            entryList.add(model)
        }
        cursor.close()
        return entryList
    }

    // model.setEntryName(cursor.getString(cursor.getColumnIndex(ORG_NAME)));
    //            model.setGave((cursor.getString(cursor.getColumnIndex(ENTRY_ENTRY_IS_GIVEN))));
    val allEntries: List<EntryModel>
        get() {
            val entryList: MutableList<EntryModel> = ArrayList()
            database = readableDatabase
            val cursor = database!!.query(EntryColumns.TABLE_ENTRY,
                    null,
                    null,
                    null,
                    null,
                    null,
                    null)
            while (cursor.moveToNext()) {
                val model = EntryModel()
                model.entryName = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_NAME))
                model.entryId = cursor.getInt(cursor.getColumnIndex(EntryColumns.ENTRY_ID)).toLong()
                // model.setEntryName(cursor.getString(cursor.getColumnIndex(ORG_NAME)));
                model.numberOfPieces = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_NOP))
                model.wtPerPiece = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_PIECE_WT))
                model.remainingWt = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_REMAIN_WT))
                model.totalWt = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_WT))
                model.totalAmount = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_AMOUNT))
                model.lbRate = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_LB_RATE))
                model.lbCharge = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_TOTAL_LB_CHARGE))
                model.adatCharge = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_ADAT_CHARGE))
                model.commodityRate = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_COMMODITY_PRICE))
                model.fare = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_FARE))
                model.prevAmount = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_PREV_AMOUNT))
                model.grandTotal = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_GRANT_TOTAL))
                model.entryDate = cursor.getLong(cursor.getColumnIndex(EntryColumns.ENTRY_ENTRY_DATE))
                model.entryAttachment = cursor.getString(cursor.getColumnIndex(EntryColumns.ENTRY_ENTRY_ATTACHMENT))
                //            model.setGave((cursor.getString(cursor.getColumnIndex(ENTRY_ENTRY_IS_GIVEN))));
                entryList.add(model)
            }
            cursor.close()
            return entryList
        }

    fun insertTransaction(contentValues: ContentValues?): Long {
        database = writableDatabase
        return database!!.insert(TransactionColumns.TABLE_TRANSACTION, null, contentValues)
    }

    fun getTranList(userId: Long): List<TransactionModel> {
        val tranList: MutableList<TransactionModel> = ArrayList()
        database = readableDatabase
        val cursor = database!!.rawQuery("SELECT * FROM table_transaction WHERE trans_user_id =$userId", null)
        while (cursor.moveToNext()) {
            val model = TransactionModel()
            model.tranAttachment = cursor.getString(cursor.getColumnIndex(TransactionColumns.TRAN_ATTACHMENT))
            model.tranDate = cursor.getLong(cursor.getColumnIndex(TransactionColumns.TRAN_DATE))
            model.tranIsGiven = cursor.getString(cursor.getColumnIndex(TransactionColumns.TRAN_IS_GIVEN))
            model.tranId = cursor.getLong(cursor.getColumnIndex(TransactionColumns.TRAN_ID))
            model.tranReason = cursor.getString(cursor.getColumnIndex(TransactionColumns.TRAN_REASON))
            model.transAmount = cursor.getString(cursor.getColumnIndex(TransactionColumns.TRAN_AMOUNT))
            tranList.add(model)
        }
        cursor.close()
        return tranList
    }

    fun insertClientData(values: ContentValues?): Long {
        database = writableDatabase
        return database!!.insert(ClientValues.TABLE_CLIENT, null, values)
    }

  /*  fun getClients(parentId: Long): List<ClientModel> {
        database = readableDatabase
        val clientList: MutableList<ClientModel> = ArrayList()
        database = readableDatabase
        val cursor = database!!.rawQuery("SELECT * FROM table_client WHERE client_parent_id =$parentId", null)
        while (cursor.moveToNext()) {
            val model = ClientModel()
            model.clientId = cursor.getLong(cursor.getColumnIndex(ClientValues.CLIENT_ID))
            model.clientName = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_NAME))
            model.clientNumber = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_NUMBER))
            model.clientImg = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_IMAGE))
            model.clientAmount = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_AMOUNT))
            model.clientAddress = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_ADDRESS))
            model.isGave = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_IS_GAVE))
            model.parentId = parentId
            clientList.add(model)
        }
        cursor.close()
        return clientList
    }*/

    fun getClient(clientId: Long): ClientModel {
        val model = ClientModel()
        database = readableDatabase
        if (clientId != -1L) {
            val query = "SELECT * FROM " + ClientValues.TABLE_CLIENT + " WHERE " + ClientValues.CLIENT_ID + " =" + clientId
            val cursor = database!!.rawQuery(query, null)
            while (cursor.moveToNext()) {
                model.clientName = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_NAME))
                model.isGave = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_IS_GAVE))
                model.clientAddress = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_ADDRESS))
                model.clientAmount = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_AMOUNT))
                model.clientNumber = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_NUMBER))
                model.clientImg = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_IMAGE))
            }
            cursor.close()
        }
        return model
    }

    fun updateClientImage(clientId: Long, contentValues: ContentValues?): Long {
        var updateId: Long = -1
        database = writableDatabase
        if (clientId != -1L) {
            updateId = database!!.update(ClientValues.TABLE_CLIENT,
                    contentValues,
                    ClientValues.CLIENT_ID + "=" + clientId,
                    null).toLong()
        }
        return updateId
    }

    fun isClientExist(phoneNumber: String): Boolean {
        database = readableDatabase
        val projection = arrayOf<String?>(ClientValues.CLIENT_NUMBER)
        val selection = ClientValues.CLIENT_NUMBER + " = ?"
        val selectArgs = arrayOf(phoneNumber)
        val cursor = database!!.query(ClientValues.TABLE_CLIENT,
                projection,
                selection,
                selectArgs,
                null,
                null,
                null)
        while (cursor.moveToNext()) {
            val savedNumber = cursor.getString(cursor.getColumnIndex(ClientValues.CLIENT_NUMBER))
            if (savedNumber.equals(phoneNumber, ignoreCase = true)) {
                cursor.close()
                return true
            }
        }
        cursor.close()
        return false
    }

    companion object {
        private const val DATABASE_VERSION = 1
        private const val DATABASE_NAME = "mandi.db"
        private const val SQL_CREATE_USER_ENTRIES = "CREATE TABLE " + UserEntry.TABLE_USER + " (" +
                UserEntry.USER_ID + " INTEGER PRIMARY KEY," +
                UserEntry.USER_NAME + " TEXT," +
                UserEntry.USER_EMAIL + " TEXT," +
                UserEntry.USER_PHONE + " TEXT," +
                UserEntry.USER_DESC + " TEXT," +
                UserEntry.USER_TIL_NUMBER + " TEXT," +
                UserEntry.USER_ADDRESS + " TEXT," +
                UserEntry.USER_PASSWORD + " TEXT," +
                UserEntry.USER_IMAGE + " TEXT)"
        private const val CREATE_ENTRY_TABLE = "CREATE TABLE " + EntryColumns.TABLE_ENTRY + " (" +
                EntryColumns.ENTRY_ID + " INTEGER PRIMARY KEY," +
                EntryColumns.ENTRY_NAME + " TEXT," +
                EntryColumns.ENTRY_USER_ID + " LONG," +
                EntryColumns.ORG_NAME + " TEXT," +
                EntryColumns.ENTRY_NOP + " TEXT," +
                EntryColumns.ENTRY_REMAIN_WT + " TEXT," +
                EntryColumns.ENTRY_PIECE_WT + " TEXT," +
                EntryColumns.ENTRY_TOTAL_WT + " TEXT," +
                EntryColumns.ENTRY_TOTAL_AMOUNT + " TEXT," +
                EntryColumns.ENTRY_LB_RATE + " TEXT," +
                EntryColumns.ENTRY_TOTAL_LB_CHARGE + " TEXT," +
                EntryColumns.ENTRY_ADAT_CHARGE + " TEXT," +
                EntryColumns.ENTRY_COMMODITY_PRICE + " TEXT," +
                EntryColumns.ENTRY_FARE + " TEXT," +
                EntryColumns.ENTRY_PREV_AMOUNT + " TEXT," +
                EntryColumns.ENTRY_GRANT_TOTAL + " TEXT," +
                EntryColumns.ENTRY_ENTRY_DATE + " LONG," +
                EntryColumns.ENTRY_ENTRY_ATTACHMENT + " TEXT," +
                EntryColumns.ENTRY_ENTRY_IS_GIVEN + " boolean)"
        private const val CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TransactionColumns.TABLE_TRANSACTION + " (" +
                TransactionColumns.TRAN_ID + " INTEGER PRIMARY KEY," +
                TransactionColumns.TRAN_DATE + " LONG," +
                TransactionColumns.TRAN_USER_ID + " LONG," +
                TransactionColumns.TRAN_REASON + " TEXT," +
                TransactionColumns.TRAN_AMOUNT + " TEXT," +
                TransactionColumns.TRAN_ATTACHMENT + " TEXT," +
                TransactionColumns.TRAN_IS_GIVEN + " TEXT)"
        private const val CREATE_TABLE_CLIENT = "CREATE TABLE " + ClientValues.TABLE_CLIENT + " (" +
                ClientValues.CLIENT_ID + " INTEGER PRIMARY KEY," +
                ClientValues.CLIENT_PARENT_ID + " LONG," +
                ClientValues.CLIENT_NAME + " TEXT," +
                ClientValues.CLIENT_NUMBER + " TEXT," +
                ClientValues.CLIENT_ADDRESS + " TEXT," +
                ClientValues.CLIENT_IMAGE + " TEXT," +
                ClientValues.CLIENT_AMOUNT + " TEXT," +
                ClientValues.CLIENT_IS_GAVE + " TEXT)"
        private const val SQL_DELETE_USER_ENTRIES = "DROP TABLE IF EXISTS " + UserEntry.TABLE_USER
        private const val DELETE_ENTRIES = "DROP TABLE IF EXISTS " + EntryColumns.TABLE_ENTRY
        private const val DELETE_TRANSACTION = "DROP TABLE IF EXISTS " + TransactionColumns.TABLE_TRANSACTION
        private const val DELETE_CLIENT = "DROP TABLE IF EXISTS " + ClientValues.TABLE_CLIENT
    }
}