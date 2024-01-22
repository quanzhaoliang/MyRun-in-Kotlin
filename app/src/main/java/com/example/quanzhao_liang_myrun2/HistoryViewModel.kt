package com.example.quanzhao_liang_myrun2

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.asLiveData
import java.io.Serializable

class HistoryViewModel(private val repository: HistoryRepository): ViewModel(), Serializable{

    val allHistoryLiveData: LiveData<List<HistoryTable>> = repository.allHistory.asLiveData()

    fun insert(historyTable: HistoryTable){
        repository.insert(historyTable)
    }


    fun delete(id: Long){
        val historyList = allHistoryLiveData.value
        if (!historyList.isNullOrEmpty()){
            repository.delete(id)
        }
    }

    fun deleteAll(){
        val historyList = allHistoryLiveData.value
        if (!historyList.isNullOrEmpty()){
            repository.deleteAll()
        }
    }

    class HistoryViewModelFactory(private val repository: HistoryRepository): ViewModelProvider.Factory{
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(HistoryViewModel::class.java))
                return HistoryViewModel(repository) as T
            throw IllegalArgumentException("Unknown ViewModel Class")
        }
    }
}