package com.zomato.data

import com.networking.retrofit.RetrofitClass
import com.zomato.R

class RestauratRepository(var lat : Double,var lng : Double) {
    suspend fun getUsers() = RetrofitClass.getClient.getPlacesApi(
        "1cc5f7df7bf1425e718a4ca9c504cada",
        lat,
        lng
    )
}