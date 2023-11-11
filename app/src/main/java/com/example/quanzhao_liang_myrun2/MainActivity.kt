package com.example.quanzhao_liang_myrun2

import android.Manifest
import android.content.pm.PackageManager
import android.location.GnssAntennaInfo.Listener
import android.location.LocationManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Spinner
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.google.android.material.tabs.TabLayoutMediator.TabConfigurationStrategy

class MainActivity : AppCompatActivity() {
    private lateinit var fragmentStart: StartFragment
    private lateinit var fragmentHistory: HistoryFragment
    private lateinit var fragmentSettings: SettingsFragment
    private lateinit var viewPager2: ViewPager2
    private lateinit var tabLayout: TabLayout
    private lateinit var myFragmentStateAdapter: MyFragmentStateAdapter
    private lateinit var fragmentArray: ArrayList<Fragment>

    private val tabTitles = arrayOf("Start", "History", "Settings")
    private lateinit var tabConfigurationStrategy: TabConfigurationStrategy
    private lateinit var tabLayoutMediator: TabLayoutMediator

    private val PERMISSION_REQUEST_CODE = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tabLayout = findViewById(R.id.tab)
        viewPager2 = findViewById(R.id.viewpager)

        fragmentStart = StartFragment()
        fragmentHistory = HistoryFragment()
        fragmentSettings = SettingsFragment()

        fragmentArray = ArrayList()
        fragmentArray.add(fragmentStart)
        fragmentArray.add(fragmentHistory)
        fragmentArray.add(fragmentSettings)

        //add the swap fragments
        myFragmentStateAdapter = MyFragmentStateAdapter(this, fragmentArray)
        viewPager2.adapter = myFragmentStateAdapter

        tabConfigurationStrategy = TabConfigurationStrategy{
            tab: TabLayout.Tab, position: Int->
            tab.text = tabTitles[position]
        }


        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }
}