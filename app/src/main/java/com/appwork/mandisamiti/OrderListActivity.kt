package com.appwork.mandisamiti

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.OrderAdapter
import com.appwork.adapter.OrderAdapter.OrderListener
import com.appwork.database.MandiManagement
import com.appwork.databaseUtils.EntryContract.EntryColumns
import com.appwork.model.EntryModel
import com.appwork.utils.AppPreferences
import com.appwork.utils.RecyclerViewUtils
import com.appwork.utils.StringValues
import com.appwork.utils.UiUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import java.util.*

class OrderListActivity : AppCompatActivity(), View.OnClickListener, OrderListener, SearchView.OnQueryTextListener {
    private var rvEntry: RecyclerView? = null
    private var searchView: SearchView? = null
    private var parentLayout: CoordinatorLayout? = null
    private var entryList: List<EntryModel?>? = null
    private var fabAddOrder: FloatingActionButton? = null
    private var userId: Long = 0
    private var comingFrom = ""
    private var clientNumber = ""
    private var adapter: OrderAdapter? = null
    private var database: MandiManagement? = null
    private var preferences: AppPreferences? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_list)
        //Mapping Views
        rvEntry = findViewById(R.id.rv_orders)
        fabAddOrder = findViewById(R.id.add_orders)
        parentLayout = findViewById(R.id.cont_order)
        //Initialize utils
        database = MandiManagement(this)
        preferences = AppPreferences(this)
        //Intent
        if (intent.hasExtra(StringValues.USER_ID)) {
            userId = intent.getLongExtra(StringValues.USER_ID, -1)
        }
        if (intent.hasExtra(StringValues.COMING_FROM)) {
            comingFrom = intent.getStringExtra(StringValues.COMING_FROM)
        }
        if (intent.hasExtra(StringValues.USER_NUM)) {
            clientNumber = intent.getStringExtra(StringValues.USER_NUM)
        }
        supportActionBar
        init()
        fabAddOrder!!.setOnClickListener(this)
    }

    private fun init() {
        adapter = OrderAdapter(this, this)
        RecyclerViewUtils.setUpRecyclerview(this, rvEntry, RecyclerView.VERTICAL)
        rvEntry!!.adapter = adapter
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_entry, menu)
        val searchItem = menu.findItem(R.id.mn_search_order)
        searchView = searchItem.actionView as SearchView
        searchView!!.imeOptions = EditorInfo.IME_ACTION_DONE
        searchView!!.queryHint = "Order Id"
        searchView!!.setOnQueryTextListener(this)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        when (id) {
            R.id.mn_order -> {
                val intent = Intent(this, EntriesActivity::class.java)
                intent.putExtra(StringValues.USER_ID, userId)
                startActivity(intent)
            }
            R.id.mn_call -> if (clientNumber.isEmpty()) {
                UiUtils.createSnackBar(this, parentLayout, "Number is not found in profile")
            } else {
                val callIntent = Intent(Intent.ACTION_DIAL)
                callIntent.data = Uri.parse("tel:$clientNumber")
                startActivity(callIntent)
            }
            R.id.mn_search_order -> {
            }
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        entryList = if (comingFrom.equals("Main", ignoreCase = true)) {
            fabAddOrder!!.hide()
            database!!.allEntries
        } else {
            fabAddOrder!!.show()
            database!!.getEntryList(userId)
        }
        if (entryList!!.size > 0) {
            Collections.reverse(entryList)
            adapter!!.setData(entryList)
        }
    }

    override fun onClick(v: View) {
        if (v === fabAddOrder) {
            val intent = Intent(this, CalculateActivity::class.java)
            intent.putExtra(StringValues.GAVE, true)
            intent.putExtra(StringValues.USER_ID, userId)
            startActivity(intent)
        }
    }

    override fun getOrder(model: EntryModel?) {
        startActivity(Intent(this, BillDetails::class.java)
                .putExtra(EntryColumns.ENTRY_ID, model!!.entryId)
                .putExtra(StringValues.USER_ID, userId)
                .putExtra(StringValues.USER_NUM, clientNumber))
    }

    override fun onQueryTextSubmit(query: String): Boolean {
        adapter!!.filter.filter(query)
        return false
    }

    override fun onQueryTextChange(query: String): Boolean {
        adapter!!.filter.filter(query)
        return false
    }
}