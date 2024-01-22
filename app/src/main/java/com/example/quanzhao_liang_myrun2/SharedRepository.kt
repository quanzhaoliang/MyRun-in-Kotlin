package com.example.quanzhao_liang_myrun2

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

object SharedRepository {
    private val _data = MutableLiveData<String>()
    val data: LiveData<String> = _data

    fun updateData(newData: String) {
        _data.postValue(newData)
    }
}