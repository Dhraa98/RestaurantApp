package com.zomato

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager.widget.ViewPager
import androidx.viewpager.widget.ViewPager.OnPageChangeListener
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.zomato.databinding.ActivityMainBinding
import com.zomato.view.fragment.FavouriteFragment
import com.zomato.view.fragment.SearchFragment
import com.zomato.view.fragment.adapter.MainPagerAdapter


class MainActivity : AppCompatActivity() {
    private lateinit var bindig: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        bindig = DataBindingUtil.setContentView(this, R.layout.activity_main)
        initControls()
    }

    private fun initControls() {
        bindig.lifecycleOwner = this

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
//                if (prevMenuItem != null) prevMenuItem.setChecked(false) else bindig.navigation.getMenu()
//                    .getItem(0).setChecked(false)
                bindig.navigation.getMenu().getItem(position).setChecked(true)
//                prevMenuItem = bindig.navigation.getMenu().getItem(position)
            }

            override fun onPageScrollStateChanged(state: Int) {}
        })
      /*  var searchFragment: SearchFragment = SearchFragment()
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
        }*/
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