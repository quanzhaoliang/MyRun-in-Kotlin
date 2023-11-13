package com.example.quanzhao_liang_myrun2

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import java.util.Calendar

class ManualInputActivity : AppCompatActivity() {
    private val startSettings = arrayOf(
        "Date",
        "Time",
        "Duration",
        "Distance",
        "Calories",
        "Heart Rate",
        "Comment"
    )

    private lateinit var listView: ListView
    private lateinit var saveBtn: Button
    private lateinit var cancelBtn: Button

    private val history = HistoryTable()
    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModel.HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    private var input: Int = 1
    private var activity: String = ""
    private var date: String = ""
    private var time: String = ""
    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var distanceUnit: String = "Kilometers"
    private var heartRate: Double = 0.0
    private var calorie: Double = 0.0
    private var comment: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manual_input)
        activity = intent.getStringExtra("ActivityType").toString()
        listView = findViewById(R.id.listView)
        val arrayAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, startSettings)
        listView.adapter = arrayAdapter
        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedItem = startSettings[position]
            for (item in startSettings) {
                if (selectedItem == item) {
                    if (selectedItem == "Date"){
                        showDatePickerDialog()
                        break
                    }
                    if (selectedItem == "Time"){
                        showTimePickerDialog()
                        break
                    }
                    showTextEntryDialog(item)
                    break
                }
            }
        }

        database = HistoryDatabase.getInstance(this)
        databaseDao = database.historyDatabaseDao
        repository = HistoryRepository(databaseDao)
        viewModelFactory = HistoryViewModel.HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this, viewModelFactory).get(HistoryViewModel::class.java)

        //Get the unit selected by the user
        val sharedPref = PreferenceManager.getDefaultSharedPreferences(this)
        val defaultValue = "-1" // Assign some meaningful default value
        val unitSelected = sharedPref.getString("unit", defaultValue)
        if (unitSelected == "metric"){
            distanceUnit = "Kilometers"
        }
        else{
            distanceUnit = "Miles"
        }


        val historyFragment = HistoryFragment()
        //Set the Save & Cancel Buttons
        saveBtn = findViewById(R.id.saveBtn)
        cancelBtn = findViewById(R.id.cancelBtn)
        saveBtn.setOnClickListener{

            history.input = input
            history.activity = activity
            history.date = date
            history.time = time
            history.duration = duration
            history.distance = distance
            history.distanceUnit = distanceUnit
            history.calorie = calorie
            history.heartRate = heartRate
            history.comment = comment
            historyViewModel.insert(history)
            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Optionally, add data to the Intent
            intent.putExtra("key", "value")

            // Start the same activity
            startActivity(intent)

            // Finish the current instance of the activity (optional)

            finish()

        }
        cancelBtn.setOnClickListener{
            finish()
        }
    }

    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Handle the selected date (e.g., update a TextView with the selected date)
                val selectedDate = "$selectedYear-${selectedMonth + 1}-$selectedDay"
                date = selectedDate
                // You can update a TextView or perform any other action with the selected date here
            },
            year, month, day
        )

        // Show the date picker dialog
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(
            this,
            { _, selectedHour, selectedMinute ->
                // Handle the selected time (e.g., update a TextView with the selected time)
                val selectedTime = "$selectedHour:$selectedMinute"
                time = selectedTime
                // You can update a TextView or perform any other action with the selected time here
            },
            hour, minute, true // true for 24-hour format, false for 12-hour format with AM/PM
        )

        // Show the time picker dialog
        timePickerDialog.show()
    }

    private fun showTextEntryDialog(title: String) {
        val editText = EditText(this)
        if (title == "Comment"){
            editText.hint = "How did it go? Notes here."
        }


        val alertDialog = AlertDialog.Builder(this)
            .setTitle(title)
            .setView(editText)
            .setPositiveButton("OK") { _, _ ->
                // Handle the entered text here
                val enteredText = editText.text.toString()
                if (title == "Duration"){
                    duration = enteredText.toDouble()
                }
                if (title == "Distance"){
                    distance = enteredText.toDouble()
                }
                if (title == "Heart Rate"){
                    heartRate = enteredText.toDouble()
                }
                if (title == "Calories"){
                    calorie = enteredText.toDouble()
                }
                if (title == "Comment"){
                    comment = enteredText
                }
                // You can do something with the entered text (e.g., display it or store it)
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        alertDialog.show()
    }
}