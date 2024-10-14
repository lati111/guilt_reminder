package com.example.guiltreminder

import android.util.Log
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.util.Date
import kotlin.math.floor
import kotlin.math.round

class ReminderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
    private val mDescriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
    private val mRemindAfterTextView: TextView = itemView.findViewById(R.id.remindAfterTextView)

    // Show the data in the views
    fun bind(reminder: Reminder) {
        val description = reminder.description;
        val remindAfter = reminder.remindAfter;

        val currDate = Date();
        var remindAtString: String = "pain";

        val dayFormat: SimpleDateFormat = SimpleDateFormat("dd");

        if (dayFormat.format(currDate) == dayFormat.format(remindAfter)) {
            remindAtString = "Today, ";
        } else if (""+((dayFormat.format(currDate).toInt() + 1)) == dayFormat.format(remindAfter)) {
            remindAtString = "Tomorrow, ";
        } else {
            val dayDiff = (((remindAfter.time - currDate.time) / 1000) / (60*60*24) + 1)
            remindAtString = "In " + (dayDiff) + " days, ";
        }

        remindAtString += SimpleDateFormat("HH:mm").format(remindAfter);

        mDescriptionTextView.text = description;
        mRemindAfterTextView.text = remindAtString;


//        val name = repo.name
//        val description = repo.description
//        val stargazersCount = repo.stargazersCount
//        val language = repo.language
//        val updatedAt = repo.updatedAt
//        val license = repo.license
//        mRepositoryName.text = name
//        mRepositoryStarCount.text = stargazersCount.toString()
//        mRepositoryUpdatedAt.text = updatedAt

        // Since the data in these can be null we check and bind data
        // or remove the view otherwise
//        bindOrHideTextView(mRepositoryDescription, description)
//        bindOrHideTextView(mRepositoryLanguage, language)
//        bindOrHideTextView(mRepositoryLicense, license)
    }

//    private fun bindOrHideTextView(textView: TextView, data: String?) {
//        if (data == null) {
//            textView.visibility = View.GONE
//        } else {
//            textView.text = data
//            textView.visibility = View.VISIBLE
//        }
//    }
}