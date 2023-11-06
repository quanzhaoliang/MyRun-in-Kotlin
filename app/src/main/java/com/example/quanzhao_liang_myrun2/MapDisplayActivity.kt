package com.example.quanzhao_liang_myrun2

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MapDisplayActivity : AppCompatActivity() {
    lateinit var mapSaveBtn: Button
    lateinit var mapCancelBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_display)
        mapSaveBtn = findViewById(R.id.mapSaveBtn)
        mapCancelBtn = findViewById(R.id.mapCancelBtn)
        mapSaveBtn.setOnClickListener{
            finish()
        }
        mapCancelBtn.setOnClickListener{
            finish()
        }
    }
}