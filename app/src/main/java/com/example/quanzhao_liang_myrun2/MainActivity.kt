package com.example.quanzhao_liang_myrun2

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationManagerCompat
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


        checkNotificationEnabled()

        tabLayoutMediator = TabLayoutMediator(tabLayout, viewPager2, tabConfigurationStrategy)
        tabLayoutMediator.attach()
    }

    private fun checkNotificationEnabled() {
        if (!NotificationManagerCompat.from(this).areNotificationsEnabled()) {
            // Notifications are not enabled
            showNotificationEnableDialog()
        }
    }

    private fun showNotificationEnableDialog() {
        // Show an explanation dialog, and upon user agreement, proceed to settings
        AlertDialog.Builder(this)
            .setTitle("Enable Notifications")
            .setMessage("To stay updated, please enable notifications for our app.")
            .setPositiveButton("Enable") { _, _ ->
                redirectToSettings()
            }
            .setNegativeButton("Cancel"){
                _,_-> finish()
            }
            .show()
    }

    private fun redirectToSettings() {
        val intent = Intent().apply {
            action = Settings.ACTION_APP_NOTIFICATION_SETTINGS
            putExtra(Settings.EXTRA_APP_PACKAGE, packageName)
        }
        startActivity(intent)
    }

    override fun onResume() {
        super.onResume()
        checkNotificationEnabled()
    }
}