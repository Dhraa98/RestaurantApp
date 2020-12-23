package com.networking.retrofit


import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import com.zomato.R
import com.zomato.retrofit.RestaurantModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Headers
import retrofit2.http.Query
import java.util.*


interface RetrofitInterface {


    //get Videos
//    @Headers("user-key : ${R.string.header}")
    @GET("geocode")
    suspend fun getPlacesApi(
        @Header("user-key") headers: String,
        @Query("lat") lat: Double,
        @Query("lon") lon: Double
    ): Response<RestaurantModel>


}