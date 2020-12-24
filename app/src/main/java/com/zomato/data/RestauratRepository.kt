package com.zomato.data

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.gson.JsonObject
import com.networking.retrofit.RetrofitClass
import com.zomato.R
import com.zomato.retrofit.RestaurantModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RestauratRepository(var lat : Double,var lng : Double) {

    suspend fun getUsers() = RetrofitClass.getClient.getPlacesApi(
        "1cc5f7df7bf1425e718a4ca9c504cada",
        lat,
        lng
    )
   /* var dataValue: MutableLiveData<RestaurantModel> = MutableLiveData()
    private  val TAG = "RestauratRepository"



    fun getPlaces(): LiveData<RestaurantModel> {

        val call: Call<RestaurantModel> =
            RetrofitClass.getClient.getPlacesApi("1cc5f7df7bf1425e718a4ca9c504cada",
                lat,
                lng)

        call.enqueue(object : Callback<RestaurantModel> {

            override fun onResponse(
                call: Call<RestaurantModel>?,
                response: Response<RestaurantModel>?
            ) {
                if (response!!.isSuccessful) {


                    if (response.body()!!.nearbyRestaurants!!.size != null && response.body()!!.nearbyRestaurants!!.size > 0) {

                            dataValue.postValue( response.body())



                    }else{
                        Log.e(TAG, "onResponse: " )
                    }
                }else{
                    Log.e(TAG, "onResponse:  "+response.message() )
                }


            }

            override fun onFailure(call: Call<RestaurantModel>?, t: Throwable?) {
                dataValue.postValue( null)
                //  Toast.makeText(context, t!!.message, Toast.LENGTH_SHORT).show()
            }
        })
        return dataValue
    }*/
}