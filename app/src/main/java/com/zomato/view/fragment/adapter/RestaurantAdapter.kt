package com.zomato.view.fragment.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zomato.R
import com.zomato.databinding.ItemSearchListBinding
import com.zomato.retrofit.RestaurantModel
import com.zomato.view.fragment.viewmodel.MainActivityViewModel

class RestaurantAdapter(
    var viewMode : MainActivityViewModel,
    var restaurantList: List<RestaurantModel.NearbyRestaurant>,
    var callback : (Int , Boolean) ->Unit

) :
    RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemSearchListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun Bind(
            viewMode: MainActivityViewModel,
            restaurantList: RestaurantModel.NearbyRestaurant
        ) {
            binding.restaurantList = restaurantList
            binding.viewmodel = viewMode


            binding.executePendingBindings()
        }
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): RestaurantAdapter.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemSearchListBinding.inflate(inflater, parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return restaurantList.size
    }

    interface ProductItemClickListener {
        //        android:src="@{viewmodel.itemClicked ? @drawable/ic_baseline_favorite_border_24 : @drawable/ic_baseline_favorite_24}"
        fun onProductItemClicked(restaurant: RestaurantModel.NearbyRestaurant)

    }

    override fun onBindViewHolder(holder: RestaurantAdapter.ViewHolder, position: Int) {
        holder.Bind(viewMode, restaurantList[position])

        holder.binding.ivFav.setOnClickListener {
            if (holder.binding.ivFav.isSelected){
                holder.binding.ivFav.setImageResource(R.drawable.ic_baseline_favorite_border_24)
                holder.binding.ivFav.isSelected = false
                callback(holder.adapterPosition,false)
            }else{
                holder.binding.ivFav.setImageResource(R.drawable.ic_baseline_favorite_24)
                holder.binding.ivFav.isSelected = true
                callback(holder.adapterPosition,true)
            }
        }

    }

}