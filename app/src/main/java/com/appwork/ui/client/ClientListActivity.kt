package com.appwork.ui.client

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.*
import androidx.recyclerview.widget.LinearLayoutManager
import com.appwork.adapter.ClientListAdapter
import com.appwork.data.DataPref
import com.appwork.data.entities.ClientModel
import com.appwork.mandisamiti.LoginActivity
import com.appwork.mandisamiti.ProfileActivity
import com.appwork.mandisamiti.R
import com.appwork.mandisamiti.databinding.ActivityClientListBinding
import com.appwork.ui.orders.OrderListActivity
import com.appwork.utils.StringValues
import com.appwork.utils.StringValues.CLIENT_ID
import com.appwork.utils.StringValues.CLIENT_NUM
import com.appwork.utils.StringValues.COMING_FROM
import com.appwork.utils.StringValues.USER_ID
import com.appwork.utils.StringValues.USER_NUM
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.activity_client_list.*
import kotlinx.android.synthetic.main.container_user_list.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ClientListActivity : AppCompatActivity(),
        SearchView.OnQueryTextListener,
        ClientListAdapter.SelectUser,
        NavigationView.OnNavigationItemSelectedListener,
        IAddClientManager,
        KodeinAware {
    private var searchView: SearchView? = null
    override val kodein by kodein()
    private val factory: ClientVMFactory by instance()

    private val dataPref: DataPref by instance()
    private var adapter: ClientListAdapter? = null
    private var clientList: List<ClientModel>? = null
    private var clientListBinder: ActivityClientListBinding? = null
    private var clientVM: ClientListVM? = null
    private var parentId:Long?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        clientListBinder = DataBindingUtil.setContentView(this, R.layout.activity_client_list)
        clientVM = ViewModelProvider(this, factory).get(ClientListVM::class.java)
        clientListBinder?.userListVM = clientVM!!
        clientVM?.addClientListener = this
        dataPref.userId.asLiveData().observe(this, Observer {
            it?.let {
                parentId = it
                getAllClients()
            }
        })
        adapter = ClientListAdapter(this, this)

        setSupportActionBar(tl_users)
        //Menu

        val actionBarDrawerToggle = ActionBarDrawerToggle(
                this,
                drawer_main,
                tl_users,
                R.string.nav_open,
                R.string.nav_close
        )
        drawer_main!!.addDrawerListener(actionBarDrawerToggle)
        actionBarDrawerToggle.syncState()
        nav_main!!.setNavigationItemSelectedListener(this)


    }

    private fun getAllClients() {
        clientVM?.getAllClients(parentId!!)?.observe(owner = this) {
            clientList = it
            Toast.makeText(this, "ClientList Size : ${clientList?.size}", Toast.LENGTH_LONG).show()
            if (clientList?.isNotEmpty()!!) {
                rv_user.also { rv ->
                    rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    rv.adapter = adapter
                }
                adapter?.setUpList(clientList)
                adapter?.notifyDataSetChanged()
            }
        }
    }

    override fun onError(errorCode: Int) {
    }

    override fun onSuccess(client: ClientModel) {
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.menu_user_list, menu)
        val searchItem = menu.findItem(R.id.search)
        searchView = searchItem.actionView as SearchView
        searchView!!.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView!!.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return if (item.itemId == R.id.search) {
            true
        } else super.onOptionsItemSelected(item)
    }

    /*   private fun addUsers() {
           if (adapter != null) {
               userList = database!!.getClients(preferences!!.userId)
               (userList as List<ClientModel>).sortedWith(compareBy { it.clientName })
               adapter!!.setUpList(userList)
           }
       }*/


/* addUsers()
         val currentUser = database!!.getUserInfo(preferences!!.userEmail)
         Glide.with(this)
                 .load(currentUser.userImage)
                 .placeholder(R.drawable.ic_user)
                 .centerCrop()
                 .into(imhHeader!!)*//*

    }
*/

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = false
        when (item.itemId) {
            R.id.nav_mn_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                drawer_main.closeDrawers()
            }
            R.id.nav_mn_logout -> {
                lifecycleScope.launch(Dispatchers.IO) {
                    dataPref.saveUserContact("")
                }
                drawer_main.closeDrawers()
                val intentLog = Intent(this, LoginActivity::class.java)
                startActivity(intentLog)
                finish()
            }
            R.id.nav_mn_orders -> {
                drawer_main.closeDrawers()
                val intentOrder = Intent(this, OrderListActivity::class.java)
                intentOrder.putExtra(StringValues.COMING_FROM, "Main")
                startActivity(intentOrder)
            }

        }
        return false
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        // filter recycler view when query submitted
        adapter!!.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        // filter recycler view when query submitted
        adapter!!.filter.filter(query)
        return false
    }

    override fun onBackPressed() {
        if (!searchView!!.isIconified) {
            searchView!!.isIconified = true
            return
        }
        super.onBackPressed()
    }

    override fun moveToAddClient() {
        startActivity(Intent(this, AddClientActivity::class.java))
    }

    override fun capturePhoto() {
        TODO("Not yet implemented")
    }

    override fun selectUserListener(client: ClientModel?) {
        Intent(this@ClientListActivity, OrderListActivity::class.java).also {
            it.putExtra(COMING_FROM, "")
            it.putExtra(CLIENT_ID, client!!.clientId)
            it.putExtra(CLIENT_NUM, client.clientNumber!!)
            startActivity(it)
        }
    }
}