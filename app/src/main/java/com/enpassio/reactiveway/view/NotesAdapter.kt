package com.enpassio.reactiveway.view

/**
 * Created by Greta Grigutė on 2018-09-13.
 *
 * This adapter class renders the RecyclerView with defined layout and data.
 */

import android.content.Context
import android.graphics.Color
import android.support.v7.widget.RecyclerView
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.enpassio.reactiveway.R
import com.enpassio.reactiveway.network.model.model.Note
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.note_list_row.*
import kotlinx.android.synthetic.main.note_list_row.view.*
import java.text.ParseException
import java.text.SimpleDateFormat


class NotesAdapter(private val context: Context, private val notesList: List<Note>) : RecyclerView.Adapter<NotesAdapter.MyViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
                .inflate(R.layout.note_list_row, parent, false)

        return MyViewHolder(itemView)
    }

    //solution for binding views in Kotlin from: https://antonioleiva.com/kotlin-android-extensions/
    class MyViewHolder(override val containerView: View) : RecyclerView.ViewHolder(containerView),
            LayoutContainer {

        fun bind() {
            containerView.note
            containerView.dot
            containerView.timestamp
        }
    }


    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val note = notesList[position]

        holder.note!!.setText(note.note)

        // Displaying dot from HTML character code
        holder.dot!!.text = Html.fromHtml("&#8226;")

        // Changing dot color to random color
        holder.dot!!.setTextColor(getRandomMaterialColor("400"))

        // Formatting and displaying timestamp
        holder.timestamp!!.text = formatDate(note.timestamp!!)
    }

    override fun getItemCount(): Int {
        return notesList.size
    }

    /**
     * Chooses random color defined in res/array.xml
     */
    private fun getRandomMaterialColor(typeColor: String): Int {
        var returnColor = Color.GRAY
        val arrayId = context.resources.getIdentifier("mdcolor_$typeColor", "array", context.packageName)

        if (arrayId != 0) {
            val colors = context.resources.obtainTypedArray(arrayId)
            val index = (Math.random() * colors.length()).toInt()
            returnColor = colors.getColor(index, Color.GRAY)
            colors.recycle()
        }
        return returnColor
    }

    /**
     * Formatting timestamp to `MMM d` format
     * Input: 2018-02-21 00:15:42
     * Output: Feb 21
     */
    private fun formatDate(dateStr: String): String {
        try {
            val fmt = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val date = fmt.parse(dateStr)
            val fmtOut = SimpleDateFormat("MMM d")
            return fmtOut.format(date)
        } catch (e: ParseException) {

        }

        return ""
    }
}