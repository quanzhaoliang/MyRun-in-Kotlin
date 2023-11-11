package com.example.quanzhao_liang_myrun2

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Button
import android.widget.ListView
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.loader.app.LoaderManager
import androidx.viewpager2.widget.ViewPager2

class HistoryFragment : Fragment() {

    private lateinit var myListView: ListView

    private lateinit var arrayList: ArrayList<HistoryTable>
    private lateinit var arrayAdapter: MyListAdapter

    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModel.HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel
    private lateinit var history: HistoryTable

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arrayList = ArrayList()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        val view = inflater.inflate(R.layout.fragment_history, container, false)
        // Inflate the layout for this fragment
        database = HistoryDatabase.getInstance(requireActivity())
        databaseDao = database.historyDatabaseDao
        repository = HistoryRepository(databaseDao)
        viewModelFactory = HistoryViewModel.HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(requireActivity(), viewModelFactory).get(HistoryViewModel::class.java)

        myListView = view.findViewById(R.id.history_list)
        arrayList = ArrayList()


        arrayAdapter = MyListAdapter(requireActivity(), arrayList, historyViewModel)

        myListView.adapter = arrayAdapter
        historyViewModel.allHistoryLiveData.observe(requireActivity(), Observer {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })

        myListView.setOnItemClickListener { parent, view, position, id ->
            val item = arrayAdapter.getItem(position) as HistoryTable
            println(item)
            val intent = Intent(requireContext(), EntryDisplayActivity::class.java)
            intent.putExtra("ID", item.id)
            intent.putExtra("Activity", item.activity)
            intent.putExtra("InputType", item.input)
            intent.putExtra("Date", item.date)
            intent.putExtra("Time", item.time)
            intent.putExtra("Duration", item.duration)
            intent.putExtra("Distance", item.distance)
            intent.putExtra("Calories", item.calorie)
            intent.putExtra("HeartRate", item.heartRate)
            startActivity(intent)
        }


        return view
    }
}