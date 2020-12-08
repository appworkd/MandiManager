package com.appwork.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.OrderAdapter.OrderVH
import com.appwork.mandisamiti.R
import com.appwork.model.EntryModel
import com.appwork.utils.DateUtils
import java.util.*

class OrderAdapter(private val context: Context, private val listener: OrderListener) : RecyclerView.Adapter<OrderVH>(), Filterable {
    private var entryList: List<EntryModel?>? = null
    private var filterList: List<EntryModel?>? = null
    fun setData(entryList: List<EntryModel?>?) {
        this.entryList = entryList
        filterList = entryList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_oder_view,
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
                entryList
            } else {
                val tempList: MutableList<EntryModel?> = ArrayList()
                for (model in entryList!!) {
                    if (model?.entryId.toString().contains(searchValue)) {
                        tempList.add(model)
                    }
                }
                tempList
            }
            val results = FilterResults()
            results.values = filterList
            return results
        }

        override fun publishResults(chars: CharSequence, results: FilterResults) {
            filterList = results.values as ArrayList<EntryModel?>
            notifyDataSetChanged()
        }
    }

    inner class OrderVH(itemView: View, listener: OrderListener) : RecyclerView.ViewHolder(itemView) {
        private val txtAmount: TextView
        private val txtDate: TextView
        private val txtOrderId: TextView
        private val txtName: TextView
        fun setData(adapterPosition: Int) {
            val model = filterList!![adapterPosition]
            txtAmount.text = String.format(Locale.getDefault(), "%s%s", model?.grandTotal, " " + context.resources.getString(R.string.txt_indian_rupee))
            txtDate.text = DateUtils.getStringCurrentDate(model!!.entryDate)
            txtName.text = model.entryName
            txtOrderId.text = String.format(Locale.getDefault(), "%s%s", "Order ID : ", model.entryId)
        }

        init {
            txtAmount = itemView.findViewById(R.id.tv_order_amount)
            txtDate = itemView.findViewById(R.id.tv_order_date)
            txtOrderId = itemView.findViewById(R.id.tv_order_id)
            txtName = itemView.findViewById(R.id.tv_order_name)
            itemView.setOnClickListener { v: View? -> listener.getOrder(entryList!![adapterPosition]) }
        }
    }

    interface OrderListener {
        fun getOrder(model: EntryModel?)
    }
}