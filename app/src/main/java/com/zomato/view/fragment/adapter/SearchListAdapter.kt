package com.zomato.view.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.zomato.databinding.ItemListSearchBinding

class SearchListAdapter(val searchList: MutableList<AutocompletePrediction>, private val callback: (position: Int) -> Unit) :
    RecyclerView.Adapter<SearchListAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemListSearchBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): SearchListAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemListSearchBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return searchList.size
    }

    override fun onBindViewHolder(holder: SearchListAdapter.ViewHolder, position: Int) {
        holder.binding.tvSearchListText.text = searchList[position].getPrimaryText(null).toString()
        holder.itemView.setOnClickListener {
            callback(position)
        }
    }
}