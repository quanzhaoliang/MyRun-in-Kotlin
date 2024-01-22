package com.example.quanzhao_liang_myrun2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ListView
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class HistoryFragment : Fragment() {

    private var mapCentered = false

    private lateinit var myListView: ListView

    private lateinit var arrayList: ArrayList<HistoryTable>
    private lateinit var arrayAdapter: MyListAdapter

    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModel.HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

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

            if (item.input == 1){
                val intent = Intent(requireContext(), EntryDisplayActivity::class.java)
                intent.putExtra("Position", position)
                intent.putExtra("ID", item.id)
                intent.putExtra("Activity", item.activity)
                intent.putExtra("InputType", item.input)
                intent.putExtra("Date", item.date)
                intent.putExtra("Time", item.time)
                intent.putExtra("Duration", item.duration)
                intent.putExtra("Distance", item.distance)
                intent.putExtra("Calories", item.calorie)
                intent.putExtra("Unit", item.distanceUnit)
                intent.putExtra("HeartRate", item.heartRate)
                startActivity(intent)
            }
            else{
                val intent = Intent(requireContext(), MapHistoryActivity::class.java)
                intent.putExtra("ID", item.id)
                intent.putExtra("Activity", item.activity)
                intent.putExtra("Duration", item.duration)
                intent.putExtra("Distance", item.distance)
                intent.putExtra("AvgSpeed", item.avgSpeed)
                intent.putExtra("Climb", item.climb)
                intent.putExtra("Calories", item.calorie)
                intent.putExtra("Unit", item.distanceUnit)
                intent.putParcelableArrayListExtra("Location", item.location)
                startActivity(intent)
            }
        }
        return view
    }
}