package com.zomato.utils

import android.widget.ImageView
import androidx.databinding.BindingAdapter
import coil.load
import com.zomato.database.TodoEntity

object BindingAdapter {
    var dataList: MutableList<TodoEntity> = mutableListOf()
    var EXTRA_KEY_LAT : String="EXTRA_KEY_LET"
    var EXTRA_KEY_LNG : String="EXTRA_KEY_LNG"
    var EXTRA_KEY_LAT_MAP : String="EXTRA_KEY_LAT_MAP"
    var EXTRA_KEY_LNG_MAP : String="EXTRA_KEY_LNG_MAP"
    var EXTRA_KEY_CHOOSE_CURRENT : String="EXTRA_KEY_CHOOSE_CURRENT"
    @JvmStatic
    @BindingAdapter("imageUrl")
    fun setImageUrl(imageView: ImageView, url: String?) {

        imageView.load(url) {
            crossfade(true)

        }
    }
}