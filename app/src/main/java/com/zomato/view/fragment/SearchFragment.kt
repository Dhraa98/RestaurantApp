package com.zomato.view.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.zomato.R
import com.zomato.database.TodoEntity
import com.zomato.database.TodoRoomDatabase
import com.zomato.databinding.FragmentSearchBinding
import com.zomato.retrofit.RestaurantModel
import com.zomato.utils.BindingAdapter.EXTRA_KEY_CHOOSE_CURRENT
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LAT
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LAT_MAP
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LNG
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LNG_MAP
import com.zomato.utils.PermissionUtils
import com.zomato.view.fragment.adapter.CuisinAdapter
import com.zomato.view.fragment.adapter.RestaurantAdapter
import com.zomato.view.fragment.viewmodel.MainActivityViewModel


class SearchFragment : Fragment(), RestaurantAdapter.ProductItemClickListener {
    var restaurantList: List<RestaurantModel.NearbyRestaurant> = listOf()
    // var cuisinList: List<RestaurantModel.Popularity> = listOf()

    // private var cuisinList: ArrayList<RestaurantModel.Popularity> = ArrayList()
    private var cuisinList: ArrayList<String> = ArrayList()
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
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
        initControls()
        return binding.root

    }

    private fun initControls() {

        binding.btnSearch.setOnClickListener {
            restaurantList = emptyList()
            cuisinList.clear()
            val intent = Intent(activity, SearchPlaceActivity::class.java)
            startActivityForResult(intent, 101)
        }
        binding.ivMap.setOnClickListener {
            binding.rvSearch.visibility = View.GONE

            val bundle = Bundle()
            bundle.putDouble(EXTRA_KEY_LAT_MAP, viewModel.latitude)
            bundle.putDouble(EXTRA_KEY_LNG_MAP, viewModel.longitude)

            var mapFragment: MapFragment = MapFragment()
            mapFragment.setArguments(bundle)
            childFragmentManager.beginTransaction()
                .replace(R.id.frm_contain, mapFragment)
                .commit()


        }
        /* viewModel.data.observe(requireActivity(), Observer {
             if (it.isSuccessful) {
                 if (it.body()!!.nearbyRestaurants!!.size != null && it.body()!!.nearbyRestaurants!!.size > 0) {
                     *//*val restaurantList: List<RestaurantModel.NearbyRestaurant> =
                        it.body()!!.nearbyRestaurants!!*//*
                    restaurantList = it.body()!!.nearbyRestaurants!!
                    adapter = RestaurantAdapter(restaurantList, this)
                    manager = LinearLayoutManager(activity)
                    binding.rvSearch.adapter = adapter
                    binding.rvSearch.layoutManager = manager
                }
            }

        })*/
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            /* binding.rvSearch.visibility = View.VISIBLE
             binding.frmContain.visibility = View.GONE*/
            if (data!!.getBooleanExtra(EXTRA_KEY_CHOOSE_CURRENT, false)) {
                viewModel.latitude = latitude
                viewModel.longitude = longitude
            } else {
                viewModel.latitude = data!!.getDoubleExtra(EXTRA_KEY_LAT, 0.0)
                viewModel.longitude = data!!.getDoubleExtra(EXTRA_KEY_LNG, 0.0)
                latitude = data!!.getDoubleExtra(EXTRA_KEY_LAT, 0.0)
                longitude = data!!.getDoubleExtra(EXTRA_KEY_LNG, 0.0)
            }

            viewModel.data.observe(requireActivity(), Observer {
                if (it.isSuccessful) {
                    if (it.body()!!.nearbyRestaurants!!.size != null && it.body()!!.nearbyRestaurants!!.size > 0) {

                        restaurantList = it.body()!!.nearbyRestaurants!!

                        for (i in it.body()!!.popularity!!.topCuisines!!.indices) {
                            cuisinList.add(it.body()!!.popularity!!.topCuisines!![i])
                        }
                        initRestaurant(restaurantList)
                        initCuisine(restaurantList)
                    }
                }

            })
        }
    }

    private fun initCuisine(restaurantList: List<RestaurantModel.NearbyRestaurant>) {
        val selectedCuisine = ArrayList<String>()
        adapterCuisin = CuisinAdapter(cuisinList) { position, isChecked ->
            if (isChecked) {
                selectedCuisine.add(cuisinList[position])
            } else {
                selectedCuisine.remove(cuisinList[position])
            }

            val resList = ArrayList<RestaurantModel.NearbyRestaurant>()
            for (i in selectedCuisine.indices) {
                for (k in restaurantList.indices) {
                    if (restaurantList[k].restaurant!!.cuisines == selectedCuisine[i]) {
                        resList.add(restaurantList[k])
                    }
                }
            }
            if (resList.isNullOrEmpty()) {
                initRestaurant(restaurantList)
            } else {
                initRestaurant(resList)
            }
        }
        managerCuisin =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.rvCuisin.adapter = adapterCuisin
        binding.rvCuisin.layoutManager = managerCuisin
    }

    private fun initRestaurant(list: List<RestaurantModel.NearbyRestaurant>) {
        adapter = RestaurantAdapter(viewModel, list, this)
        manager = LinearLayoutManager(activity)
        binding.rvSearch.adapter = adapter
        binding.rvSearch.layoutManager = manager

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

                        // latTextView.text = location.latitude.toString()
                        // lngTextView.text = location.longitude.toString()
                    }
                    // Few more things we can do here:
                    // For example: Update the location of user on server
                }
            },
            Looper.myLooper()

        )
    }

    override fun onProductItemClicked(restaurant: RestaurantModel.NearbyRestaurant) {
        var todoEntity = TodoEntity()
        todoEntity.avgRating = restaurant.restaurant!!.userRating!!.aggregateRating.toString()
        todoEntity.cuisin = restaurant.restaurant!!.cuisines.toString()
        todoEntity.listingName = restaurant.restaurant!!.name.toString()
        todoEntity.minPrice = restaurant.restaurant!!.averageCostForTwo.toString()
        todoEntity.imagePath = restaurant.restaurant!!.featuredImage.toString()
        TodoRoomDatabase.getDatabase(activity!!).todoDao().insertAll(todoEntity)

//        if (viewModel.itemClicked.value!!){
//            viewModel.itemClicked.value = false
//        }else{
//            viewModel.itemClicked.value = true
//        }

//        initRestaurant(restaurantList)
    }

    override fun setUserVisibleHint(isVisibleToUser: Boolean) {
        super.setUserVisibleHint(isVisibleToUser)
        if (isVisibleToUser) {
            viewModel.latitude = latitude
            viewModel.longitude = longitude
            viewModel.data.observe(requireActivity(), Observer {
                if (it.isSuccessful) {
                    if (it.body()!!.nearbyRestaurants!!.size != null && it.body()!!.nearbyRestaurants!!.size > 0) {

                        restaurantList = it.body()!!.nearbyRestaurants!!

                        for (i in it.body()!!.popularity!!.topCuisines!!.indices) {
                            cuisinList.add(it.body()!!.popularity!!.topCuisines!![i])
                        }
                        initRestaurant(restaurantList)
                        initCuisine(restaurantList)
                    }
                }

            })
        }
    }
}