package com.enpassio.reactiveway.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.network.model.Ticket
import kotlinx.android.synthetic.main.ticket_row.view.*


/**
 * Created by Greta Grigutė on 2018-09-25.
 */
class TicketsAdapter(private val context: Context, private val contactList: List<Ticket>, private val listener: TicketsAdapterListener) : RecyclerView.Adapter<TicketsAdapter.MyViewHolder>() {

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        init {

            view.setOnClickListener {
                    // send selected contact in callback
                    listener.onTicketSelected(contactList[adapterPosition])
                }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.ticket_row, parent, false)

        return MyViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val ticket = contactList[position]

        Glide.with(context)
                .load(ticket.airline!!.logo)
                .apply(RequestOptions.circleCropTransform())
                .into(holder.itemView.logo)

        holder.itemView.airline_name.setText(ticket.airline!!.name)

        holder.itemView.departure.setText(ticket.departure + " Dep")
        holder.itemView.arrival.setText(ticket.arrival + " Dest")

        holder.itemView.duration.setText(ticket.flightNumber)
        holder.itemView.duration.append(", " + ticket.duration)
        holder.itemView.number_of_stops.setText(ticket.numberOfStops.toString() + " Stops")

        if (!TextUtils.isEmpty(ticket.instructions)) {
            holder.itemView.duration.append(", " + ticket.instructions)
        }

        if (ticket.price != null) {
            holder.itemView.price.text = "₹" + String.format("%.0f", ticket.price!!.price)
            holder.itemView.number_of_seats.setText(ticket.price!!.seats + " Seats")
            holder.itemView.loader.visibility = View.INVISIBLE
        } else {
            holder.itemView.loader.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return contactList.size
    }

    interface TicketsAdapterListener {
        fun onTicketSelected(contact: Ticket)
    }
}