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
    private val mListener: ProductItemClickListener
) :
    RecyclerView.Adapter<RestaurantAdapter.ViewHolder>() {
    class ViewHolder(val binding: ItemSearchListBinding) : RecyclerView.ViewHolder(binding.root) {
        fun Bind(
             viewMode : MainActivityViewModel,
            restaurantList: RestaurantModel.NearbyRestaurant,
            listener: ProductItemClickListener
        ) {
            binding.restaurantList = restaurantList
            binding.viewmodel = viewMode
            binding.itemClick = listener

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

    override fun onBindViewHolder(holder: RestaurantAdapter.ViewHolder, position: Int) =
        holder.Bind(viewMode,restaurantList[position], mListener)
}