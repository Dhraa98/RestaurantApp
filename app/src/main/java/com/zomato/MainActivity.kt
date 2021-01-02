package com.zomato

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ktx.Firebase
import com.google.firebase.remoteconfig.FirebaseRemoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfig
import com.google.firebase.remoteconfig.ktx.remoteConfigSettings
import com.zomato.databinding.ActivityMainBinding
import com.zomato.view.fragment.FavouriteFragment
import com.zomato.view.fragment.SearchFragment
import com.zomato.view.fragment.adapter.MainPagerAdapter


class MainActivity : AppCompatActivity() {
    private val TAG = "MainActivity"
    private lateinit var bindig: ActivityMainBinding
    private lateinit var remoteConfig: FirebaseRemoteConfig
    private var COLOR_CONFIG_KEY = "Home"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindig = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initControls()
    }

    @SuppressLint("ResourceType")
    private fun initControls() {
        bindig.lifecycleOwner = this
        //Enable Debug mode for frequent fetches
        remoteConfig = Firebase.remoteConfig
        val configSettings = remoteConfigSettings {
            minimumFetchIntervalInSeconds = 3600
        }

        remoteConfig.setConfigSettingsAsync(configSettings)
        colorBackground = Color.parseColor(remoteConfig.getString(COLOR_CONFIG_KEY))
        bindig.navigation.setBackgroundColor(colorBackground)
        remoteConfig.fetchAndActivate()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val updated = task.result
                    remoteConfig.activate();
                    Log.d(TAG, "Config params updated: $updated")
                    Toast.makeText(
                        this, "Fetch and activate succeeded",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(
                        this, "Fetch failed",
                        Toast.LENGTH_SHORT
                    ).show()
                }




                Toast.makeText(this, remoteConfig.getString(COLOR_CONFIG_KEY), Toast.LENGTH_LONG)
                    .show()
                //remoteConfig.setDefaultsAsync(R.xml.remote_config_defaults)
            }

        setupViewPager(bindig.mainTabsViewPager)
        bindig.navigation.setOnNavigationItemSelectedListener(
            object : BottomNavigationView.OnNavigationItemSelectedListener {
                override fun onNavigationItemSelected(item: MenuItem): Boolean {
                    when (item.getItemId()) {
                        R.id.page_1 -> bindig.mainTabsViewPager.setCurrentItem(0)
                        R.id.page_2 -> bindig.mainTabsViewPager.setCurrentItem(1)

                    }
                    return false
                }
            })
        bindig.mainTabsViewPager.addOnPageChangeListener(object : OnPageChangeListener {
            override fun onPageScrolled(
                position: Int,
                positionOffset: Float,
                positionOffsetPixels: Int
            ) {
            }

            override fun onPageSelected(position: Int) {

                bindig.navigation.getMenu().getItem(position).setChecked(true)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })

    }

    companion object {
        var colorBackground: Int = 0
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun setupViewPager(viewPager: ViewPager) {

        val adapter = MainPagerAdapter(supportFragmentManager)

        if (SearchFragment().isAdded) {
            return
        } else {
            adapter.addFragment(SearchFragment(), resources.getString(R.string.search))
        }

        if (FavouriteFragment().isAdded) {
            return
        } else {
            adapter.addFragment(FavouriteFragment(), resources.getString(R.string.favorites))
        }


        viewPager.offscreenPageLimit = 3
//        viewPager.setPageTransformer(false,NoPageTransformer())
        viewPager.adapter = adapter

    }

}