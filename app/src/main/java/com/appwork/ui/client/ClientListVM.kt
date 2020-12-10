package com.appwork.ui.client

import android.app.Application
import android.view.View
import androidx.lifecycle.AndroidViewModel
import com.appwork.data.entities.ClientModel
import com.appwork.mandisamiti.R
import com.appwork.ui.addclient.IAddClientManager
import com.appwork.utils.Coroutines
import com.appwork.utils.hideKeyboard
import com.appwork.utils.showSnackBar

/**
 * Created by Vivek Kumar belongs to APP WORK  on 08-12-2020.
 */
class ClientListVM(private val repo: ClientRepo, application: Application) : AndroidViewModel(application) {
    var clientName: String? = null
    var clientNumber: String? = null
    var clientAddress: String? = null
    var clientImage: String? = null
    lateinit var addClientListener: IAddClientManager

  public  fun getAllClients() = repo.getAllClients()
    fun saveClient(v: View) {

        if (clientName.isNullOrEmpty()) {
            v.hideKeyboard()
            addClientListener.onError(1)
            return
        }
        if (clientNumber.isNullOrEmpty()) {
            v.hideKeyboard()
            addClientListener.onError(2)
            return
        }
        if (clientAddress.isNullOrEmpty()) {
            v.hideKeyboard()
            addClientListener.onError(3)
            return
        }
        Coroutines.main {
            val client = ClientModel(
                    1L,
                    clientName,
                    clientNumber,
                    clientAddress,
                    "",
                    "",
                    "")
            val id = repo.insertClient(client)
            if (id != 0L) {
                v.hideKeyboard()
                v.showSnackBar(getApplication<Application>().getString(R.string.add_msg))
                addClientListener.onSuccess(client)
            } else {
                v.hideKeyboard()
                v.showSnackBar(getApplication<Application>().getString(R.string.msg_something_went_wrong))
                addClientListener.onError(5)
            }
            return@main
        }
    }

    fun addUserList(v: View) {
        addClientListener.moveToAddClient()
    }

    fun performImageOperation(v:View){
        addClientListener.capturePhoto()
    }
}