package com.example.quanzhao_liang_myrun2

import android.location.GnssAntennaInfo.Listener
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.Spinner
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

//        val historyFragment =HistoryFragment()
//        val onTabSelectedListener = object : TabLayout.OnTabSelectedListener {
//            override fun onTabSelected(tab: TabLayout.Tab) {
//                // Tab selected, perform your action here
//                val position = tab.position
//                if (position == 1){
//                    println("OKKKKK")
//                    supportFragmentManager.beginTransaction()
//                        .detach(HistoryFragment())
//                        .attach(HistoryFragment())
//                        .commit()
//                }
//                // Do something with the selected tab
//            }
//
//            override fun onTabUnselected(tab: TabLayout.Tab) {
//                // Tab unselected
//            }
//
//            override fun onTabReselected(tab: TabLayout.Tab) {
//                // Tab reselected
//            }
//        }


//        tabLayout.addOnTabSelectedListener(onTabSelectedListener)
    }

}