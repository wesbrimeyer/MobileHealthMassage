package edu.wit.mobilehealth.mobilehealthmassage

import androidx.recyclerview.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView


import edu.wit.mobilehealth.mobilehealthmassage.EMGHistoryListFragment.OnListFragmentInteractionListener

import kotlinx.android.synthetic.main.fragment_emgscanitem.view.*

/**
 * [RecyclerView.Adapter] that can display a [EMGScanItem] and makes a call to the
 * specified [OnListFragmentInteractionListener].
 */
class EMGScanItemRecyclerViewAdapter(
        private val historyList: List<EMGScanItem>,
        private val listener: OnListFragmentInteractionListener?)
    : RecyclerView.Adapter<EMGScanItemRecyclerViewAdapter.ViewHolder>() {

    private val onClickListener: View.OnClickListener

    init {
        onClickListener = View.OnClickListener { v ->
            val item = v.tag as EMGScanItem
            // Notify the active callbacks interface (the activity, if the fragment is attached to
            // one) that an item has been selected.
            listener?.onListFragmentInteraction(item)
        }
    }

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

        with(holder.view) {
            tag = item
            setOnClickListener(onClickListener)
        }
    }

    override fun getItemCount(): Int = historyList.size

    inner class ViewHolder(val view: View) : RecyclerView.ViewHolder(view) {
        val valueView: TextView = view.value
        val descriptionView: TextView = view.description
        val dateView: TextView = view.date
    }
}
