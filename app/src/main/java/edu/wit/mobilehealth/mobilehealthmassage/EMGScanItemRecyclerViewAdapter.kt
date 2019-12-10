package edu.wit.mobilehealth.mobilehealthmassage

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView

import kotlinx.android.synthetic.main.fragment_emgscanitem.view.*


class EMGScanItemRecyclerViewAdapter(
        private val historyList: List<EMGScanItem>)
    : RecyclerView.Adapter<EMGScanItemRecyclerViewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.fragment_emgscanitem, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = historyList[position]
        holder.dateView.text = item.date
        holder.descriptionView.text = item.description
        holder.valueView.text = String.format("%.2f", item.value)

    }

    override fun getItemCount(): Int = historyList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val valueView: TextView = view.value
        val descriptionView: TextView = view.description
        val dateView: TextView = view.date
    }
}
