package com.zomato

import android.app.Application
import com.zomato.utils.DiscreteScrollViewOptions




class App : Application() {
    companion object{
        private var instance: App? = null

        fun getInstance(): App? {
            return instance
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        DiscreteScrollViewOptions.init(this)
    }
}