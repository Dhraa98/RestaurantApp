package com.zomato.view.fragment

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zomato.MainActivity
import com.zomato.R
import com.zomato.database.TodoEntity
import com.zomato.database.TodoRoomDatabase
import com.zomato.databinding.FragmentFavouritesBinding
import com.zomato.retrofit.RestaurantModel
import com.zomato.utils.BindingAdapter.colorBackground
import com.zomato.utils.BindingAdapter.dataList
import com.zomato.view.fragment.adapter.FavouritesAdapter

class FavouriteFragment : Fragment(), FavouritesAdapter.ProductItemClickListener {
    private lateinit var binding: FragmentFavouritesBinding
    private lateinit var adapter: FavouritesAdapter
    private lateinit var manager: RecyclerView.LayoutManager
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_favourites, container, false)
        binding.lifecycleOwner = this
        initControls()
        return binding.root

    }

    @SuppressLint("ResourceAsColor")
    private fun initControls() {
        if(!colorBackground.equals("")){
            binding.tvTxt.setTextColor(Color.parseColor(colorBackground))
        }
        dataList.clear()
        TodoRoomDatabase.getDatabase(activity!!).todoDao().getAll().forEach()
        {
            dataList.addAll(listOf(it))
            Log.i("Fetch Records", "Id:  : ${it.Id}")
            Log.i("Fetch Records", "Name:  : ${it.listingName}")
        }
        // binding.progress.visibility = View.GONE
        adapter = FavouritesAdapter(dataList, this)
        manager = LinearLayoutManager(activity)
        binding.rvFavourites.adapter = adapter
        binding.rvFavourites.layoutManager = manager
        adapter.notifyDataSetChanged()
    }


    override fun onProductItemClicked(restaurant: TodoEntity) {
        TodoRoomDatabase.getDatabase(requireContext()).todoDao().delete(restaurant)
        adapter.notifyDataSetChanged()
        dataList.remove(restaurant)
    }



    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible)
            initControls()
    }

}