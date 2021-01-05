package com.appwork.ui.orders

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.OrderAdapter
import com.appwork.adapter.OrderAdapter.OrderListener
import com.appwork.database.MandiManagement
import com.appwork.data.entities.OrderModel
import com.appwork.mandisamiti.BillDetails
import com.appwork.mandisamiti.EntriesActivity
import com.appwork.mandisamiti.R
import com.appwork.mandisamiti.databinding.ActivityOrderListBinding
import com.appwork.utils.AppPreferences
import com.appwork.utils.RecyclerViewUtils
import com.appwork.utils.StringValues
import com.appwork.utils.StringValues.CLIENT_ID
import com.appwork.utils.StringValues.CLIENT_NAME
import com.appwork.utils.StringValues.CLIENT_NUM
import com.appwork.utils.StringValues.COMING_FROM
import com.appwork.utils.UiUtils
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.android.synthetic.main.activity_order_list.*
import org.kodein.di.Kodein
import org.kodein.di.KodeinAware
import org.kodein.di.android.kodein
import org.kodein.di.generic.factory
import org.kodein.di.generic.instance
import java.util.*

class OrderListActivity : AppCompatActivity(),
        View.OnClickListener,
        OrderListener,
        SearchView.OnQueryTextListener,
        KodeinAware {
    private var rvEntry: RecyclerView? = null
    private var searchView: SearchView? = null
    private var parentLayout: CoordinatorLayout? = null
    private var orderList: List<OrderModel?>? = null
    private var fabAddOrder: FloatingActionButton? = null
    private var userId: Long = 0
    private var comingFrom = ""
    private var clientNumber = ""
    private var adapter: OrderAdapter? = null
    private var database: MandiManagement? = null
    private var preferences: AppPreferences? = null
    private var orderListBinding: ActivityOrderListBinding? = null
    private var orderListVM: OrderVM? = null
    private val orderFactory: OrderVMFactory by instance()
    override val kodein by kodein()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        orderListBinding = DataBindingUtil.setContentView(this,
                R.layout.activity_order_list)
        orderListVM = ViewModelProvider(this, orderFactory).get(OrderVM::class.java)
        orderListBinding!!.lifecycleOwner = this
        orderListBinding!!.ordersVM = orderListVM

        //Intent
        if (intent.hasExtra(CLIENT_ID)) {
            userId = intent.getLongExtra(CLIENT_ID, -1)
        }
        if (intent.hasExtra(COMING_FROM)) {
            comingFrom = intent.getStringExtra(COMING_FROM)
        }
        if (intent.hasExtra(CLIENT_NUM)) {
            clientNumber = intent.getStringExtra(CLIENT_NUM)
        }
        supportActionBar
        adapter = OrderAdapter(this, this)
        orderListVM!!.getAllOrders().observe(this, Observer {
            if (it.isNotEmpty()) {
                rv_orders.also {rv->
                    rv.layoutManager = LinearLayoutManager(this@OrderListActivity,LinearLayoutManager.VERTICAL,false)
                    rv.adapter=adapter
                }
                adapter!!.setData(it)
                adapter!!.notifyDataSetChanged()
            }
        })
        orderListVM!!.isNavigateToBill.observe(this, Observer {
            if (it){
                val intent = Intent(this, OrderActivity::class.java)
                intent.putExtra(StringValues.GAVE, true)
                intent.putExtra(StringValues.USER_ID, userId)
                startActivity(intent)
                        //.putExtra(EntryColumns.ENTRY_ID, model!!.entryId)
                       /* .putExtra(StringValues.USER_ID, userId)
                        .putExtra(StringValues.USER_NUM, clientNumber))*/
            }
        })

        Log.e("Orders","Time zone ids : "+ TimeZone.getAvailableIDs()[282])
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
        when (item.itemId) {
            R.id.mn_order -> {
                val intent = Intent(this, EntriesActivity::class.java)
                intent.putExtra(StringValues.USER_ID, userId)
                startActivity(intent)
                return true
            }
            R.id.mn_call -> {
                if (clientNumber.isEmpty()) {
                    UiUtils.createSnackBar(this, parentLayout, "Number is not found in profile")
                } else {
                    val callIntent = Intent(Intent.ACTION_DIAL)
                    callIntent.data = Uri.parse("tel:$clientNumber")
                    startActivity(callIntent)
                }
                return true
            }
            R.id.mn_search_order -> {
                return true
            }
            else -> {
                return super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        /*orderList = if (comingFrom.equals("Main", ignoreCase = true)) {
            fabAddOrder!!.hide()
            database!!.allOrders
        } else {
            fabAddOrder!!.show()
            database!!.getEntryList(userId)
        }
        if (orderList!!.size > 0) {
            Collections.reverse(orderList)
            adapter!!.setData(orderList)
        }*/
    }

    override fun onClick(v: View) {
        if (v === fabAddOrder) {
            val intent = Intent(this, OrderActivity::class.java)
            intent.putExtra(StringValues.GAVE, true)
            intent.putExtra(StringValues.USER_ID, userId)
            startActivity(intent)
        }
    }

    override fun getOrder(model: OrderModel?) {
        startActivity(Intent(this, BillDetails::class.java)
                //.putExtra(EntryColumns.ENTRY_ID, model!!.entryId)
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