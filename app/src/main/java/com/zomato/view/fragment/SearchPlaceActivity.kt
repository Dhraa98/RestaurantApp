package com.zomato.view.fragment

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompletePrediction
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FetchPlaceResponse
import com.google.android.libraries.places.api.net.PlacesClient
import com.zomato.R
import com.zomato.databinding.ActivitySearchPlaceBinding
import com.zomato.utils.BindingAdapter.EXTRA_KEY_CHOOSE_CURRENT
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LAT
import com.zomato.utils.BindingAdapter.EXTRA_KEY_LNG
import com.zomato.view.fragment.adapter.SearchListAdapter
import com.zomato.view.fragment.viewmodel.SearchPlacesViewModel


class SearchPlaceActivity : AppCompatActivity() {
    private val TAG = "SearchPlaceActivity"
    private var flag : Boolean=false
    private lateinit var binding: ActivitySearchPlaceBinding
    lateinit var placesClient: PlacesClient
    lateinit var adapter: SearchListAdapter
    lateinit var manager: RecyclerView.LayoutManager
    private var searchList: MutableList<AutocompletePrediction> = mutableListOf()
    val viewModel: SearchPlacesViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_search_place)
        initControls()
    }

    private fun initControls() {
        binding.lifecycleOwner = this
        binding.viewmodel = viewModel
        val apiKey = "AIzaSyDFjbzwtDSTsbCKwwjcSakbWjmM6I6nIhw"
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, apiKey)
        }
        placesClient = Places.createClient(this)
        binding.ivClose.setOnClickListener {
            onBackPressed()
        }
        binding.lnChooseCurrentLocation.setOnClickListener {
            flag=true
            val intent = Intent()
            intent.putExtra(
                EXTRA_KEY_CHOOSE_CURRENT,
                flag
            )

            setResult(
                Activity.RESULT_OK,
                intent
            ) // You can also send result without any data using setResult(int resultCode)

            finish()
        }
        binding.etInputtext.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {

            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                binding.lnChooseCurrentLocation.visibility=View.GONE
                viewModel.searchResult(s.toString())
                viewModel.responseData.observe(this@SearchPlaceActivity, Observer {
                    searchList.add(it)
                    adapter = SearchListAdapter(searchList, { position ->
                        val placeFields =
                            listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)

// Construct a request object, passing the place ID and fields array.
                        val request =
                            FetchPlaceRequest.newInstance(searchList[position].placeId, placeFields)

                        placesClient.fetchPlace(request)
                            .addOnSuccessListener { response: FetchPlaceResponse ->
                                val place = response.place
                                Log.e(TAG, "Place found: ${place.name},${place.latLng}")
                                val placeLat = place.latLng!!.latitude
                                val placeLng = place.latLng!!.longitude
                                flag=false
                                val intent = Intent()
                                intent.putExtra(
                                    EXTRA_KEY_LAT,
                                    placeLat
                                )
                                intent.putExtra(
                                    EXTRA_KEY_LNG,
                                    placeLng
                                )
                                setResult(
                                    Activity.RESULT_OK,
                                    intent
                                ) // You can also send result without any data using setResult(int resultCode)

                                finish()
                            }.addOnFailureListener { exception: Exception ->
                                if (exception is ApiException) {
                                    Log.e(TAG, "Place not found: ${exception.message}")
                                    val statusCode = exception.statusCode

                                }
                            }


                        Toast.makeText(
                            this@SearchPlaceActivity,
                            searchList[position].getPrimaryText(null).toString(),
                            Toast.LENGTH_LONG
                        ).show()
                        Log.e(TAG, "onTextChanged: " + searchList[position])
                        binding.rvSearchList.visibility = View.GONE
                        binding.etInputtext.text = null

                    })
                    manager = LinearLayoutManager(this@SearchPlaceActivity)
                    binding.rvSearchList.adapter = adapter
                    binding.rvSearchList.layoutManager = manager
                })

                if (binding.etInputtext.text.toString().length > 0) {
                    binding.rvSearchList.visibility = View.VISIBLE
                } else {
                    searchList.clear()
                    adapter.notifyDataSetChanged()
                    binding.rvSearchList.visibility = View.GONE
                }
            }

        })

    }


}