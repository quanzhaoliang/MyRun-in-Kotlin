package com.example.quanzhao_liang_myrun2

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapHistoryActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var activity: String
    private var id: Long = 0
    private var position: Int = 0
    private var duration: Double = 0.0
    private var distance: Double = 0.0
    private var avgSpeed: Double = 0.0
    private var climb: Double = 0.0
    private var calories: Double = 0.0
    private var unit: String = "Kilometers"
    private var location: ArrayList<LatLng>? = null

    private lateinit var curSpeedView: TextView
    private lateinit var distanceView: TextView
    private lateinit var avgSpeedView: TextView
    private lateinit var climbView: TextView
    private lateinit var activityView: TextView
    private lateinit var caloriesView: TextView

    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModel.HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    private lateinit var arrayList: ArrayList<HistoryTable>
    private lateinit var arrayAdapter: MyListAdapter
    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_history)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map_history)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        curSpeedView = findViewById(R.id.map_his_current_speed)
        avgSpeedView = findViewById(R.id.map_his_avg_speed)
        climbView = findViewById(R.id.map_his_climb)
        distanceView = findViewById(R.id.map_his_distance)
        activityView = findViewById(R.id.map_his_activity_type)
        caloriesView = findViewById(R.id.map_his_calories)

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

        id = intent.getLongExtra("ID", 0)
        position = intent.getIntExtra("Position", 0)
        activity = intent.getStringExtra("Activity").toString()
        duration = intent.getDoubleExtra("Duration", 0.0)
        distance = intent.getDoubleExtra("Distance", 0.0)
        avgSpeed = intent.getDoubleExtra("AvgSpeed", 0.0)
        climb = intent.getDoubleExtra("Climb", 0.0)
        calories = intent.getDoubleExtra("Calories", 0.0)
        location = intent.getParcelableArrayListExtra("Location")
        unit = intent.getStringExtra("Unit").toString()

        curSpeedView.text = "Current Speed: N/A"
        avgSpeedView.text = "Average Speed: $avgSpeed"
        climbView.text = "Climb: $climb"
        distanceView.text = "Distance: $distance $unit"
        activityView.text = "Type: $activity"
        caloriesView.text = "Calories: $calories"

        val deleteBtn = findViewById<Button>(R.id.delete_map_btn)
        deleteBtn.setOnClickListener{
            historyViewModel.delete(id)
            finish()
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL

        val polylineOptions = location?.let { PolylineOptions().addAll(it) }
        if (polylineOptions != null) {
            mMap.addPolyline(polylineOptions)
            polylineOptions.color(Color.RED).width(8f)
        }
        location?.let { setupMapAndMarkers(mMap, it) }
    }

    fun setupMapAndMarkers(googleMap: GoogleMap, polylinePoints: ArrayList<LatLng>) {
        // Calculate bounds
        val builder = LatLngBounds.Builder()
        for (point in polylinePoints) {
            builder.include(point)
        }
        val bounds = builder.build()

        // Move camera
        val padding = 400 // offset from edges of the map in pixels
        val cameraUpdate = CameraUpdateFactory.newLatLngBounds(bounds, padding)
        googleMap.animateCamera(cameraUpdate)

        // Add markers
        val startPoint = polylinePoints.first()
        googleMap.addMarker(
            MarkerOptions().position(startPoint).title("Start").icon(
            BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)))

        val endPoint = polylinePoints.last()
        googleMap.addMarker(
            MarkerOptions().position(endPoint).title("End").icon(
                BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)))
    }
}