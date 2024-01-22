package com.example.quanzhao_liang_myrun2

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider

class EntryDisplayActivity : AppCompatActivity() {

    private var id:Long = 0
    private lateinit var inputType:String
    private lateinit var activity:String
    private lateinit var date:String
    private lateinit var time:String
    private var duration:Double = 0.0
    private var distance:Double = 0.0
    private var calories:Double = 0.0
    private var heartRate:Double = 0.0
    private lateinit var distanceUnit:String

    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModel.HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    private lateinit var arrayList: ArrayList<HistoryTable>
    private lateinit var arrayAdapter: MyListAdapter


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_entry_display)


        id = intent.getLongExtra("ID", 0)
        println(id)
        val inputTypeInt = intent.getIntExtra("InputType", 1)
        if (inputTypeInt == 1){
            inputType = "Manual Entry"
        }
        else if (inputTypeInt == 2){
            inputType = "GPS"
        }
        else{
            inputType = "Automatic"
        }
        activity = intent.getStringExtra("Activity").toString()
        date =intent.getStringExtra("Date").toString()
        time = intent.getStringExtra("Time").toString()
        duration = intent.getDoubleExtra("Duration", 0.0)
        distance = intent.getDoubleExtra("Distance", 0.0)
        calories = intent.getDoubleExtra("Calories", 0.0)
        heartRate = intent.getDoubleExtra("HeartRate", 0.0)
        distanceUnit = intent.getStringExtra("Unit").toString()

        val inputTypeEt = findViewById<EditText>(R.id.input_type_edit_text)
        inputTypeEt.setText(inputType)

        val activityEt = findViewById<EditText>(R.id.activity_type_edit_text)
        activityEt.setText(activity)

        val dateNtimeEt = findViewById<EditText>(R.id.date_edit_text)
        val dateNtime = "$date $time"
        dateNtimeEt.setText(dateNtime)

        val durationEt = findViewById<EditText>(R.id.duration_edit_text)
        durationEt.setText(duration.toString())

        val distanceEt = findViewById<EditText>(R.id.duration_edit_text)
        distanceEt.setText("$distance $distanceUnit")

        val caloriesEt = findViewById<EditText>(R.id.calories_edit_text)
        caloriesEt.setText(calories.toString())

        val heartRateEt = findViewById<EditText>(R.id.heart_rate_edit_text)
        heartRateEt.setText(heartRate.toString())

        val deleteBtn = findViewById<Button>(R.id.delete_btn)

        database = HistoryDatabase.getInstance(this)
        databaseDao = database.historyDatabaseDao
        repository = HistoryRepository(databaseDao)
        viewModelFactory = HistoryViewModel.HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this, viewModelFactory).get(HistoryViewModel::class.java)
        arrayList = ArrayList()
        arrayAdapter = MyListAdapter(this, arrayList, historyViewModel)

        historyViewModel.allHistoryLiveData.observe(this, Observer {
            arrayAdapter.replace(it)
            arrayAdapter.notifyDataSetChanged()
        })

        deleteBtn.setOnClickListener{
            historyViewModel.delete(id)
            finish()
        }

    }
}