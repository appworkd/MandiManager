package com.appwork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.OrderAdapter.OrderVH
import com.appwork.mandisamiti.R
import com.appwork.data.entities.OrderModel
import com.appwork.mandisamiti.databinding.ItemViewOderViewBinding
import com.appwork.mandisamiti.databinding.ItemViewUserBinding
import com.appwork.utils.DateUtils
import java.util.*

class OrderAdapter(private val context: Context,
                   private val listener: OrderListener)
    : RecyclerView.Adapter<OrderVH>(), Filterable {
    private var orderList: List<OrderModel?>? = null
    private var filterList: List<OrderModel?>? = null
    fun setData(orderList: List<OrderModel?>?) {
        this.orderList = orderList
        filterList = orderList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderVH {
        val view = DataBindingUtil.inflate<ItemViewOderViewBinding>(LayoutInflater.from(parent.context),
                R.layout.item_view_oder_view,
                parent,
                false)
        return OrderVH(view, listener)
    }

    override fun onBindViewHolder(holder: OrderVH, position: Int) {
        holder.setData(holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return if (filterList == null) 0 else filterList!!.size
    }

    override fun getFilter(): Filter {
        return FilterOrder()
    }

    internal inner class FilterOrder : Filter() {
        override fun performFiltering(chars: CharSequence): FilterResults {
            val searchValue = chars.toString()
            filterList = if (searchValue.isEmpty()) {
                orderList
            } else {
                val tempList: MutableList<OrderModel?> = ArrayList()
                for (model in orderList!!) {
//                    if (model?.entryId.toString().contains(searchValue)) {
//                        tempList.add(model)
//                    }
                }
                tempList
            }
            val results = FilterResults()
            results.values = filterList
            return results
        }

        override fun publishResults(chars: CharSequence, results: FilterResults) {
            filterList = results.values as ArrayList<OrderModel?>
            notifyDataSetChanged()
        }
    }

    inner class OrderVH(private val itemViewUserBinding: ItemViewOderViewBinding, listener: OrderListener) : RecyclerView.ViewHolder(itemViewUserBinding.root) {
        fun setData(adapterPosition: Int) {

            itemViewUserBinding.itemOrderViewModel=orderList!![adapterPosition]
           /* val model = filterList!![adapterPosition]
            txtAmount.text = String.format(Locale.getDefault(), "%s%s", model?.grandTotal, " " + context.resources.getString(R.string.txt_indian_rupee))
            txtDate.text = DateUtils.getStringCurrentDate(model!!.entryDate)
            txtName.text = model.entryName*/
            //txtOrderId.text = String.format(Locale.getDefault(), "%s%s", "Order ID : ", model.entryId)
        }

        init {
            itemViewUserBinding.root.setOnClickListener { listener.getOrder(orderList!![adapterPosition]) }
        }
    }

    interface OrderListener {
        fun getOrder(model: OrderModel?)
    }
}