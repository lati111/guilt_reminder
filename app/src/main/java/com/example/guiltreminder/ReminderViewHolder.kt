package com.example.guiltreminder

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val mDescriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val mRemindAfterTextView: TextView = itemView.findViewById(R.id.remindAfterTextView)

    // Show the data in the views
    fun bind(reminder: com.example.guiltreminder.database.Reminder) {
        val description = reminder.description;
        val remindAfter = reminder.remind_after;

        val currDate = Date();
        var remindAtString: String = "pain";

        val dayFormat: SimpleDateFormat = SimpleDateFormat("dd");

        if (dayFormat.format(currDate) == dayFormat.format(remindAfter)) {
            remindAtString = "Today, ";
        } else if ("" + ((dayFormat.format(currDate)
                .toInt() + 1)) == dayFormat.format(remindAfter)
        ) {
            remindAtString = "Tomorrow, ";
        } else {
            val dayDiff = (((remindAfter.time - currDate.time) / 1000) / (60 * 60 * 24) + 1)
            remindAtString = "In " + (dayDiff) + " days, ";
        }

        remindAtString += SimpleDateFormat("HH:mm").format(remindAfter);

        mDescriptionTextView.text = description;
        mRemindAfterTextView.text = remindAtString;
    }
}