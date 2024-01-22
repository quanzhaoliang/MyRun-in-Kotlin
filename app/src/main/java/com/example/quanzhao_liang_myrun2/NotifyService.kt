package com.example.quanzhao_liang_myrun2

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.AsyncTask
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import java.util.concurrent.ArrayBlockingQueue

class NotifyService: Service(), SensorEventListener{
    private val channelId = "notification channel"
    private val PENDINGINTENT_REQUEST_CODE = 0
    private val NOTIFY_ID = 11
    private lateinit var notificationManager: NotificationManager
    private lateinit var myBroadcastReceiver: MyBroadcastReceiver
    private lateinit var mSensorManager: SensorManager
    private lateinit var mAccelerometer: Sensor

    private var x: Double = 0.0
    private var y: Double = 0.0
    private var z: Double = 0.0

    private lateinit var inputType:String
    private var activityType = "Other";
    private lateinit var mAccBuffer: ArrayBlockingQueue<Double>
    private lateinit var mAsyncTask: OnSensorChangedTask
    private lateinit var displayIntent: Intent




    companion object{
        val STOP_SERVICE_ACTION = "stop service action"
        private const val MIN_DISTANCE_CHANGE_FOR_UPDATES: Float = 10f // 10 meters
        private const val MIN_TIME_BW_UPDATES: Long = 1000 * 60 * 1 // 1 minute
    }
    override fun onCreate() {
        super.onCreate()
        println("debug: onCreate called")
        mAccBuffer = ArrayBlockingQueue<Double>(Globals.ACCELEROMETER_BUFFER_CAPACITY)


        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        showNotification()
        myBroadcastReceiver = MyBroadcastReceiver()
        val intentFilter = IntentFilter()
        intentFilter.addAction(STOP_SERVICE_ACTION)
        registerReceiver(myBroadcastReceiver, intentFilter)
    }


    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        println("debug: onStartCommand called")
        inputType = intent.getStringExtra("InputType").toString()
        println("$inputType")

        displayIntent = Intent(this, MapDisplayActivity::class.java)

        mSensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_LINEAR_ACCELERATION)!!
        mSensorManager.registerListener(this, mAccelerometer, SensorManager.SENSOR_DELAY_FASTEST)
        mAsyncTask = OnSensorChangedTask()
        mAsyncTask.execute()
        return START_NOT_STICKY
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


    override fun onSensorChanged(event: SensorEvent) {
        //println("On Sensor Changed Start!")
        if (event.sensor.type == Sensor.TYPE_LINEAR_ACCELERATION){
            x = (event.values[0] / SensorManager.GRAVITY_EARTH).toDouble()
            y = (event.values[1] / SensorManager.GRAVITY_EARTH).toDouble()
            z = (event.values[2] / SensorManager.GRAVITY_EARTH).toDouble()
            val magnitude = Math.sqrt(x * x + y * y + z * z)
            // Inserts the specified element into this queue if it is possible
            // to do so immediately without violating capacity restrictions,
            // returning true upon success and throwing an IllegalStateException
            // if no space is currently available. When using a
            // capacity-restricted queue, it is generally preferable to use
            // offer.
            try {
                mAccBuffer.add(magnitude)
            } catch (e: IllegalStateException) {

                // Exception happens when reach the capacity.
                // Doubling the buffer. ListBlockingQueue has no such issue,
                // But generally has worse performance
                val newBuf = ArrayBlockingQueue<Double>(mAccBuffer.size * 2)
                mAccBuffer.drainTo(newBuf)
                mAccBuffer = newBuf
                mAccBuffer.add(magnitude)
            }
        }
    }

    override fun onDestroy() {
        mAsyncTask.cancel(true)
        try {
            Thread.sleep(100)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }
        mSensorManager.unregisterListener(this)
        super.onDestroy()
    }

    inner class OnSensorChangedTask : AsyncTask<Void, Void, Void>() {
        override fun doInBackground(vararg arg0: Void?): Void? {
            println("DEBUG: doInBackground")
            val vector = ArrayList<Double>(Globals.ACCELEROMETER_BLOCK_CAPACITY+1)
            var blockSize = 0
            val fft = FFT(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val accBlock = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            val im = DoubleArray(Globals.ACCELEROMETER_BLOCK_CAPACITY)
            var max = Double.MIN_VALUE

            while (true){
                try {
                    if (isCancelled() == true) {
                        return null
                    }

                    // Dumping buffer
                    accBlock[blockSize++] = mAccBuffer.take().toDouble()
                    if (blockSize == Globals.ACCELEROMETER_BLOCK_CAPACITY) {
                        blockSize = 0

                        // time = System.currentTimeMillis();
                        max = .0
                        for (`val` in accBlock) {
                            if (max < `val`) {
                                max = `val`
                            }
                        }
                        fft.fft(accBlock, im)
                        for (i in accBlock.indices) {
                            val mag = Math.sqrt(accBlock[i] * accBlock[i] + im[i] * im[i])
                            vector.add(mag)
                            im[i] = .0 // Clear the field
                        }

                        // Append max after frequency component
                        vector.add(max)

                        val result = WekaClassifier
                            .classify(vector.toTypedArray()).toInt()

                        //Decide the activity type by the vector classifiedInference
                        when (result) {
                            0 -> activityType = "Standing"
                            1 -> activityType = "Walking"
                            2 -> activityType = "Running"
                        }
                        println("DEBUG: type is $activityType")
                        SharedRepository.updateData(activityType)

                        vector.clear()
                    }
                }catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            }
    }
    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {

    }

}