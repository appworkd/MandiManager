package com.appwork.adapter

import android.content.Context
import android.content.res.Resources
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.appwork.adapter.UserListAdapter.UserVH
import com.appwork.mandisamiti.R
import com.appwork.data.entities.ClientModel
import com.appwork.utils.CircleImageView
import com.bumptech.glide.Glide
import java.util.*

class UserListAdapter(private val context: Context, private val selectUserListener: SelectUser) : RecyclerView.Adapter<UserVH>(), Filterable {
    private var userList: List<ClientModel?>? = null
    private var filterUserList: List<ClientModel?>? = null
    private val resources: Resources
    fun setUpList(userList: List<ClientModel?>?) {
        this.userList = userList
        filterUserList = userList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserVH {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view_user, parent, false)
        return UserVH(view, selectUserListener)
    }

    override fun onBindViewHolder(holder: UserVH, position: Int) {
        holder.setUpData(holder.adapterPosition)
    }

    override fun getItemCount(): Int {
        return if (filterUserList == null) 0 else filterUserList!!.size
    }

    override fun getFilter(): Filter {
        return UserFilter()
    }

    internal inner class UserFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {
            val searchValue = charSequence.toString()
            filterUserList = if (searchValue.isEmpty()) {
                userList
            } else {
                val tempFilterList: MutableList<ClientModel?> = ArrayList()
                for (currentUser in userList!!) {
                    if (currentUser!!.clientName!!.toLowerCase().contains(searchValue)
                            || currentUser.clientNumber!!.contains(searchValue)) {
                        tempFilterList.add(currentUser)
                    }
                }
                tempFilterList
            }
            val filterResults = FilterResults()
            filterResults.values = filterUserList
            return filterResults
        }

        override fun publishResults(charSequence: CharSequence, filterResults: FilterResults) {
            filterUserList = filterResults.values as ArrayList<ClientModel?>
            notifyDataSetChanged()
        }
    }

    inner class UserVH(itemView: View, selectUserListener: SelectUser) : RecyclerView.ViewHolder(itemView) {
        var txtName: TextView
        var txtDesc: TextView
        var txtStatus: TextView
        var txtAmount: TextView
        var imgUser: CircleImageView
        fun setUpData(adapterPosition: Int) {
            val currentUser = filterUserList!![adapterPosition]
            val userName = currentUser?.clientName
            val userDesc = currentUser?.clientAddress
            val uri = currentUser?.clientImg
           /* val amount = currentUser?.clientAmount?.toInt()
            if (!userName!!.isEmpty()) {
                txtName.text = userName
            }
            if (userDesc != null && !userDesc.isEmpty()) {
                txtDesc.text = userDesc
            }
            Glide.with(context)
                    .load(uri)
                    .centerCrop()
                    .placeholder(R.drawable.ic_user)
                    .into(imgUser)
            if (amount != 0) {
                txtAmount.text = String.format(Locale.getDefault(), "%s%s", "\u20B9 ", amount)
                if (currentUser.isGave.equals("true", ignoreCase = true)) {
                    txtAmount.setTextColor(resources.getColor(R.color.colorGet))
                } else {
                    txtAmount.setTextColor(resources.getColor(R.color.colorGive))
                }
            } else {
                txtAmount.text = "\u20B9 0"
                txtStatus.visibility = View.GONE
                txtAmount.setTextColor(resources.getColor(R.color.colorSecondaryText))
            }
            if (currentUser.isGave != null) {
                if (currentUser.isGave.equals("true", ignoreCase = true)) {
                    txtStatus.text = resources.getString(R.string.txt_get)
                    txtAmount.setTextColor(resources.getColor(R.color.colorGet))
                } else {
                    txtStatus.text = resources.getString(R.string.txt_give)
                    txtAmount.setTextColor(resources.getColor(R.color.colorGive))
                }
            }*/
        }

        init {
            txtDesc = itemView.findViewById(R.id.itemUserDesc)
            txtName = itemView.findViewById(R.id.itemUserName)
            imgUser = itemView.findViewById(R.id.imgUser)
            txtStatus = itemView.findViewById(R.id.txtStatus)
            txtAmount = itemView.findViewById(R.id.txtAmount)
            //itemClick
            itemView.setOnClickListener { v: View? -> selectUserListener.selectUserListener(filterUserList!![adapterPosition]) }
        }
    }

    interface SelectUser {
        fun selectUserListener(userModel: ClientModel?)
    }

    init {
        resources = context.resources
    }
}