package com.example.quanzhao_liang_myrun2

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class HistoryRepository(private val historyDatabaseDao: DatabaseDao) {

    val allHistory: Flow<List<HistoryTable>> = historyDatabaseDao.getAllHistory()

    fun insert(historyTable: HistoryTable) {
        CoroutineScope(IO).launch {
            historyDatabaseDao.insertHistory(historyTable)
        }
    }

    fun delete(id: Long){
        CoroutineScope(IO).launch{
            historyDatabaseDao.deleteHistory(id)
        }
    }

    fun deleteAll(){
        CoroutineScope(IO).launch {
            historyDatabaseDao.deleteAll()
        }
    }
}