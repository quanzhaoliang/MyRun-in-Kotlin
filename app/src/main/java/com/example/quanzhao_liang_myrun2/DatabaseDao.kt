package com.example.quanzhao_liang_myrun2

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow


@Dao
interface DatabaseDao {

    @Insert
    suspend fun insertHistory(historyTable: HistoryTable)

    @Query("SELECT * FROM history_table")
    fun getAllHistory(): Flow<List<HistoryTable>>

    @Query("DELETE FROM history_table")
    suspend fun deleteAll()

    @Query("DELETE FROM history_table WHERE id = :key") //":" indicates that it is a Bind variable
    suspend fun deleteHistory(key: Long)
}