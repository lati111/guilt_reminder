package com.ironbrain.guiltreminder

import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.util.Date

class ReminderViewHolder(val activity: OverviewActivity, itemView: View) : RecyclerView.ViewHolder(itemView){
    private val mDescriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val mRemindAfterTextView: TextView = itemView.findViewById(R.id.remindAfterTextView)

    // Show the data in the views
    fun bind(reminder: com.ironbrain.guiltreminder.database.Reminder) {
        val description = reminder.description;
        val remindAfter = reminder.remind_after;

        val currDate = Date();
        var remindAtString: String = "pain";

        val dayFormat: SimpleDateFormat = SimpleDateFormat("dd");

        if (currDate.after(remindAfter) === false) {
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
        } else {
            remindAtString = "Now"
        }

        mDescriptionTextView.text = description;
        mRemindAfterTextView.text = remindAtString;

        val checkbox: CheckBox = itemView.findViewById(R.id.doneCheckbox)
        checkbox.setOnClickListener(View.OnClickListener { view: View ->
            checkbox.isChecked = false;

            val newFragment = FinishReminderDialogFragment(activity, reminder)
            newFragment.show(activity.supportFragmentManager, "game")
        })
    }
}