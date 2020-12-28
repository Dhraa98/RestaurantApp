package com.zomato.view.fragment

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
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
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.yarolegovich.discretescrollview.DSVOrientation
import com.yarolegovich.discretescrollview.DiscreteScrollView
import com.yarolegovich.discretescrollview.InfiniteScrollAdapter
import com.yarolegovich.discretescrollview.transform.ScaleTransformer
import com.zomato.R
import com.zomato.database.TodoEntity
import com.zomato.database.TodoRoomDatabase
import com.zomato.databinding.FragmetMapBinding
import com.zomato.retrofit.RestaurantModel
import com.zomato.utils.BindingAdapter
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LAT_MAP
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LNG_MAP
import com.zomato.utils.DiscreteScrollViewOptions
import com.zomato.utils.PermissionUtils
import com.zomato.view.fragment.adapter.CuisinAdapter
import com.zomato.view.fragment.adapter.RestaurantAdapter
import com.zomato.view.fragment.viewmodel.MainActivityViewModel

class MapFragment() : Fragment(),
    OnMapReadyCallback,
    DiscreteScrollView.OnItemChangedListener<RestaurantAdapter.ViewHolder> {
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private var flag: Int = 0
    private var isPlaceSelected: Int = 0
    private var isCuisinSelected: Int = 0
    private lateinit var map: GoogleMap
    private lateinit var binding: FragmetMapBinding

    private lateinit var viewModel: MainActivityViewModel
    private lateinit var adapter: RestaurantAdapter
    private lateinit var manager: LinearLayoutManager
    private lateinit var managerCuisin: LinearLayoutManager
    private lateinit var adapterCuisin: CuisinAdapter
    private var cuisinList: ArrayList<String> = ArrayList()
    private var infiniteAdapter: InfiniteScrollAdapter<*>? = null

    var favouriteList: ArrayList<String> = ArrayList()
    private val options = MarkerOptions()
    val resList = ArrayList<RestaurantModel.NearbyRestaurant>()
    var restaurantList: List<RestaurantModel.NearbyRestaurant> = listOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmet_map, container, false)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)


        binding.lifecycleOwner = this
        if (PermissionUtils.isAccessFineLocationGranted(requireContext())) {
            initControls()
        } else {
            PermissionUtils.requestAccessFineLocationPermission(
                (activity as AppCompatActivity?)!!,
                LOCATION_PERMISSION_REQUEST_CODE
            )
        }

        return binding.root

    }


    private fun initControls() {
        val bundle = this.arguments
        isPlaceSelected = 1
        if (bundle != null) {
            viewModel.latitude = bundle.getDouble(EXTRA_KEY_LAT_MAP, 0.0)
            viewModel.longitude = bundle.getDouble(EXTRA_KEY_LNG_MAP, 0.0)

        }
        if (viewModel.latitude == 0.0 && viewModel.longitude == 0.0) {

        } else {

            viewModel.progressVisibility.value = true
            viewModel.getRestaurant()!!.observe(requireActivity(), Observer {
                if (it.isSuccessful) {
                    viewModel.progressVisibility.value = false
                    binding.itemPicker.visibility = View.VISIBLE
                    if (it.body()!!.nearbyRestaurants!!.size != null && it.body()!!.nearbyRestaurants!!.size > 0) {
                        flag = 1
                        /*  restaurantList =
                             it.body()!!.nearbyRestaurants!!*/
                        for (i in it.body()!!.nearbyRestaurants!!.indices) {
                            val lat =
                                it.body()!!.nearbyRestaurants!![i].restaurant!!.location!!.latitude!!.toDouble()
                            val lng =
                                it.body()!!.nearbyRestaurants!![i].restaurant!!.location!!.longitude!!.toDouble()
                            val latLng = LatLng(lat, lng)
                            options.position(latLng!!)
                            options.title(it.body()!!.nearbyRestaurants!![i].restaurant!!.name!!)

                            map.addMarker(options)

                        }

                        restaurantList = it.body()!!.nearbyRestaurants!!
                        for (i in it.body()!!.popularity!!.topCuisines!!.indices) {
                            cuisinList.add(it.body()!!.popularity!!.topCuisines!![i])
                        }
                        //  isAlreadyFavourite()
                        //   adapter = RestaurantAdapter(restaurantList, this)
                        initRestaurant(restaurantList)
                        initCuisine(restaurantList)

                        //  binding.rvHorizontal.adapter = adapter
                        //  binding.rvHorizontal.layoutManager = manager

                    }
                }

            })
        }

    }

    private fun initRestaurant(list: List<RestaurantModel.NearbyRestaurant>) {
        manager =
            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
        binding.itemPicker.setOrientation(DSVOrientation.HORIZONTAL)
        binding.itemPicker.addOnItemChangedListener(this)
        infiniteAdapter =
            InfiniteScrollAdapter.wrap(
                RestaurantAdapter(
                    viewModel, requireContext(),
                    list,
                    { position, Bool ->
                        if (Bool) {
                            val positionInDataSet =
                                infiniteAdapter!!.getRealPosition(position)
                            var todoEntity = TodoEntity()
                            if (isCuisinSelected == 1) {
                                todoEntity.avgRating =
                                    resList[positionInDataSet].restaurant!!.userRating!!.aggregateRating.toString()
                                todoEntity.cuisin =
                                    resList[positionInDataSet].restaurant!!.cuisines.toString()
                                todoEntity.listingName =
                                    resList[positionInDataSet].restaurant!!.name.toString()
                                todoEntity.minPrice =
                                    resList[positionInDataSet].restaurant!!.averageCostForTwo.toString()
                                todoEntity.imagePath =
                                    resList[positionInDataSet].restaurant!!.featuredImage.toString()
                                todoEntity.textBack =
                                    resList[positionInDataSet].restaurant!!.userRating!!.ratingColor.toString()
                                TodoRoomDatabase.getDatabase(activity!!).todoDao()
                                    .insertAll(todoEntity)
                            } else {
                                todoEntity.avgRating =
                                    restaurantList[positionInDataSet].restaurant!!.userRating!!.aggregateRating.toString()
                                todoEntity.cuisin =
                                    restaurantList[positionInDataSet].restaurant!!.cuisines.toString()
                                todoEntity.listingName =
                                    restaurantList[positionInDataSet].restaurant!!.name.toString()
                                todoEntity.minPrice =
                                    restaurantList[positionInDataSet].restaurant!!.averageCostForTwo.toString()
                                todoEntity.imagePath =
                                    restaurantList[positionInDataSet].restaurant!!.featuredImage.toString()
                                todoEntity.textBack =
                                    restaurantList[positionInDataSet].restaurant!!.userRating!!.ratingColor.toString()
                                TodoRoomDatabase.getDatabase(activity!!).todoDao()
                                    .insertAll(todoEntity)
                            }
                        } else {
                            val positionInDataSet =
                                infiniteAdapter!!.getRealPosition(position)
                            BindingAdapter.dataList.clear()
                            TodoRoomDatabase.getDatabase(activity!!).todoDao()
                                .getAll().forEach()
                                {
                                    BindingAdapter.dataList.addAll(listOf(it))
                                    Log.i("Fetch Records", "Id:  : ${it.Id}")
                                    Log.i(
                                        "Fetch Records",
                                        "Name:  : ${it.listingName}"
                                    )
                                }
                            loop@ for (i in BindingAdapter.dataList.indices) {
                                if (BindingAdapter.dataList[i].listingName.equals(
                                        restaurantList[positionInDataSet].restaurant!!.name
                                    )
                                ) {
                                    TodoRoomDatabase.getDatabase(activity!!)
                                        .todoDao()
                                        .delete(BindingAdapter.dataList[i])

                                    break@loop
                                }
                            }

                        }

                    }
                )
            )
        binding.itemPicker.setAdapter(infiniteAdapter)
        binding.itemPicker.setItemTransitionTimeMillis(DiscreteScrollViewOptions.getTransitionTime())
        binding.itemPicker.setItemTransformer(
            ScaleTransformer.Builder()
                .setMinScale(0.8f)
                .build()
        )
        onItemChanged(restaurantList.get(0));

    }

    private fun initCuisine(restaurantList: List<RestaurantModel.NearbyRestaurant>) {
        val selectedCuisine = ArrayList<String>()
        adapterCuisin = CuisinAdapter(cuisinList) { position, isChecked ->

            if (isChecked)
                selectedCuisine.add(cuisinList[position])
            else
                selectedCuisine.remove(cuisinList[position])

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
                    initRestaurant(restaurantList)
                    isCuisinSelected = 0
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

    private fun onItemChanged(restaurantList: RestaurantModel.NearbyRestaurant) {
        map?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(
                    restaurantList.restaurant!!.location!!.latitude!!.toDouble(),
                    restaurantList.restaurant!!.location!!.longitude!!.toDouble()
                ), 15f
            )
        )
    }

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 999
    }

    private fun initMap() {
        val mapFragment =
            childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        // requireActivity().supportFragmentManager.findFragmentById(R.id.googleMap) as? SupportMapFragment
        mapFragment.getMapAsync(this)
    }


    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap?) {
        if (PermissionUtils.isAccessFineLocationGranted(requireContext())) {


            map = googleMap!!
            val fusedLocationProviderClient =
                LocationServices.getFusedLocationProviderClient(activity!!)
            // for getting the current location update after every 2 seconds with high accuracy
            val locationRequest = LocationRequest().setInterval(2000).setFastestInterval(2000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
            fusedLocationProviderClient.lastLocation.addOnSuccessListener { location: Location? ->
                if (location != null) {
                    latitude = location.latitude
                    longitude = location.longitude
                    googleMap?.apply {
                        val currentLocation = LatLng(latitude, longitude)
                        addMarker(
                            MarkerOptions()
                                .position(currentLocation)
                                .title("currentLocation")
                        )
                        googleMap?.animateCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    latitude,
                                    longitude
                                ), 15f
                            )
                        )
                    }
                }
            }


        }

    }

    override fun onStart() {
        super.onStart()
        when {
            PermissionUtils.isAccessFineLocationGranted(requireContext()) -> {
                when {
                    PermissionUtils.isLocationEnabled(requireContext()) -> {
                        // setUpLocationListener()
                        initMap()
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
                            //  setUpLocationListener()
                            initMap()
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

    fun isAlreadyFavourite() {
        BindingAdapter.dataList.clear()
        if (TodoRoomDatabase.getDatabase(activity!!).todoDao().getAll().size > 0) {
            TodoRoomDatabase.getDatabase(activity!!).todoDao().getAll().forEach()
            {
                BindingAdapter.dataList.addAll(listOf(it))
                Log.i("Fetch Records", "Id:  : ${it.Id}")
                Log.i("Fetch Records", "Name:  : ${it.listingName}")
            }

            for (i in BindingAdapter.dataList.indices) {
                for (j in restaurantList.indices) {
                    if (BindingAdapter.dataList[i].listingName.equals(restaurantList[j].restaurant!!.name)) {
                        favouriteList.add(restaurantList[j].restaurant!!.name!!)
                    }
                }
            }
            adapter.notifyDataSetChanged()
        }
    }

    override fun setMenuVisibility(menuVisible: Boolean) {
        super.setMenuVisibility(menuVisible)
        if (menuVisible) {
            if (isPlaceSelected == 1) {
                adapter.notifyDataSetChanged()
            }
        }
    }

    override fun onCurrentItemChanged(
        viewHolder: RestaurantAdapter.ViewHolder?,
        adapterPosition: Int
    ) {
        val positionInDataSet = infiniteAdapter!!.getRealPosition(adapterPosition)
        onItemChanged(restaurantList.get(positionInDataSet))


    }
}