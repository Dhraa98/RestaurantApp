package com.zomato.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.zomato.MainActivity
import com.zomato.R
import com.zomato.database.TodoEntity
import com.zomato.database.TodoRoomDatabase
import com.zomato.databinding.FragmentSearchBinding
import com.zomato.retrofit.RestaurantModel
import com.zomato.utils.BindingAdapter
import com.zomato.utils.BindingAdapter.EXTRA_KEY_CHOOSE_CURRENT
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LAT
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LAT_MAP
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LNG
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LNG_MAP
import com.zomato.utils.BindingAdapter.EXTRA_NAME
import com.zomato.utils.BindingAdapter.colorBackground
import com.zomato.utils.BindingAdapter.dataList
import com.zomato.utils.PermissionUtils
import com.zomato.view.fragment.adapter.CuisinAdapter
import com.zomato.view.fragment.adapter.RestaurantAdapter
import com.zomato.view.fragment.viewmodel.MainActivityViewModel


class SearchFragment : Fragment() {
    var restaurantList: List<RestaurantModel.NearbyRestaurant> = listOf()
    // var favouriteList: ArrayList<String> = ArrayList()
    // var cuisinList: List<RestaurantModel.Popularity> = listOf()

    // private var cuisinList: ArrayList<RestaurantModel.Popularity> = ArrayList()
    private var cuisinList: ArrayList<String> = ArrayList()
    val resList = ArrayList<RestaurantModel.NearbyRestaurant>()
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var flag: Int = 0
    private var isPlaceSelected: Int = 0
    private var isCuisinSelected: Int = 0
    private var isFirstTime: Boolean = true
    private lateinit var binding: FragmentSearchBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var adapter: RestaurantAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var adapterCuisin: CuisinAdapter
    private lateinit var managerCuisin: LinearLayoutManager

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_search, container, false)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel

        initControls()

        return binding.root

    }

    @SuppressLint("ResourceAsColor")
    private fun initControls() {
        if(!colorBackground.equals("")){
            binding.tvMainTxt.setTextColor(Color.parseColor(colorBackground))
        }
        binding.btnSearch.setOnClickListener {

            restaurantList = emptyList()
            cuisinList.clear()
            binding.rvCuisin.visibility = View.GONE
            binding.rvSearch.visibility = View.GONE
            val intent = Intent(activity, SearchPlaceActivity::class.java)
            startActivityForResult(intent, 101)
        }

        binding.ivMap.setOnClickListener {


            if (flag == 1) {
                flag = 0
                binding.ivMap.setImageResource(R.drawable.map)
                binding.rvSearch.visibility = View.VISIBLE
                binding.rvCuisin.visibility = View.VISIBLE
                binding.frm.visibility = View.GONE
                /*var searchFragment: SearchFragment = SearchFragment()
                binding.ivMap.setImageResource(R.drawable.map)
                childFragmentManager.beginTransaction()
                    .replace(R.id.frm, searchFragment).addToBackStack("tag")
                    .commit()*/

            } else {

                val bundle = Bundle()
                binding.rvSearch.visibility = View.GONE
                binding.rvCuisin.visibility = View.GONE
                if (isPlaceSelected == 1) {
                    bundle.putDouble(EXTRA_KEY_LAT_MAP, viewModel.latitude)
                    bundle.putDouble(EXTRA_KEY_LNG_MAP, viewModel.longitude)
                } else {

                    bundle.putDouble(EXTRA_KEY_LAT_MAP, 0.0)
                    bundle.putDouble(EXTRA_KEY_LNG_MAP, 0.0)
                }


                var mapFragment: MapFragment = MapFragment()

                mapFragment.setArguments(bundle)
                flag = 1
                binding.ivMap.setImageResource(R.drawable.menu)
                childFragmentManager.beginTransaction()
                    .replace(R.id.frm, mapFragment).addToBackStack("tag")
                    .commit()

            }


        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {


            /* binding.rvSearch.visibility = View.VISIBLE
             binding.frmContain.visibility = View.GONE*/
            if (data != null) {

                if (data!!.getBooleanExtra(EXTRA_KEY_CHOOSE_CURRENT, false)) {
                    viewModel.latitude = latitude
                    viewModel.longitude = longitude
                    viewModel.setAddress(viewModel.latitude, viewModel.longitude)
                    if (PermissionUtils.isAccessFineLocationGranted(requireContext())) {
                        binding.btnSearch.text = viewModel.searchText.value.toString()
                    } else {
                        binding.btnSearch.text = ""
                    }


                } else {
                    viewModel.latitude = data!!.getDoubleExtra(EXTRA_KEY_LAT, 0.0)
                    viewModel.longitude = data!!.getDoubleExtra(EXTRA_KEY_LNG, 0.0)
                    latitude = data!!.getDoubleExtra(EXTRA_KEY_LAT, 0.0)
                    longitude = data!!.getDoubleExtra(EXTRA_KEY_LNG, 0.0)
                    binding.btnSearch.text = data.getStringExtra(EXTRA_NAME)

                }
                viewModel.progressVisibility.value = true
                viewModel.getRestaurant().observe(this, Observer {

                    // your code here ...

                    viewModel.progressVisibility.value = false
                    binding.rvCuisin.visibility = View.VISIBLE
                    binding.rvSearch.visibility = View.VISIBLE
                    if (it.isSuccessful) {
                        if (it.body()!!.nearbyRestaurants!!.size != null && it.body()!!.nearbyRestaurants!!.size > 0) {

                            restaurantList = it.body()!!.nearbyRestaurants!!

                            for (i in it.body()!!.popularity!!.topCuisines!!.indices) {
                                cuisinList.add(it.body()!!.popularity!!.topCuisines!![i])
                            }
                            initRestaurant(restaurantList)
                            initCuisine(restaurantList)
                            isPlaceSelected = 1
                        }
                    }


                })

            }
        }
    }


    private fun initCuisine(restaurantList: List<RestaurantModel.NearbyRestaurant>) {
        val selectedCuisine = ArrayList<String>()
        adapterCuisin = CuisinAdapter(cuisinList) { position, isChecked ->

            if (isChecked) {


                resList.clear()
                selectedCuisine.add(cuisinList[position])

            } else {
                resList.clear()
                selectedCuisine.remove(cuisinList[position])
            }
            if (selectedCuisine.size != 0) {
                resList.clear()
                for (i in selectedCuisine.indices) {
                    for (k in restaurantList.indices) {
                        if (restaurantList[k].restaurant!!.cuisines!!.contains(selectedCuisine[i])) {
                            resList.add(restaurantList[k])
                            isCuisinSelected = 1

                        }
                    }
                }
                if (resList.isNullOrEmpty()) {
                    isCuisinSelected = 0
                    initRestaurant(restaurantList)
                } else {
                    initRestaurant(resList)
                }
            } else {
                isCuisinSelected = 0
                initRestaurant(restaurantList)
            }


        }
        managerCuisin =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCuisin.adapter = adapterCuisin
        binding.rvCuisin.layoutManager = managerCuisin
    }

    private fun initRestaurant(list: List<RestaurantModel.NearbyRestaurant>) {
        adapter = RestaurantAdapter(viewModel, requireContext(), list, { position, bool ->
            if (bool) {
                var todoEntity = TodoEntity()
                if (isCuisinSelected == 1) {
                    todoEntity.avgRating =
                        resList[position].restaurant!!.userRating!!.aggregateRating.toString()
                    todoEntity.cuisin = resList[position].restaurant!!.cuisines.toString()
                    todoEntity.listingName = resList[position].restaurant!!.name.toString()
                    todoEntity.minPrice =
                        resList[position].restaurant!!.averageCostForTwo.toString()
                    todoEntity.imagePath =
                        resList[position].restaurant!!.featuredImage.toString()
                    todoEntity.textBack =
                        resList[position].restaurant!!.userRating!!.ratingColor.toString()
                    TodoRoomDatabase.getDatabase(activity!!).todoDao().insertAll(todoEntity)
                } else {


                    todoEntity.avgRating =
                        restaurantList[position].restaurant!!.userRating!!.aggregateRating.toString()
                    todoEntity.cuisin = restaurantList[position].restaurant!!.cuisines.toString()
                    todoEntity.listingName = restaurantList[position].restaurant!!.name.toString()
                    todoEntity.minPrice =
                        restaurantList[position].restaurant!!.averageCostForTwo.toString()
                    todoEntity.imagePath =
                        restaurantList[position].restaurant!!.featuredImage.toString()
                    todoEntity.textBack =
                        restaurantList[position].restaurant!!.userRating!!.ratingColor.toString()
                    TodoRoomDatabase.getDatabase(activity!!).todoDao().insertAll(todoEntity)
                }
            } else {

                BindingAdapter.dataList.clear()
                TodoRoomDatabase.getDatabase(activity!!).todoDao().getAll().forEach()
                {
                    BindingAdapter.dataList.addAll(listOf(it))
                    Log.i("Fetch Records", "Id:  : ${it.Id}")
                    Log.i("Fetch Records", "Name:  : ${it.listingName}")
                }
                loop@ for (i in BindingAdapter.dataList.indices) {
                    if (BindingAdapter.dataList[i].listingName.equals(restaurantList[position].restaurant!!.name)) {
                        TodoRoomDatabase.getDatabase(activity!!).todoDao()
                            .delete(BindingAdapter.dataList[i])

                        break@loop
                    }
                }

            }

        })
        manager = LinearLayoutManager(activity)
        binding.rvSearch.adapter = adapter
        binding.rvSearch.layoutManager = manager
        adapter.notifyDataSetChanged()

    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(requireContext()) -> {
                when {
                    PermissionUtils.isLocationEnabled(requireContext()) -> {
                        setUpLocationListener()

                    }
                    else -> {
                        PermissionUtils.showGPSNotEnabledDialog(requireContext())
                    }
                }
            }
            else -> {
                PermissionUtils.requestAccessFineLocationPermission(
                    (activity as AppCompatActivity?)!!,
                    LOCATION_PERMISSION_REQUEST_CODE
                )
            }
        }
    }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    when {
                        PermissionUtils.isLocationEnabled(requireContext()) -> {
                            setUpLocationListener()

                        }
                        else -> {
                            PermissionUtils.showGPSNotEnabledDialog(requireContext())
                        }
                    }
                } else {
                    Toast.makeText(
                        activity,
                        getString(R.string.location_permission_not_granted),
                        Toast.LENGTH_LONG
                    ).show()
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    private fun setUpLocationListener() {
        val fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(activity!!)
        // for getting the current location update after every 2 seconds with high accuracy
        val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
            .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
        fusedLocationProviderClient.requestLocationUpdates(
            locationRequest,
            object : LocationCallback() {
                override fun onLocationResult(locationResult: LocationResult) {
                    super.onLocationResult(locationResult)
                    for (location in locationResult.locations) {
                        if (location != null) {
                            latitude = location.latitude
                            longitude = location.longitude

                        }

                    }

                }
            },
            Looper.myLooper()

        )
    }

    override fun onResume() {
        super.onResume()
        if(!colorBackground.equals("")){
            binding.tvMainTxt.setTextColor(Color.parseColor(colorBackground))
        }
    }


    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {

            /*if(!colorBackground.equals("")){
                binding.tvMainTxt.setTextColor(Color.parseColor(colorBackground))
            }*/
            if (isPlaceSelected == 1) {

                adapter.notifyDataSetChanged()
            }

            //  isAlreadyFavourite()


        }
    }

}


