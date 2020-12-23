package com.zomato

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.databinding.DataBindingUtil
import com.zomato.databinding.ActivityMainBinding
import com.zomato.view.fragment.FavouriteFragment
import com.zomato.view.fragment.SearchFragment

class MainActivity : AppCompatActivity() {
    private lateinit var bindig: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindig = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initControls()
    }

    private fun initControls() {
        bindig.lifecycleOwner = this
        var searchFragment: SearchFragment = SearchFragment()
        supportFragmentManager.beginTransaction()
            .replace(R.id.frm_contain, searchFragment)
            .commit()
        bindig.navigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.page_1 -> {
                    var searchFragment: SearchFragment = SearchFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_contain, searchFragment)
                        .commit()
                    // Respond to navigation item 1 click
                    true
                }
                R.id.page_2 -> {
                    var favouritesFragment: FavouriteFragment = FavouriteFragment()
                    supportFragmentManager.beginTransaction()
                        .replace(R.id.frm_contain, favouritesFragment)
                        .commit()
                    // Respond to navigation item 2 click
                    true
                }
                else -> false
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }
}