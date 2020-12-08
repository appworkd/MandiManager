package com.appwork.mandisamiti

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.appcompat.widget.Toolbar
import androidx.drawerlayout.widget.DrawerLayout
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.UserListAdapter
import com.appwork.adapter.UserListAdapter.SelectUser
import com.appwork.database.MandiManagement
import com.appwork.mandisamiti.UserListActivity
import com.appwork.model.ClientModel
import com.appwork.utils.AppPreferences
import com.appwork.utils.CircleImageView
import com.appwork.utils.RecyclerViewUtils
import com.appwork.utils.StringValues
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.navigation.NavigationView
import java.util.*

class UserListActivity : AppCompatActivity(), SearchView.OnQueryTextListener, SelectUser, NavigationView.OnNavigationItemSelectedListener {
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
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setTheme(R.style.AppTheme_NoActionBar)
        setContentView(R.layout.activity_user_list)

        //Finding view
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
        fabAddUser!!.setOnClickListener(View.OnClickListener { view: View? -> startActivity(Intent(this, AddClientActivity::class.java)) })
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

    private fun addUsers() {
        if (adapter != null) {
            userList = database!!.getClients(preferences!!.userId)
            (userList as List<ClientModel>).sortedWith(compareBy { it.clientName })
            adapter!!.setUpList(userList)
        }
    }

    private fun init() {
        adapter = UserListAdapter(this, this)
        RecyclerViewUtils.setUpRecyclerview(this, rvAddUser, RecyclerView.VERTICAL)
        rvAddUser!!.adapter = adapter
        headerText!!.setText(preferences!!.userName)
    }

    override fun onResume() {
        super.onResume()
        addUsers()
        val currentUser = database!!.getUserInfo(preferences!!.userEmail)
        Glide.with(this)
                .load(currentUser.userImage)
                .placeholder(R.drawable.ic_user)
                .centerCrop()
                .into(imhHeader!!)
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
        val intent = Intent(this@UserListActivity, OrderListActivity::class.java)
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
                preferences!!.userId=(0L)
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
}