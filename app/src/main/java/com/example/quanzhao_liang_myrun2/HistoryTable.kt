package com.example.quanzhao_liang_myrun2

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.android.gms.maps.model.LatLng

@Entity(tableName = "history_table")
data class HistoryTable (
    @PrimaryKey(autoGenerate = true)
    var id: Long = 0L,

    @ColumnInfo(name = "input_type_column")
    var input: Int = 0,

    @ColumnInfo(name = "activity_column")
    var activity: String = "",

    @ColumnInfo(name = "date_column")
    var date: String = "",

    @ColumnInfo(name = "time_column")
    var time: String = "",

    @ColumnInfo(name = "duration_column")
    var duration: Double = 0.0,

    @ColumnInfo(name = "distance_column")
    var distance: Double = 0.0,

    @ColumnInfo(name = "average_speed_column")
    var avgSpeed: Double = 0.0,

    @ColumnInfo(name = "climb_column")
    var climb: Double = 0.0,

    @ColumnInfo(name = "unit_column")
    var distanceUnit: String = "Kilometers",

    @ColumnInfo(name = "calorie_column")
    var calorie: Double = 0.0,

    @ColumnInfo(name = "heart_rate_column")
    var heartRate: Double = 0.0,

    @ColumnInfo(name = "comment_column")
    var comment: String = "",

    @ColumnInfo(name = "lagLng_column")
    var location: ArrayList<LatLng>? = null
)