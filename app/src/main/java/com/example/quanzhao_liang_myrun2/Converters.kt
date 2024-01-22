package com.example.quanzhao_liang_myrun2

import androidx.room.TypeConverter
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    @TypeConverter
    fun fromLatLngList(value: ArrayList<LatLng>?): String {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<LatLng>>() {}.type
        return gson.toJson(value, type)
    }

    @TypeConverter
    fun toLatLngList(value: String): ArrayList<LatLng>? {
        val gson = Gson()
        val type = object : TypeToken<ArrayList<LatLng>>() {}.type
        return gson.fromJson(value, type)
    }
}
