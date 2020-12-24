package com.zomato.view.fragment.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.liveData
import com.zomato.data.RestauratRepository
import com.zomato.retrofit.RestaurantModel
import kotlinx.coroutines.Dispatchers
import retrofit2.Response

class MainActivityViewModel(application: Application) : AndroidViewModel(application) {

    var latitude = 0.0
    var longitude = 0.0



    var progressVisibility: MutableLiveData<Boolean> = MutableLiveData(false)


    var itemClicked: MutableLiveData<Boolean> = MutableLiveData(false)

    val data: LiveData<Response<RestaurantModel>> = liveData(Dispatchers.IO) {
//        progressVisibility.value=true
        val userRepository = RestauratRepository(latitude, longitude)
        val retrievedData = userRepository.getUsers()
        emit(retrievedData)
//        progressVisibility.value=false

    }

}