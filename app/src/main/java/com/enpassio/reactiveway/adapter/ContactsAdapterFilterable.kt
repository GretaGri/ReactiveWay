package com.enpassio.reactiveway.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.network.model.Contact


/**
 * Created by Greta Grigutė on 2018-09-23.
 */
class ContactsAdapterFilterable(private val context: Context, private val contactList: List<Contact>, private val listener: ContactsAdapterListener) : RecyclerView.Adapter<ContactsAdapterFilterable.MyViewHolder>(), Filterable {
    private var contactListFiltered: List<Contact>? = null

    // name match condition. this might differ depending on your requirement
    // here we are looking for name or phone number match
    override fun getFilter(): Filter{
        return object : Filter() {
            override fun performFiltering(charSequence: CharSequence): Filter.FilterResults {
                val charString = charSequence.toString()
                if (charString.isEmpty()) {
                    contactListFiltered = contactList
                } else {
                    val filteredList = ArrayList<Contact>()
                    for (row in contactList) {
                        if (row.name!!.toLowerCase().contains(charString.toLowerCase()) || row.phone!!.contains(charSequence)) {
                            filteredList.add(row)
                        }
                    }

                    contactListFiltered = filteredList
                }

                val filterResults = Filter.FilterResults()
                filterResults.values = contactListFiltered
                return filterResults
            }


           override fun publishResults(charSequence: CharSequence, filterResults: Filter.FilterResults) {
                contactListFiltered = filterResults.values as List<Contact>?
                notifyDataSetChanged()
            }
        }
        }

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var name: TextView
        var phone: TextView
        var thumbnail: ImageView

        init {
            name = view.findViewById(R.id.name)
            phone = view.findViewById(R.id.phone)
            thumbnail = view.findViewById(R.id.thumbnail)

            view.setOnClickListener {
                    // send selected contact in callback
                    listener.onContactSelected(contactListFiltered!![adapterPosition])
                }
        }
    }


    init {
        this.contactListFiltered = contactList
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_row_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contactListFiltered!![position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone

        Glide.with(context)
                .load(contact.profileImage)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail)
    }

    override fun getItemCount(): Int {
        return contactListFiltered!!.size
    }

    interface ContactsAdapterListener {
        fun onContactSelected(contact: Contact)
    }
}