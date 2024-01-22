package com.example.quanzhao_liang_myrun2

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.preference.PreferenceManager
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.Polyline
import com.google.android.gms.maps.model.PolylineOptions
import java.text.DecimalFormat
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class MapDisplayActivity : AppCompatActivity(), OnMapReadyCallback, LocationListener{
    lateinit var mapSaveBtn: Button
    lateinit var mapCancelBtn: Button

    private lateinit var mMap: GoogleMap
    private lateinit var polyline: Polyline
    private val PERMISSION_REQUEST_CODE = 0
    private lateinit var locationManager: LocationManager
    private lateinit var locationListener: LocationListener
    private var mapCentered = false
    private lateinit var  markerOptions: MarkerOptions
    private lateinit var  polylineOptions: PolylineOptions
    private lateinit var pathList: ArrayList<Polyline>
    private lateinit var lastLocationMarker: Marker
    private var lastLocation: Location? = null
    private var starterFlag = true


    private lateinit var curSpeedView: TextView
    private lateinit var distanceView: TextView
    private lateinit var avgSpeedView: TextView
    private lateinit var climbView: TextView
    private lateinit var activityView: TextView
    private lateinit var caloriesView: TextView

    private lateinit var locationList: ArrayList<LatLng>
    private lateinit var inputType:String
    private lateinit var date: String
    private lateinit var time: String
    private lateinit var distance: String
    private lateinit var curSpeed: String
    private lateinit var avgSpeed: String
    private var totalSpeedSum: Float = 0f
    private var speedReadingsCount: Int = 0
    private var distanceUnit: String = "Kilometers"
    private var totalDistance: Double = 0.0
    private var convertedDistance = 0.0

    private var lastAltitude: Double ?= null
    private var totalClimb: Double = 0.0
    private lateinit var activityType: String

    private var calories:Double = 0.0
    private var duration: Double = 0.0

    private val history = HistoryTable()
    private lateinit var database: HistoryDatabase
    private lateinit var databaseDao: DatabaseDao
    private lateinit var repository: HistoryRepository
    private lateinit var viewModelFactory: HistoryViewModel.HistoryViewModelFactory
    private lateinit var historyViewModel: HistoryViewModel

    private lateinit var myViewModel: MyViewModel


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map)
                as SupportMapFragment
        mapFragment.getMapAsync(this)

        val startTime = System.currentTimeMillis() / 1000
        locationList = ArrayList<LatLng>()

        database = HistoryDatabase.getInstance(this)
        databaseDao = database.historyDatabaseDao
        repository = HistoryRepository(databaseDao)
        viewModelFactory = HistoryViewModel.HistoryViewModelFactory(repository)
        historyViewModel = ViewModelProvider(this, viewModelFactory).get(HistoryViewModel::class.java)

        mapSaveBtn = findViewById(R.id.mapSaveBtn)
        mapCancelBtn = findViewById(R.id.mapCancelBtn)
        curSpeedView = findViewById(R.id.map_current_speed)
        distanceView = findViewById(R.id.map_distance)
        avgSpeedView = findViewById(R.id.map_avg_speed)
        climbView = findViewById(R.id.map_climb)
        activityView = findViewById(R.id.map_activity_type)
        caloriesView = findViewById(R.id.map_calories)

        activityType = intent.getStringExtra("ActivityType").toString()


        inputType = intent.getStringExtra("InputType").toString()
        if (inputType == "Automatic"){
            SharedRepository.data.observe(this, Observer{
                data->
                activityView.text = "Type: $data"
                activityType = data
            })
        }
        //Add activity type to text view
        if (inputType == "GPS"){
            activityView.text = "Type: $activityType"
        }
        //get current date and time
        val currentDate = LocalDate.now()
        val formatterDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
        date = currentDate.format(formatterDate)
        val currentTime = LocalTime.now()
        val formatterTime = DateTimeFormatter.ofPattern("HH:mm")
        time = currentTime.format(formatterTime)


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

        val intent = Intent()
        intent.action = NotifyService.STOP_SERVICE_ACTION

        mapSaveBtn.setOnClickListener{
            sendBroadcast(intent)
            val endTime = System.currentTimeMillis() /1000
            duration = (endTime - startTime).toDouble()
            locationManager.removeUpdates(this)
            if (inputType == "GPS"){
                history.input = 2
            }
            else{
                history.input = 3
            }
            history.activity = activityType
            history.date = date
            history.time = time
            history.distance = distance.toDouble()
            history.calorie = calories
            history.avgSpeed = avgSpeed.toDouble()
            history.location = locationList
            history.duration = duration
            historyViewModel.insert(history)

            val intent = Intent(this, MainActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // Optionally, add data to the Intent
            intent.putExtra("key", "value")

            // Start the same activity
            startActivity(intent)
            finish()
        }
        mapCancelBtn.setOnClickListener{
            sendBroadcast(intent)
            locationManager.removeUpdates(this)
            finish()
        }
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        mMap.mapType = GoogleMap.MAP_TYPE_NORMAL
        polylineOptions = PolylineOptions()
        polylineOptions.width(5f).color(Color.RED)
        markerOptions = MarkerOptions()
        checkPermission()
    }

    override fun onDestroy() {
        super.onDestroy()
        if (locationManager != null)
            locationManager.removeUpdates(this)
    }

    fun initLocationManager() {
        try {
            locationManager = getSystemService(LOCATION_SERVICE) as LocationManager

            if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) return

            val location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER)
            if (location != null)
                onLocationChanged(location)

            //minDistanceM: predict the position
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0f, this)

        } catch (e: SecurityException) {
        }
    }

    override fun onLocationChanged(location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        //println(latLng)
        locationList.add(latLng)
        //println(locationList)
        // Add a point to the polyline
        polylineOptions.add(latLng)
        polyline = mMap.addPolyline(polylineOptions)

        // Move the last location marker
        if (::lastLocationMarker.isInitialized) {
            lastLocationMarker.position = latLng
        } else {
            // This is the first time, so we initialize the marker
            lastLocationMarker = mMap.addMarker(
                MarkerOptions().position(latLng).icon(
                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                )
            )!!
        }

        //Convert the result to 2 decimal places
        val formatter = DecimalFormat("#.##")
        formatter.roundingMode = java.math.RoundingMode.HALF_UP



        //Calculate the total distance moved
        lastLocation?.let { lastLocation ->
            val distance = lastLocation.distanceTo(location)
            totalDistance += distance
        }
        lastLocation = location

        //Calculate the Altitude change
        //add the Altitude Change to Text View
        val currentAltitude = location.altitude
        lastAltitude?.let{lastAltitude->
            val altitudeChange = currentAltitude - lastAltitude
            if (altitudeChange > 0){
                totalClimb += altitudeChange
            }
        }
        lastAltitude = currentAltitude
        val formattedClimb = formatter.format(totalClimb)
        val totalClimbString = formattedClimb.toString()
        climbView.text = "Climb: $totalClimbString m"

        //Convert the total distance from meters to kms or miles
        //Add the on changing distance to the text view
        if (distanceUnit == "Kilometers"){
            convertedDistance = totalDistance / 1000
        }
        else{
            convertedDistance = totalDistance / 1609
        }

        val formattedDis = formatter.format(convertedDistance)
        distance = formattedDis.toString()
        distanceView.text = "Distance: $distance $distanceUnit"

        //Add the on changing speed to the text view
        val speed = location.speedAccuracyMetersPerSecond
        val formattedSpeed = formatter.format(speed)
        curSpeed = formattedSpeed.toString()
        curSpeedView.text = "Current Speed: $curSpeed m/s"

        //Calculate the average speed
        //Add the on changing average speed to the text view
        speedReadingsCount++
        totalSpeedSum += speed
        val averageSpeed = if (speedReadingsCount > 0) totalSpeedSum / speedReadingsCount else 0f
        val formattedAvgSpeed = formatter.format(averageSpeed)
        avgSpeed = formattedAvgSpeed.toString()
        avgSpeedView.text = "Average Speed: $avgSpeed m/s"

        //Calculate the calories
        //Add calories burn to the text view
        calories += caloriesCalculate(convertedDistance, activityType, distanceUnit)
        val formattedCalories = formatter.format(calories)
        calories = formattedCalories.toDouble()
        caloriesView.text = "Calories: $formattedCalories"


        if (!mapCentered) {
            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17f))
            markerOptions.position(latLng)
            mMap.addMarker(markerOptions)
            mapCentered = true
        }
    }

    fun checkPermission() {
        if (Build.VERSION.SDK_INT < 23) return
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), PERMISSION_REQUEST_CODE)
        else
            initLocationManager()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                initLocationManager()
        }
    }

    private fun caloriesCalculate(distance: Double, activityType: String, distanceUnit: String): Double {
        val calPerKm = when(activityType){
            "Walking" -> 0.5
            "Running" -> 1.0
            "Standing" -> 0.05
            else -> 1.5
        }
        if (distanceUnit == "Miles"){
            return distance*1.6214 * 55 * calPerKm / 10
        }
        return distance * 55 * calPerKm / 10
    }
}