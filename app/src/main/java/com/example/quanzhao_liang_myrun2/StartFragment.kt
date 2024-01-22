package com.example.quanzhao_liang_myrun2

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import androidx.fragment.app.Fragment


class StartFragment : Fragment() {
    private lateinit var spinner1: Spinner
    private lateinit var spinner2: Spinner
    private lateinit var startBtn: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_start, container, false)
        // Inflate the layout for this fragment
        spinner1 = view.findViewById(R.id.spinner1)
        val input = resources.getStringArray(R.array.inputType)
        val adapter1 = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            input
            )
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner1.adapter = adapter1


        spinner2 = view.findViewById(R.id.spinner2)
        val activity = resources.getStringArray(R.array.activityType)
        val adapter2 = ArrayAdapter(requireContext(),
            android.R.layout.simple_spinner_item,
            activity)
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner2.adapter = adapter2

        startBtn = view.findViewById(R.id.startBtn)
        startBtn.setOnClickListener{
            val selectedInputType = spinner1.selectedItem.toString()
            val selectedActivityType = spinner2.selectedItem.toString()

            when (selectedInputType){
                "Manual Entry" ->{
                    val intent = Intent(requireContext(), ManualInputActivity::class.java)
                    intent.putExtra("ActivityType", selectedActivityType)
                    startActivity(intent)
                }
                "GPS" -> {
                    val intent = Intent(requireContext(), MapDisplayActivity::class.java)
                    intent.putExtra("ActivityType", selectedActivityType)
                    intent.putExtra("InputType", selectedInputType)
                    val notifyIntent = Intent(requireContext(), NotifyService::class.java)
                    requireContext().startService(notifyIntent)
                    startActivity(intent)
                }
                "Automatic"->{
                    val intent = Intent(requireContext(), MapDisplayActivity::class.java)
                    intent.putExtra("ActivityType", selectedActivityType)
                    intent.putExtra("InputType", selectedInputType)
                    val notifyIntent = Intent(requireContext(), NotifyService::class.java)
                    notifyIntent.putExtra("InputType", selectedInputType)
                    requireContext().startService(notifyIntent)
                    startActivity(intent)
                }
            }
        }
        return view
    }
}