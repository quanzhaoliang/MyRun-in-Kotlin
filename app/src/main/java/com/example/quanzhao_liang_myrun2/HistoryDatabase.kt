package com.example.quanzhao_liang_myrun2

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = [HistoryTable::class], version = 1)
@TypeConverters(Converters::class)
abstract class HistoryDatabase: RoomDatabase() {
    abstract val historyDatabaseDao: DatabaseDao

    companion object{
        @Volatile
        private var INSTANCE:HistoryDatabase? = null

        fun getInstance(context: Context): HistoryDatabase {
            synchronized(this){
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        HistoryDatabase::class.java, "history_table"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}