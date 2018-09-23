package com.enpassio.reactiveway.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.network.model.Contact


/**
 * Created by Greta Grigutė on 2018-09-24.
 */
class ContactsAdapter(private val context: Context, private val contactList: List<Contact>, private val listener: ContactsAdapterListener) : RecyclerView.Adapter<ContactsAdapter.MyViewHolder>() {

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
                    listener.onContactSelected(contactList[adapterPosition])
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.contact_row_item, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val contact = contactList[position]
        holder.name.text = contact.name
        holder.phone.text = contact.phone

        Glide.with(context)
                .load(contact.profileImage)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.thumbnail)
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    interface ContactsAdapterListener {
        fun onContactSelected(contact: Contact)
    }
}