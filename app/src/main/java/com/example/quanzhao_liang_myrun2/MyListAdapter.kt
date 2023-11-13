package com.example.quanzhao_liang_myrun2

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView

class MyListAdapter(private val context: Context, private var historyList: List<HistoryTable>, private var historyViewModel: HistoryViewModel) : BaseAdapter(){

    override fun getItem(position: Int): Any {
        return historyList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getCount(): Int {
        return historyList.size
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View = View.inflate(context, R.layout.layout_adapter,null)

        val textViewID = view.findViewById(R.id.tv_number) as TextView
        val textViewInput = view.findViewById(R.id.tv_input_type) as TextView
        val textViewActivity = view.findViewById(R.id.tv_activity) as TextView
        val textViewDate = view.findViewById(R.id.tv_date) as TextView
        val textViewTime = view.findViewById(R.id.tv_time) as TextView
        val textViewDuration = view.findViewById(R.id.tv_duration) as TextView
        val textViewDistance = view.findViewById(R.id.tv_distance) as TextView
//        val textViewCalorie = view.findViewById(R.id.tv_calorie) as TextView
//        val textViewHeart = view.findViewById(R.id.tv_heartRate) as TextView
//        val textViewComment = view.findViewById(R.id.tv_comment) as TextView

//        val deleteButton: Button = view.findViewById(R.id.delete_btn)



        textViewID.text = "ID: " + historyList.get(position).id.toString()
        if (historyList.get(position).input == 1){
            textViewInput.text = "Input Type: " + "Manual Entry"
        }
        else if (historyList.get(position).input == 2){
            textViewInput.text = "Input Type: " + "GPS"
        }
        else{
            if (historyList.get(position).input == 1){
                textViewInput.text = "Input Type: " + "Automatics"
            }
        }
        textViewActivity.text = "Activity: " + historyList.get(position).activity
        textViewDate.text = "Date: " + historyList.get(position).date
        textViewTime.text = "Time: " + historyList.get(position).time
        textViewDuration.text = "Duration: " + historyList.get(position).duration + " secs"
        textViewDistance.text = "Distance: " + historyList.get(position).distance + " " + historyList.get(position).distanceUnit
//        textViewCalorie.text = "Calorie: " + historyList.get(position).calorie + " cals"
//        textViewHeart.text = "Heart Rate: " + historyList.get(position).heartRate + " bpm"
//        textViewComment.text = "Comment: " + historyList.get(position).comment

        return view
    }

    fun replace(newHistoryList: List<HistoryTable>){
        historyList = newHistoryList
    }

}