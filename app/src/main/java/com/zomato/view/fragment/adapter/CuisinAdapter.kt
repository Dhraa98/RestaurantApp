package com.zomato.view.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zomato.databinding.ItemCuisinListBinding
import com.zomato.retrofit.RestaurantModel

class CuisinAdapter(
    var cuisinList: ArrayList<String>,
    var chipCheckCallBack: (Int, Boolean) -> Unit
) :
    RecyclerView.Adapter<CuisinAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemCuisinListBinding) : RecyclerView.ViewHolder(binding.root) {

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CuisinAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemCuisinListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return cuisinList.size
    }

    override fun onBindViewHolder(holder: CuisinAdapter.ViewHolder, position: Int) {
        holder.binding.chip.text = cuisinList[position]
        holder.binding.chip.setOnCheckedChangeListener { buttonView, isChecked ->
            chipCheckCallBack(holder.adapterPosition,isChecked)
        }
    }
}