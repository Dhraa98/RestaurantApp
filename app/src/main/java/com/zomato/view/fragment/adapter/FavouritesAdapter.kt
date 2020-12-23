package com.zomato.view.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zomato.database.TodoEntity
import com.zomato.databinding.ItemDbListBinding
import com.zomato.databinding.ItemSearchListBinding
import com.zomato.retrofit.RestaurantModel

class FavouritesAdapter(
    var dataList: MutableList<TodoEntity>,
    private val mListener: ProductItemClickListener
) :
    RecyclerView.Adapter<FavouritesAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemDbListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun Bind(dataList: TodoEntity, listener: ProductItemClickListener) {
            binding.dbList = dataList
            binding.itemClick = listener
            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): FavouritesAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemDbListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    interface ProductItemClickListener {
        fun onProductItemClicked(restaurant: TodoEntity)

    }

    override fun onBindViewHolder(holder: FavouritesAdapter.ViewHolder, position: Int) =
        holder.Bind(dataList[position], mListener)
}