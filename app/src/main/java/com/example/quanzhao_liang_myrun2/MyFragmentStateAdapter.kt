package com.example.quanzhao_liang_myrun2

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import kotlin.collections.ArrayList

class MyFragmentStateAdapter(activity: MainActivity, var list: ArrayList<Fragment>)
    : FragmentStateAdapter(activity){

    override fun createFragment(position: Int): Fragment {
        return list[position]
    }

    override fun getItemCount(): Int {
        return list.size
    }


}