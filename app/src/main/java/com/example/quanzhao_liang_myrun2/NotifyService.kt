package com.example.quanzhao_liang_myrun2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat

class NotifyService: Service(), LocationListener{
    private val channelId = "notification channel"
    private val PENDINGINTENT_REQUEST_CODE = 0
    private val NOTIFY_ID = 11
    private lateinit var notificationManager: NotificationManager
    private lateinit var myBroadcastReceiver: MyBroadcastReceiver

    private lateinit var locationManager: LocationManager

    private val mFeatLen = Globals.ACCELEROMETER_BLOCK_CAPACITY + 2
    companion object{
        val STOP_SERVICE_ACTION = "stop service action"
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1 // 1 minute
    }
    override fun onCreate() {
        super.onCreate()
        println("debug: onCreate called")
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()
        myBroadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(STOP_SERVICE_ACTION)
        registerReceiver(myBroadcastReceiver, intentFilter)

    }


    override fun onLocationChanged(location: Location) {
        val speed = location.speed // speed in m/s
        // Calculate distance moved and update UI or store in database
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("debug: onStartCommand called")
        return START_NOT_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        println("debug: onDestroy called")
    }

    fun showNotification(){
        val mapIntent = Intent(this, MapDisplayActivity::class.java)

        val pendingIntent = PendingIntent.getActivity(
            this, PENDINGINTENT_REQUEST_CODE,
            mapIntent, PendingIntent.FLAG_IMMUTABLE
        )

        val notificationBuilder: NotificationCompat.Builder = NotificationCompat.Builder(
            this, channelId
        )
        notificationBuilder.setContentTitle("MyRun")
        notificationBuilder.setContentText("Your path is being recorded")
        notificationBuilder.setContentIntent(pendingIntent)
        notificationBuilder.setSmallIcon(R.drawable.baseline_run_circle_24)
        val notification = notificationBuilder.build()


        if (Build.VERSION.SDK_INT > 26) {
            val notificationChannel = NotificationChannel(channelId, "channel name", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(notificationChannel)
        }
        notificationManager.notify(NOTIFY_ID,notification)
    }

    inner class MyBroadcastReceiver: BroadcastReceiver(){
        override fun onReceive(context: Context?, intent: Intent?) {
            stopSelf()
            notificationManager.cancel(NOTIFY_ID)
            unregisterReceiver(myBroadcastReceiver)
        }

    }
    override fun onBind(intent: Intent?): IBinder? {
        return null
    }



}