package com.appwork.ui.client

import android.content.Intent
import android.os.Bundle
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.databinding.DataBindingUtil
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.UserListAdapter
import com.appwork.database.MandiManagement
import com.appwork.data.entities.ClientModel
import com.appwork.ui.addclient.AddClientActivity
import com.appwork.mandisamiti.R
import com.appwork.mandisamiti.databinding.ActivityClientListBinding
import com.appwork.ui.addclient.IAddClientManager
import com.appwork.utils.AppPreferences
import com.appwork.utils.CircleImageView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import kotlinx.android.synthetic.main.container_user_list.*
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.instance

class ClientListActivity : AppCompatActivity(),
        //SearchView.OnQueryTextListener,
        UserListAdapter.SelectUser,
        //NavigationView.OnNavigationItemSelectedListener,
        IAddClientManager,
        KodeinAware {
    override val kodein by kodein()
    private val factory: ClientVMFactory by instance()
    private var rvAddUser: RecyclerView? = null
    private var fabAddUser: FloatingActionButton? = null
    private var adapter: UserListAdapter? = null
    private var userList: List<ClientModel?>? = null
    private var database: MandiManagement? = null
    private var preferences: AppPreferences? = null
    private var tlUsers: Toolbar? = null
    private var drawer: DrawerLayout? = null
    private var navigationView: NavigationView? = null
    private var searchView: SearchView? = null
    private var headerText: TextView? = null
    private var imhHeader: CircleImageView? = null
    private  var clientList: List<ClientModel>?=null
    private var  clientListBinder: ActivityClientListBinding?=null
    private var  clientVM: ClientListVM?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        clientListBinder = DataBindingUtil.setContentView(this, R.layout.activity_client_list)
        clientVM = ViewModelProvider(this, factory).get(ClientListVM::class.java)
        clientListBinder?.userListVM = clientVM!!
        clientVM?.addClientListener = this
        adapter= UserListAdapter(this, this)
        clientVM?.getAllClients()?.observe(this, Observer {
            clientList = it
            Toast.makeText(this, "ClientList Size : ${clientList?.size}", Toast.LENGTH_LONG).show()
            if (clientList?.isNotEmpty()!!) {
                rv_user.also { rv ->
                    rv.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                    rv.adapter =adapter
                }
                adapter?.setUpList(clientList)
                adapter?.notifyDataSetChanged()

            }
        })


//        setContentView(R.layout.activity_client_list)

        /*  //Finding view
          rvAddUser = findViewById(R.id.rv_user)
          fabAddUser = findViewById(R.id.fab_add_user)
          tlUsers = findViewById(R.id.tl_users)
          drawer = findViewById(R.id.drawer_main)
          navigationView = findViewById(R.id.nav_main)
          val headerView = navigationView!!.getHeaderView(0)
          headerText = headerView.findViewById(R.id.tv_nav_name)
          imhHeader = headerView.findViewById(R.id.img_nav)

          //Initialize
          userList = ArrayList()
          database = MandiManagement(this)
          preferences = AppPreferences(this)
          //Menu
          setSupportActionBar(tlUsers)
          val actionBarDrawerToggle = ActionBarDrawerToggle(
                  this,
                  drawer,
                  tlUsers,
                  R.string.nav_open,
                  R.string.nav_close
          )
          drawer!!.addDrawerListener(actionBarDrawerToggle)
          actionBarDrawerToggle.syncState()
          navigationView!!.setNavigationItemSelectedListener(this)
          init()
          fabAddUser!!.setOnClickListener(View.OnClickListener { view: View? -> startActivity(Intent(this, AddClientActivity::class.java)) })*/
    }

    override fun onError(errorCode: Int) {
        /*when(errorCode){
            1->
        }*/
    }

    override fun onSuccess(client: ClientModel) {
        TODO("Not yet implemented")
    }

    /* override fun onCreateOptionsMenu(menu: Menu): Boolean {
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
     }*/

    /*   private fun addUsers() {
           if (adapter != null) {
               userList = database!!.getClients(preferences!!.userId)
               (userList as List<ClientModel>).sortedWith(compareBy { it.clientName })
               adapter!!.setUpList(userList)
           }
       }*/

/*
    private fun init() {
        adapter = UserListAdapter(this, this)
        RecyclerViewUtils.setUpRecyclerview(this, rvAddUser, RecyclerView.VERTICAL)
        rvAddUser!!.adapter = adapter
        headerText!!.setText(preferences!!.userName)
    }

    override fun onResume() {
        super.onResume()
        */
/* addUsers()
         val currentUser = database!!.getUserInfo(preferences!!.userEmail)
         Glide.with(this)
                 .load(currentUser.userImage)
                 .placeholder(R.drawable.ic_user)
                 .centerCrop()
                 .into(imhHeader!!)*//*

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

    override fun selectUserListener(userModel: ClientModel?) {
        val intent = Intent(this@ClientListActivity, OrderListActivity::class.java)
        intent.putExtra(StringValues.COMING_FROM, "")
        intent.putExtra(StringValues.USER_ID, userModel!!.clientId)
        intent.putExtra(StringValues.USER_NUM, userModel.clientNumber!!)
        startActivity(intent)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        item.isChecked = false
        val id = item.itemId
        when (id) {
            R.id.nav_mn_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.nav_mn_logout -> {
                preferences!!.userId = (0L)
                val intentLog = Intent(this, LoginActivity::class.java)
                startActivity(intentLog)
                finish()
            }
            R.id.nav_mn_orders -> {
                val intentOrder = Intent(this, OrderListActivity::class.java)
                intentOrder.putExtra(StringValues.COMING_FROM, "Main")
                startActivity(intentOrder)
            }
        }
        return false
    }
*/


    override fun moveToAddClient() {
        startActivity(Intent(this, AddClientActivity::class.java))
    }

    override fun selectUserListener(userModel: ClientModel?) {
        TODO("Not yet implemented")
    }


}