package com.zomato.view.fragment.viewmodel

import android.app.Application
import android.location.Address
import android.location.Geocoder
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.zomato.data.RestauratRepository
import com.zomato.retrofit.RestaurantModel
import kotlinx.coroutines.Dispatchers
import retrofit2.Response
import java.io.IOException
import java.util.*

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var latitude = 0.0
    var longitude = 0.0

    val userRepository = RestauratRepository(latitude, longitude)
   /* fun getUser(): LiveData<RestaurantModel> {
       val loginResponseModelMutableLiveData = userRepository.getPlaces()
        return loginResponseModelMutableLiveData
    }
*/
    var progressVisibility: MutableLiveData<Boolean> = MutableLiveData(false)


    var itemClicked: MutableLiveData<Boolean> = MutableLiveData(false)
    fun getRestaurant() = liveData(Dispatchers.IO) {
        val userRepository = RestauratRepository(latitude, longitude)
        val retrievedData = userRepository.getUsers()
        emit(retrievedData)
    }
/*
    val data: LiveData<Response<RestaurantModel>> = liveData(Dispatchers.IO) {
//        progressVisibility.value=true
        val userRepository = RestauratRepository(latitude, longitude)
        val retrievedData = userRepository.getUsers()
        emit(retrievedData)

//        progressVisibility.value=false

    }*/

    var searchText: MutableLiveData<String> = MutableLiveData()

    fun setAddress(latitude: Double, longitude: Double) {
        val geocoder: Geocoder
        var addresses: List<Address>? = null
        geocoder = Geocoder(getApplication(), Locale.getDefault())
        try {
            addresses = geocoder.getFromLocation(
                latitude,
                longitude,
                1
            ) // Here 1 represent max location result to returned, by documents it recommended 1 to 5
        } catch (e: IOException) {
            e.printStackTrace()
        }
        if (addresses!!.size > 0) {
            Log.d("max", " " + addresses[0].maxAddressLineIndex)
            val address = addresses[0]
                .getAddressLine(0) // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()
            val city = addresses[0].locality
            val state = addresses[0].adminArea
            val country = addresses[0].countryName
            val postalCode = addresses[0].postalCode
            val knownName =
                addresses[0].featureName // Only if available else return NULL
            addresses[0].adminArea

            searchText.value=city


        }

    }

}