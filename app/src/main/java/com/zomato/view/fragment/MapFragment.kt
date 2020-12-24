package com.zomato.view.fragment

import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
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
import com.zomato.databinding.FragmetMapBinding
import com.zomato.retrofit.RestaurantModel
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LAT_MAP
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LNG_MAP
import com.zomato.utils.DiscreteScrollViewOptions
import com.zomato.utils.PermissionUtils
import com.zomato.view.fragment.adapter.RestaurantAdapter
import com.zomato.view.fragment.viewmodel.MainActivityViewModel

class MapFragment() : Fragment(),
    OnMapReadyCallback,
    DiscreteScrollView.OnItemChangedListener<RestaurantAdapter.ViewHolder> {
    private var latitude: Double = 0.0
    private var longitude: Double = 0.0
    private lateinit var map: GoogleMap
    private lateinit var binding: FragmetMapBinding
    private lateinit var viewModel: MainActivityViewModel
    private lateinit var adapter: RestaurantAdapter
    private lateinit var manager: LinearLayoutManager
    private var infiniteAdapter: InfiniteScrollAdapter<*>? = null

    private var latlngs: ArrayList<LatLng> = ArrayList()
    private val options = MarkerOptions()
    var restaurantList: List<RestaurantModel.NearbyRestaurant> = listOf()
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragmet_map, container, false)
        viewModel = ViewModelProvider(this).get(MainActivityViewModel::class.java)


        binding.lifecycleOwner = this
        initControls()
        return binding.root

    }


    private fun initControls() {
        val bundle = this.arguments
        if (bundle != null) {
            viewModel.latitude = bundle.getDouble(EXTRA_KEY_LAT_MAP, 0.0)
            viewModel.longitude = bundle.getDouble(EXTRA_KEY_LNG_MAP, 0.0)

        }
        if (viewModel.latitude == 0.0 && viewModel.longitude == 0.0) {

        } else {


            viewModel.data.observe(requireActivity(), Observer {
                if (it.isSuccessful) {
                    if (it.body()!!.nearbyRestaurants!!.size != null && it.body()!!.nearbyRestaurants!!.size > 0) {
                        /*val restaurantList: List<RestaurantModel.NearbyRestaurant> =
                            it.body()!!.nearbyRestaurants!!*/
                        for (i in it.body()!!.nearbyRestaurants!!.indices) {
                            val lat =
                                it.body()!!.nearbyRestaurants!![i].restaurant!!.location!!.latitude!!.toDouble()
                            val lng =
                                it.body()!!.nearbyRestaurants!![i].restaurant!!.location!!.longitude!!.toDouble()
                            val latLng = LatLng(lat, lng)
                            latlngs.add(latLng)
                        }
                        for (point in latlngs) {
                            options.position(point!!)
                            options.title("someTitle")
                            options.snippet("someDesc")
                            map.addMarker(options)
                        }

                        restaurantList = it.body()!!.nearbyRestaurants!!
                        //   adapter = RestaurantAdapter(restaurantList, this)
                        manager =
                            LinearLayoutManager(activity, LinearLayoutManager.HORIZONTAL, false)
                        binding.itemPicker.setOrientation(DSVOrientation.HORIZONTAL)
                        binding.itemPicker.addOnItemChangedListener(this)
                        infiniteAdapter =
                            InfiniteScrollAdapter.wrap(
                                RestaurantAdapter(
                                    viewModel,
                                    restaurantList,
                                    { position,Bool->

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
                        //  binding.rvHorizontal.adapter = adapter
                        //  binding.rvHorizontal.layoutManager = manager

                    }
                }

            })
        }
        /*  binding.rvHorizontal.addOnScrollListener(object : RecyclerView.OnScrollListener() {
              override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                  super.onScrolled(recyclerView, dx, dy)

                  recyclerView.post {

                  }
                  val firstPos: Int = manager.findFirstVisibleItemPosition()
                  val lastPos: Int = manager.findLastVisibleItemPosition()
                  val middle = Math.abs(lastPos - firstPos) / 2 + firstPos

                  var selectedPos = -1
                  *//* for (i in 0 until adapter.itemCount) {
                     if (i == middle) {
                         adapter.getItem(i).setSelected(true)
                         selectedPos = i
                     } else {
                         adapter.getItem(i).setSelected(false)
                     }
                 }*//*

                adapter.notifyDataSetChanged()
            }
        })*/

        /* binding.rvHorizontal.addOnItemTouchListener(
             RecyclerItemClickListener(
                 context,
                 binding.rvHorizontal,
                 object : AdapterView.OnItemClickListener() {

                     override fun onItemClick(
                         parent: AdapterView<*>?,
                         view: View?,
                         position: Int,
                         id: Long
                     ) {
                         binding.rvHorizontal.smoothScrollToPosition(position)
                     }
                 })
         )*/
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

        /* fusedLocationProviderClient.requestLocationUpdates(
             locationRequest,
             object : LocationCallback() {
                 override fun onLocationResult(locationResult: LocationResult) {
                     super.onLocationResult(locationResult)
                     for (location in locationResult.locations) {
                         if(location!=null){
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

         )*/


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



    override fun onCurrentItemChanged(
        viewHolder: RestaurantAdapter.ViewHolder?,
        adapterPosition: Int
    ) {
        val positionInDataSet = infiniteAdapter!!.getRealPosition(adapterPosition)
        onItemChanged(restaurantList.get(positionInDataSet))
    }
}